package com.sentinelai.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentinel")
public record SentinelProperties(
		Cors cors,
		Messaging messaging,
		Deduplication deduplication,
		Ai ai
) {

	public record Cors(List<String> allowedOrigins) {
	}

	public record Messaging(
			boolean consumerEnabled,
			String exchange,
			String queue,
			String routingKey,
			String dlx,
			String dlq,
			long messageTtlMs
	) {
	}

	public record Deduplication(
			boolean redisEnabled,
			long windowSeconds,
			long recurringThreshold
	) {
	}

	public record Ai(
			long cooldownSeconds,
			String provider,
			String model
	) {
	}

}

