package com.logialog.application.usecase;

import com.logialog.application.port.in.IngestLogUseCase;
import com.logialog.application.port.out.ErrorDeduplicationCachePort;
import com.logialog.application.port.out.LogStreamBroadcasterPort;
import com.logialog.config.LogiaLogProperties;
import com.logialog.domain.model.LogEntry;
import com.logialog.domain.model.LogLevel;
import com.logialog.domain.service.SensitiveDataSanitizer;
import com.logialog.domain.service.StackTraceFingerprinter;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogIngestionService implements IngestLogUseCase {

	private final StackTraceFingerprinter fingerprinter;
	private final SensitiveDataSanitizer sanitizer;
	private final ErrorDeduplicationCachePort deduplicationCachePort;
	private final LogStreamBroadcasterPort broadcasterPort;
	private final LogiaLogProperties properties;

	@Override
	public LogEntry ingest(IngestLogCommand command) {
		LogLevel level = command.level() != null ? command.level() : LogLevel.INFO;
		String sanitizedMessage = sanitizer.sanitize(command.rawMessage());
		String sanitizedStack = sanitizer.sanitize(command.stackTrace());

		String fingerprint = null;
		long occurrences = 1L;
		boolean recurringAnomaly = false;

		if (level.isErrorSeverity() || (sanitizedStack != null && !sanitizedStack.isBlank())) {
			fingerprint = fingerprinter.computeFingerprint(command.serviceName(), sanitizedMessage, sanitizedStack);
			Duration window = Duration.ofSeconds(properties.deduplication().windowSeconds());
			occurrences = deduplicationCachePort.incrementAndGetOccurrence(fingerprint, window);
			recurringAnomaly = occurrences >= properties.deduplication().recurringThreshold();
		}

		LogEntry entry = new LogEntry(
				UUID.randomUUID(),
				command.serviceName(),
				level,
				command.traceId(),
				fingerprint,
				sanitizedMessage,
				sanitizedStack,
				command.metadata(),
				occurrences,
				recurringAnomaly,
				Instant.now()
		);

		broadcasterPort.broadcast(entry);
		return entry;
	}

}

