package com.logialog.application.port.in;

import com.logialog.domain.model.LogEntry;
import com.logialog.domain.model.LogLevel;
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
