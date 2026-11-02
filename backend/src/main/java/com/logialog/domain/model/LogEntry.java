package com.logialog.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record LogEntry(
		UUID id,
		String serviceName,
		LogLevel level,
		String traceId,
		String fingerprintHash,
		String rawMessage,
		String stackTrace,
		Map<String, Object> metadata,
		long occurrenceCount,
		boolean recurringAnomaly,
		Instant createdAt
) {

	public LogEntry {
		if (id == null) {
			id = UUID.randomUUID();
		}
		if (serviceName == null || serviceName.isBlank()) {
			serviceName = "unknown-service";
		}
		if (level == null) {
			level = LogLevel.INFO;
		}
		if (rawMessage == null) {
			rawMessage = "";
		}
		if (metadata == null) {
			metadata = Map.of();
		} else {
			metadata = Map.copyOf(metadata);
		}
		if (occurrenceCount < 1) {
			occurrenceCount = 1;
		}
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}

}
