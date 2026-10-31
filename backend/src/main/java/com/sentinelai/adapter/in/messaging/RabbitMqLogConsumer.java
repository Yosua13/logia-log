package com.sentinelai.adapter.in.messaging;

import com.sentinelai.application.port.in.IngestLogUseCase;
import com.sentinelai.application.port.in.IngestLogUseCase.IngestLogCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqLogConsumer {

	private final IngestLogUseCase ingestLogUseCase;

	@RabbitListener(
			queues = "${sentinel.messaging.queue}",
			autoStartup = "${sentinel.messaging.consumer-enabled:false}"
	)
	public void onLogMessage(IngestLogCommand payload) {
		log.debug("Consumed log event from RabbitMQ for service={} on virtualThread={}",
				payload.serviceName(), Thread.currentThread().isVirtual());
		ingestLogUseCase.ingest(payload);
	}

}

