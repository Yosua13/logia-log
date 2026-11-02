package com.logialog.adapter.out.redis;

import com.logialog.application.port.out.ErrorDeduplicationCachePort;
import com.logialog.config.LogiaLogProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Outbound Adapter untuk Managed Redis SaaS (Upstash Serverless Redis via TLS rediss://).
 * Menggunakan INCR atomik + EXPIRE hanya pada hit pertama untuk menghemat kuota command bulanan Upstash (500K/bulan),
 * mekanisme cooldown kunci SET NX EX untuk mencegah Log Flood panggilan LLM/GitHub,
 * serta fallback in-memory yang tangguh (graceful fallback) apabila kredensial Upstash belum diaktifkan
 * atau terjadi gangguan jaringan eksternal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpstashErrorDeduplicationAdapter implements ErrorDeduplicationCachePort {

	private static final String DEDUP_KEY_PREFIX = "logialog:dedup:err:";
	private static final String COOLDOWN_KEY_PREFIX = "logialog:cooldown:ai:";

	private final StringRedisTemplate redisTemplate;
	private final LogiaLogProperties properties;

	private final ConcurrentHashMap<String, AtomicLong> localFallbackCounter = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Instant> localFallbackCooldown = new ConcurrentHashMap<>();

	@Override
	public long incrementAndGetOccurrence(String fingerprintHash, Duration window) {
		if (fingerprintHash == null || fingerprintHash.isBlank()) {
			return 1L;
		}

		Duration effectiveWindow = (window != null && !window.isZero() && !window.isNegative())
				? window
				: Duration.ofSeconds(properties.deduplication() != null ? properties.deduplication().windowSeconds() : 300);

		if (!isRedisEnabled()) {
			return incrementLocalFallback(fingerprintHash);
		}

		try {
			String key = DEDUP_KEY_PREFIX + fingerprintHash;
			Long count = redisTemplate.opsForValue().increment(key);
			if (count != null && count == 1L) {
				redisTemplate.expire(key, effectiveWindow);
			}
			return count != null ? count : 1L;
		} catch (Exception ex) {
			log.warn("Upstash Redis unreachable for deduplication, falling back to in-memory counter: {}", ex.getMessage());
			return incrementLocalFallback(fingerprintHash);
		}
	}

	@Override
	public boolean tryAcquireAiCooldownLock(String fingerprintHash, Duration cooldown) {
		if (fingerprintHash == null || fingerprintHash.isBlank()) {
			return false;
		}

		Duration effectiveCooldown = (cooldown != null && !cooldown.isZero() && !cooldown.isNegative())
				? cooldown
				: Duration.ofSeconds(properties.ai() != null ? properties.ai().cooldownSeconds() : 900);

		if (!isRedisEnabled()) {
			return acquireLocalCooldownLock(fingerprintHash, effectiveCooldown);
		}

		try {
			String key = COOLDOWN_KEY_PREFIX + fingerprintHash;
			Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "ACTIVE", effectiveCooldown);
			return Boolean.TRUE.equals(acquired);
		} catch (Exception ex) {
			log.warn("Upstash Redis unreachable for cooldown lock, falling back to in-memory cooldown: {}", ex.getMessage());
			return acquireLocalCooldownLock(fingerprintHash, effectiveCooldown);
		}
	}

	@Override
	public boolean isAiInCooldown(String fingerprintHash) {
		if (fingerprintHash == null || fingerprintHash.isBlank()) {
			return false;
		}

		if (!isRedisEnabled()) {
			Instant expiry = localFallbackCooldown.get(fingerprintHash);
			return expiry != null && Instant.now().isBefore(expiry);
		}

		try {
			String key = COOLDOWN_KEY_PREFIX + fingerprintHash;
			Boolean exists = redisTemplate.hasKey(key);
			return Boolean.TRUE.equals(exists);
		} catch (Exception ex) {
			log.warn("Upstash Redis unreachable for cooldown check, falling back to in-memory check: {}", ex.getMessage());
			Instant expiry = localFallbackCooldown.get(fingerprintHash);
			return expiry != null && Instant.now().isBefore(expiry);
		}
	}

	@Override
	public void reset(String fingerprintHash) {
		if (fingerprintHash == null || fingerprintHash.isBlank()) {
			return;
		}

		localFallbackCounter.remove(fingerprintHash);
		localFallbackCooldown.remove(fingerprintHash);

		if (isRedisEnabled()) {
			try {
				redisTemplate.delete(List.of(
						DEDUP_KEY_PREFIX + fingerprintHash,
						COOLDOWN_KEY_PREFIX + fingerprintHash
				));
			} catch (Exception ex) {
				log.warn("Failed to reset keys in Upstash Redis: {}", ex.getMessage());
			}
		}
	}

	private boolean isRedisEnabled() {
		return properties.deduplication() != null && properties.deduplication().redisEnabled();
	}

	private long incrementLocalFallback(String fingerprintHash) {
		return localFallbackCounter
				.computeIfAbsent(fingerprintHash, ignored -> new AtomicLong(0))
				.incrementAndGet();
	}

	private boolean acquireLocalCooldownLock(String fingerprintHash, Duration cooldown) {
		Instant now = Instant.now();
		AtomicBoolean acquired = new AtomicBoolean(false);
		localFallbackCooldown.compute(fingerprintHash, (k, currentExpiry) -> {
			if (currentExpiry == null || now.isAfter(currentExpiry)) {
				acquired.set(true);
				return now.plus(cooldown);
			}
			return currentExpiry;
		});
		return acquired.get();
	}

}
