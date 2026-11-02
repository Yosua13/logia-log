package com.logialog.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SystemInfoControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Test
	@DisplayName("GET /api/v1/system/info returns Java 25.x, Spring Boot 4.1.2, and virtualThreadsActive=true")
	void shouldReturnSystemInfoWithJava25SpringBoot412AndVirtualThreads() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/api/v1/system/info"))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(200);
		String body = response.body();
		assertThat(body).isNotNull();

		// Acceptance Criteria 2 validations:
		// 1. javaVersion starts with "25."
		// 2. springBootVersion is "4.1.2"
		// 3. virtualThreadsActive is true
		assertThat(body).contains("\"virtualThreadsActive\":true");
		assertThat(body).contains("\"springBootVersion\":\"4.1.2\"");
		assertThat(body).containsPattern("\"javaVersion\":\"25\\.");

		// Architectural invariants
		assertThat(body).contains("Logia Log");
		assertThat(body).contains("Clean Hexagonal + Event-Driven");
	}

}
