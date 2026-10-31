package com.sentinelai.application.port.in;

import com.sentinelai.domain.model.LogEntry;
import com.sentinelai.domain.model.LogLevel;
import java.util.Map;

public interface IngestLogUseCase {

	LogEntry ingest(IngestLogCommand command);

	record IngestLogCommand(
			String serviceName,
			LogLevel level,
			String traceId,
			String rawMessage,
			String stackTrace,
			Map<String, Object> metadata
	) {
	}

}

