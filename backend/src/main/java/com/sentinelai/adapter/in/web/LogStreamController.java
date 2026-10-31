package com.sentinelai.adapter.in.web;

import com.sentinelai.adapter.out.sse.SseLogBroadcasterAdapter;
import com.sentinelai.application.port.in.IngestLogUseCase;
import com.sentinelai.application.port.in.IngestLogUseCase.IngestLogCommand;
import com.sentinelai.domain.model.LogEntry;
import com.sentinelai.domain.model.LogLevel;
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
			@RequestHeader(name = "X-Sentinel-Trace-Id", required = false) String headerTraceId
	) {
		String effectiveTraceId = (request.traceId() != null && !request.traceId().isBlank())
				? request.traceId()
				: headerTraceId;

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

