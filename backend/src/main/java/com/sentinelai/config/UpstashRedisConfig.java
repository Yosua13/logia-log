package com.sentinelai.config;

import java.time.Duration;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

/**
 * Konfigurasi Redis Cache yang dioptimalkan untuk Managed Redis SaaS (Upstash Free Tier).
 * Menggunakan TTL terukur agar hemat memori (256 MB) dan hemat kuota command (500K/bulan).
 */
@Configuration
public class UpstashRedisConfig {

	@Bean
	public RedisCacheManagerBuilderCustomizer upstashRedisCacheCustomizer(SentinelProperties properties) {
		Duration aiCooldown = Duration.ofSeconds(properties.ai().cooldownSeconds());
		Duration dedupWindow = Duration.ofSeconds(properties.deduplication().windowSeconds());

		return builder -> builder
				.withCacheConfiguration(
						"aiDiagnosticReports",
						RedisCacheConfiguration.defaultCacheConfig()
								.disableCachingNullValues()
								.entryTtl(aiCooldown)
				)
				.withCacheConfiguration(
						"errorFingerprints",
						RedisCacheConfiguration.defaultCacheConfig()
								.disableCachingNullValues()
								.entryTtl(dedupWindow)
				);
	}

}

