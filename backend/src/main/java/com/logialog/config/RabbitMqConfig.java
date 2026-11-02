package com.logialog.config;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	@Bean
	public TopicExchange logIngestionExchange(LogiaLogProperties properties) {
		return new TopicExchange(properties.messaging().exchange(), true, false);
	}

	@Bean
	public DirectExchange logDeadLetterExchange(LogiaLogProperties properties) {
		return new DirectExchange(properties.messaging().dlx(), true, false);
	}

	@Bean
	public Queue logIngestionQueue(LogiaLogProperties properties) {
		return QueueBuilder.durable(properties.messaging().queue())
				.withArguments(Map.of(
						"x-dead-letter-exchange", properties.messaging().dlx(),
						"x-dead-letter-routing-key", properties.messaging().dlq(),
						"x-message-ttl", properties.messaging().messageTtlMs()
				))
				.build();
	}

	@Bean
	public Queue logDeadLetterQueue(LogiaLogProperties properties) {
		return QueueBuilder.durable(properties.messaging().dlq()).build();
	}

	@Bean
	public Binding logIngestionBinding(
			Queue logIngestionQueue,
			TopicExchange logIngestionExchange,
			LogiaLogProperties properties
	) {
		return BindingBuilder.bind(logIngestionQueue)
				.to(logIngestionExchange)
				.with(properties.messaging().routingKey());
	}

	@Bean
	public Binding logDeadLetterBinding(
			Queue logDeadLetterQueue,
			DirectExchange logDeadLetterExchange,
			LogiaLogProperties properties
	) {
		return BindingBuilder.bind(logDeadLetterQueue)
				.to(logDeadLetterExchange)
				.with(properties.messaging().dlq());
	}

	@Bean
	public MessageConverter rabbitJsonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}

}
