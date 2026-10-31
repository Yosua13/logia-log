package com.sentinelai.adapter.in.web;

import com.sentinelai.config.SentinelProperties;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemInfoController {

	private final SentinelProperties properties;

	@GetMapping("/info")
	public Map<String, Object> systemInfo() {
		return Map.of(
				"platform", "SentinelAI v3.1 (Target-Agnostic Enterprise Edition)",
				"architecture", "Clean Hexagonal + Event-Driven",
				"javaVersion", Runtime.version().toString(),
				"jvmName", System.getProperty("java.vm.name", "HotSpot 64-Bit Server VM"),
				"virtualThreadsActive", Thread.currentThread().isVirtual(),
				"springBootVersion", SpringBootVersion.getVersion(),
				"redisMode", "Upstash Serverless Redis SaaS (TLS rediss://)",
				"aiModel", properties.ai().model(),
				"timestamp", Instant.now().toString()
		);
	}

}

