package com.logialog.adapter.in.messaging;

import com.logialog.application.port.in.IngestLogUseCase;
import com.logialog.application.port.in.IngestLogUseCase.IngestLogCommand;
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
			queues = "${logialog.messaging.queue}",
			autoStartup = "${logialog.messaging.consumer-enabled:false}"
	)
	public void onLogMessage(IngestLogCommand payload) {
		log.debug("Consumed log event from RabbitMQ for service={} on virtualThread={}",
				payload.serviceName(), Thread.currentThread().isVirtual());
		ingestLogUseCase.ingest(payload);
	}

}
