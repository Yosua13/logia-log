package com.sentinelai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
public class SentinelAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentinelAiApplication.class, args);
	}

}

