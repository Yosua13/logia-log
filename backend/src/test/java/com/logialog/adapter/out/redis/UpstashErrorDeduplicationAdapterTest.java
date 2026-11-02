package com.logialog.adapter.out.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.logialog.config.LogiaLogProperties;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class UpstashErrorDeduplicationAdapterTest {

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	private LogiaLogProperties enabledProperties;
	private LogiaLogProperties disabledProperties;

	@BeforeEach
	void setUp() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		enabledProperties = new LogiaLogProperties(
				null,
				null,
				new LogiaLogProperties.Deduplication(true, 300, 3),
				new LogiaLogProperties.Ai(900, "gemini", "gemini-2.5-pro")
		);

		disabledProperties = new LogiaLogProperties(
				null,
				null,
				new LogiaLogProperties.Deduplication(false, 300, 3),
				new LogiaLogProperties.Ai(900, "gemini", "gemini-2.5-pro")
		);
	}

	@Nested
	@DisplayName("Redis Enabled Mode (Upstash Free Tier Command Efficiency)")
	class RedisEnabledTests {

		@Test
		@DisplayName("Hit 1: INCR returns 1 -> sets EXPIRE (Exactly 2 Redis commands to initialize counter)")
		void hitOneSetsExpiration() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-abc-123";
			String expectedKey = "logialog:dedup:err:" + hash;

			when(valueOperations.increment(expectedKey)).thenReturn(1L);

			long count = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));

			assertThat(count).isEqualTo(1L);
			verify(valueOperations, times(1)).increment(expectedKey);
			verify(redisTemplate, times(1)).expire(expectedKey, Duration.ofSeconds(300));
		}

		@Test
		@DisplayName("Hit 2+: INCR returns 2 -> does NOT set EXPIRE (Conserves 500K monthly command quota)")
		void hitTwoDoesNotSetExpirationAgain() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-abc-123";
			String expectedKey = "logialog:dedup:err:" + hash;

			when(valueOperations.increment(expectedKey)).thenReturn(2L);

			long count = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));

			assertThat(count).isEqualTo(2L);
			verify(valueOperations, times(1)).increment(expectedKey);
			verify(redisTemplate, never()).expire(eq(expectedKey), any(Duration.class));
		}

		@Test
		@DisplayName("AI Cooldown Lock: SET NX EX acquires lock once and rejects concurrent/repeated attempts")
		void aiCooldownLockAcquisitionAndRejection() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-sql-syntax-err";
			String expectedKey = "logialog:cooldown:ai:" + hash;

			when(valueOperations.setIfAbsent(expectedKey, "ACTIVE", Duration.ofSeconds(900)))
					.thenReturn(true)
					.thenReturn(false);

			boolean firstAttempt = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));
			boolean secondAttempt = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));

			assertThat(firstAttempt).isTrue();
			assertThat(secondAttempt).isFalse();
			verify(valueOperations, times(2)).setIfAbsent(expectedKey, "ACTIVE", Duration.ofSeconds(900));
		}

		@Test
		@DisplayName("isAiInCooldown checks key existence via hasKey")
		void isAiInCooldownChecksExistence() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-oom-leak";
			String expectedKey = "logialog:cooldown:ai:" + hash;

			when(redisTemplate.hasKey(expectedKey)).thenReturn(true);

			assertThat(adapter.isAiInCooldown(hash)).isTrue();
			verify(redisTemplate, times(1)).hasKey(expectedKey);
		}

		@Test
		@DisplayName("Reset deletes both dedup and cooldown keys from Redis")
		void resetDeletesKeys() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-clean";

			adapter.reset(hash);

			verify(redisTemplate, times(1)).delete(List.of(
					"logialog:dedup:err:hash-clean",
					"logialog:cooldown:ai:hash-clean"
			));
		}
	}

	@Nested
	@DisplayName("Graceful Fallback on Redis Network Disconnection / Outage")
	class GracefulFallbackTests {

		@Test
		@DisplayName("Falls back to in-memory counter when Upstash Redis throws Connection Exception")
		void fallsBackToInMemoryCounterOnRedisException() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-unreachable";
			String expectedKey = "logialog:dedup:err:" + hash;

			when(valueOperations.increment(expectedKey))
					.thenThrow(new RedisConnectionFailureException("Connection refused to rediss://upstash.io:6379"));

			// First event falls back to local in-memory counter
			long count1 = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));
			// Second event continues using local counter
			long count2 = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));

			assertThat(count1).isEqualTo(1L);
			assertThat(count2).isEqualTo(2L);
		}

		@Test
		@DisplayName("Falls back to in-memory cooldown lock when Upstash Redis throws Connection Exception")
		void fallsBackToInMemoryCooldownOnRedisException() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);
			String hash = "hash-ai-fallback";
			String expectedKey = "logialog:cooldown:ai:" + hash;

			when(valueOperations.setIfAbsent(expectedKey, "ACTIVE", Duration.ofSeconds(900)))
					.thenThrow(new RedisConnectionFailureException("TLS handshake timeout to Upstash"));

			boolean first = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));
			boolean second = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));

			assertThat(first).isTrue();
			assertThat(second).isFalse();
		}
	}

	@Nested
	@DisplayName("Offline / Disabled Mode (deduplication.redis-enabled=false)")
	class DisabledModeTests {

		@Test
		@DisplayName("Operates purely in-memory without invoking StringRedisTemplate")
		void operatesPurelyInMemoryWhenDisabled() {
			UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, disabledProperties);
			String hash = "hash-offline";

			long c1 = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));
			long c2 = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));
			long c3 = adapter.incrementAndGetOccurrence(hash, Duration.ofSeconds(300));

			assertThat(c1).isEqualTo(1L);
			assertThat(c2).isEqualTo(2L);
			assertThat(c3).isEqualTo(3L);

			boolean lock1 = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));
			boolean lock2 = adapter.tryAcquireAiCooldownLock(hash, Duration.ofSeconds(900));

			assertThat(lock1).isTrue();
			assertThat(lock2).isFalse();

			verify(valueOperations, never()).increment(anyString());
			verify(valueOperations, never()).setIfAbsent(anyString(), anyString(), any(Duration.class));
		}
	}

	@Test
	@DisplayName("Handles null or blank fingerprint gracefully")
	void handlesNullOrBlankFingerprint() {
		UpstashErrorDeduplicationAdapter adapter = new UpstashErrorDeduplicationAdapter(redisTemplate, enabledProperties);

		assertThat(adapter.incrementAndGetOccurrence(null, Duration.ofSeconds(300))).isEqualTo(1L);
		assertThat(adapter.incrementAndGetOccurrence("   ", Duration.ofSeconds(300))).isEqualTo(1L);
		assertThat(adapter.tryAcquireAiCooldownLock(null, Duration.ofSeconds(900))).isFalse();
		assertThat(adapter.isAiInCooldown("")).isFalse();
	}

}

