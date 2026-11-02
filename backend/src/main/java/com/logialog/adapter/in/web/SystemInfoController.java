package com.logialog.adapter.in.web;

import com.logialog.config.LogiaLogProperties;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemInfoController {

	private final LogiaLogProperties properties;
	private final Environment environment;

	@GetMapping("/info")
	public Map<String, Object> systemInfo() {
		boolean isVirtualThread = Thread.currentThread().isVirtual();
		boolean virtualThreadsConfigured = Boolean.parseBoolean(environment.getProperty("spring.threads.virtual.enabled", "false"));

		return Map.of(
				"platform", "Logia Log (Target-Agnostic Enterprise Edition)",
				"architecture", "Clean Hexagonal + Event-Driven",
				"javaVersion", Runtime.version().toString(),
				"jvmName", System.getProperty("java.vm.name", "HotSpot 64-Bit Server VM"),
				"virtualThreadsActive", isVirtualThread || virtualThreadsConfigured,
				"springBootVersion", SpringBootVersion.getVersion(),
				"redisMode", "Upstash Serverless Redis SaaS (TLS rediss://)",
				"aiModel", properties.ai().model(),
				"timestamp", Instant.now().toString()
		);
	}

}
