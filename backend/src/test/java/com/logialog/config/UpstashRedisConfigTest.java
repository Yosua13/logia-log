package com.logialog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

class UpstashRedisConfigTest {

	private final UpstashRedisConfig config = new UpstashRedisConfig();

	@Test
	@DisplayName("Configures aiDiagnosticReports (15 min) and errorFingerprints (5 min) TTLs with null-values disabled")
	void customizesCacheManagerWithSpecificTtls() {
		LogiaLogProperties properties = new LogiaLogProperties(
				null,
				null,
				new LogiaLogProperties.Deduplication(true, 300, 3),
				new LogiaLogProperties.Ai(900, "gemini", "gemini-2.5-pro")
		);

		RedisCacheManagerBuilderCustomizer customizer = config.upstashRedisCacheCustomizer(properties);
		assertThat(customizer).isNotNull();

		RedisCacheManager.RedisCacheManagerBuilder builder = Mockito.mock(RedisCacheManager.RedisCacheManagerBuilder.class);
		Mockito.when(builder.withCacheConfiguration(Mockito.anyString(), Mockito.any(RedisCacheConfiguration.class)))
				.thenReturn(builder);

		customizer.customize(builder);

		ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<RedisCacheConfiguration> configCaptor = ArgumentCaptor.forClass(RedisCacheConfiguration.class);

		Mockito.verify(builder, Mockito.times(2)).withCacheConfiguration(nameCaptor.capture(), configCaptor.capture());

		assertThat(nameCaptor.getAllValues()).containsExactly(
				UpstashRedisConfig.CACHE_AI_DIAGNOSTIC_REPORTS,
				UpstashRedisConfig.CACHE_ERROR_FINGERPRINTS
		);

		RedisCacheConfiguration aiConfig = configCaptor.getAllValues().get(0);
		RedisCacheConfiguration dedupConfig = configCaptor.getAllValues().get(1);

		assertThat(aiConfig.getTtlFunction().getTimeToLive("anyKey", null)).isEqualTo(Duration.ofSeconds(900));
		assertThat(aiConfig.getAllowCacheNullValues()).isFalse();

		assertThat(dedupConfig.getTtlFunction().getTimeToLive("anyKey", null)).isEqualTo(Duration.ofSeconds(300));
		assertThat(dedupConfig.getAllowCacheNullValues()).isFalse();
	}

	@Test
	@DisplayName("Configures RedisTemplate with key and value serializers")
	void configuresRedisTemplateBean() {
		RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);
		RedisTemplate<String, Object> template = config.redisTemplate(connectionFactory);

		assertThat(template).isNotNull();
		assertThat(template.getConnectionFactory()).isEqualTo(connectionFactory);
		assertThat(template.getKeySerializer()).isNotNull();
		assertThat(template.getValueSerializer()).isNotNull();
	}

}
