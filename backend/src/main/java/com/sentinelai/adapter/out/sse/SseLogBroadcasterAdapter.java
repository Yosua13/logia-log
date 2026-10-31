package com.sentinelai.adapter.out.sse;

import com.sentinelai.application.port.out.LogStreamBroadcasterPort;
import com.sentinelai.domain.model.LogEntry;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseLogBroadcasterAdapter implements LogStreamBroadcasterPort {

	private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SseEmitter subscribe() {
		SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
		emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError(ignored -> emitters.remove(emitter));
		return emitter;
	}

	@Override
	public void broadcast(LogEntry logEntry) {
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event()
						.name("log-entry")
						.id(logEntry.id().toString())
						.data(logEntry, MediaType.APPLICATION_JSON));
			} catch (IOException ex) {
				emitters.remove(emitter);
			}
		}
	}

}

