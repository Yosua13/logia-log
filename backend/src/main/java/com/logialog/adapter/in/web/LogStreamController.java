package com.logialog.adapter.in.web;

import com.logialog.adapter.out.sse.SseLogBroadcasterAdapter;
import com.logialog.application.port.in.IngestLogUseCase;
import com.logialog.application.port.in.IngestLogUseCase.IngestLogCommand;
import com.logialog.domain.model.LogEntry;
import com.logialog.domain.model.LogLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogStreamController {

	private final IngestLogUseCase ingestLogUseCase;
	private final SseLogBroadcasterAdapter sseBroadcaster;

	@GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamLogs() {
		return sseBroadcaster.subscribe();
	}

	@PostMapping("/ingest")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public LogEntry ingestLog(
			@Valid @RequestBody LogIngestRequest request,
			@RequestHeader(name = "X-Logia-Trace-Id", required = false) String logiaTraceId,
			@RequestHeader(name = "X-Sentinel-Trace-Id", required = false) String legacyTraceId
	) {
		String headerTrace = (logiaTraceId != null && !logiaTraceId.isBlank()) ? logiaTraceId : legacyTraceId;
		String effectiveTraceId = (request.traceId() != null && !request.traceId().isBlank())
				? request.traceId()
				: headerTrace;

		return ingestLogUseCase.ingest(new IngestLogCommand(
				request.serviceName(),
				request.level() != null ? request.level() : LogLevel.INFO,
				effectiveTraceId,
				request.rawMessage(),
				request.stackTrace(),
				request.metadata()
		));
	}

	public record LogIngestRequest(
			@NotBlank String serviceName,
			LogLevel level,
			String traceId,
			@NotBlank String rawMessage,
			String stackTrace,
			Map<String, Object> metadata
	) {
	}

}
