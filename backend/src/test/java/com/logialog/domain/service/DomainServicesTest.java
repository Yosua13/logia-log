package com.logialog.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainServicesTest {

	private final StackTraceFingerprinter fingerprinter = new StackTraceFingerprinter();
	private final SensitiveDataSanitizer sanitizer = new SensitiveDataSanitizer();

	@Test
	void fingerprinterProducesIdenticalHashAcrossDynamicTimestampsAndUuids() {
		String trace1 = "2026-11-02T10:00:00Z ERROR Order 550e8400-e29b-41d4-a716-446655440000 failed at com.app.OrderService@1a2b3c4d";
		String trace2 = "2026-11-02T11:45:12Z ERROR Order 123e4567-e89b-12d3-a456-426614174000 failed at com.app.OrderService@9f8e7d6c";

		String hash1 = fingerprinter.computeFingerprint("order-service", "Order failed", trace1);
		String hash2 = fingerprinter.computeFingerprint("order-service", "Order failed", trace2);

		assertThat(hash1)
				.hasSize(64)
				.isEqualTo(hash2);
	}

	@Test
	void sanitizerRedactsPasswordsJwtAndCardNumbers() {
		String raw = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.c2lnbmF0dXJl1234 "
				+ "password=superSecret123 card=4532-1111-2222-3333";

		String sanitized = sanitizer.sanitize(raw);

		assertThat(sanitized)
				.doesNotContain("superSecret123")
				.doesNotContain("4532-1111-2222-3333")
				.doesNotContain("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
				.contains("[REDACTED]");
	}

}
