package com.logialog.config;

import java.time.Duration;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Konfigurasi Redis Cache yang dioptimalkan untuk Managed Redis SaaS (Upstash Free Tier).
 * Menggunakan TTL terukur agar hemat memori (256 MB) dan hemat kuota command (500K/bulan).
 */
@Configuration
public class UpstashRedisConfig {

	public static final String CACHE_AI_DIAGNOSTIC_REPORTS = "aiDiagnosticReports";
	public static final String CACHE_ERROR_FINGERPRINTS = "errorFingerprints";

	@Bean
	public RedisCacheManagerBuilderCustomizer upstashRedisCacheCustomizer(LogiaLogProperties properties) {
		Duration aiCooldown = Duration.ofSeconds(
				properties.ai() != null && properties.ai().cooldownSeconds() > 0
						? properties.ai().cooldownSeconds()
						: 900 // 15 menit default
		);
		Duration dedupWindow = Duration.ofSeconds(
				properties.deduplication() != null && properties.deduplication().windowSeconds() > 0
						? properties.deduplication().windowSeconds()
						: 300 // 5 menit default
		);

		RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

		return builder -> builder
				.withCacheConfiguration(
						CACHE_AI_DIAGNOSTIC_REPORTS,
						baseConfig.entryTtl(aiCooldown)
				)
				.withCacheConfiguration(
						CACHE_ERROR_FINGERPRINTS,
						baseConfig.entryTtl(dedupWindow)
				);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(RedisSerializer.string());
		template.setHashKeySerializer(RedisSerializer.string());
		template.setValueSerializer(RedisSerializer.json());
		template.setHashValueSerializer(RedisSerializer.json());
		template.afterPropertiesSet();
		return template;
	}

}
