package com.logialog.adapter.out.redis;

import com.logialog.application.port.out.ErrorDeduplicationCachePort;
import com.logialog.config.LogiaLogProperties;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Outbound Adapter untuk Managed Redis SaaS (Upstash Serverless Redis via TLS rediss://).
 * Menggunakan INCR atomik + EXPIRE hanya pada hit pertama untuk menghemat kuota command bulanan Upstash,
 * serta fallback in-memory apabila kredensial Upstash belum diaktifkan pada mode dev lokal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpstashErrorDeduplicationAdapter implements ErrorDeduplicationCachePort {

	private static final String KEY_PREFIX = "logialog:dedup:err:";

	private final StringRedisTemplate redisTemplate;
	private final LogiaLogProperties properties;
	private final ConcurrentHashMap<String, AtomicLong> localFallbackCounter = new ConcurrentHashMap<>();

	@Override
	public long incrementAndGetOccurrence(String fingerprintHash, Duration window) {
		if (!properties.deduplication().redisEnabled()) {
			return localFallbackCounter
					.computeIfAbsent(fingerprintHash, ignored -> new AtomicLong(0))
					.incrementAndGet();
		}

		try {
			String key = KEY_PREFIX + fingerprintHash;
			Long count = redisTemplate.opsForValue().increment(key);
			if (count != null && count == 1L) {
				redisTemplate.expire(key, window);
			}
			return count != null ? count : 1L;
		} catch (Exception ex) {
			log.warn("Upstash Redis unreachable, falling back to in-memory counter: {}", ex.getMessage());
			return localFallbackCounter
					.computeIfAbsent(fingerprintHash, ignored -> new AtomicLong(0))
					.incrementAndGet();
		}
	}

}

