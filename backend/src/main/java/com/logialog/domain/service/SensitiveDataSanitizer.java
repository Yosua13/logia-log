package com.logialog.domain.service;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Pure Domain Service (FR-DSI-03): Menyensor data pribadi (PII), token JWT, password,
 * nomor kartu, dan NIK sebelum dikirim ke LLM eksternal menjadi [REDACTED].
 */
@Component
public class SensitiveDataSanitizer {

	private static final Pattern JWT_PATTERN = Pattern.compile(
			"\\beyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\b");
	private static final Pattern BEARER_SECRET_PATTERN = Pattern.compile(
			"(?i)(authorization\\s*[:=]\\s*bearer\\s+)([^\\s,;\"']+)");
	private static final Pattern SECRET_KV_PATTERN = Pattern.compile(
			"(?i)\\b(password|passwd|secret|api[_-]?key|access[_-]?token|client[_-]?secret)\\b(\\s*[:=]\\s*[\"']?)([^\"'\\s,;}]+)");
	private static final Pattern CARD_OR_NIK_PATTERN = Pattern.compile(
			"\\b(?:\\d[ -]*?){13,16}\\b");

	public String sanitize(String input) {
		if (input == null || input.isBlank()) {
			return input;
		}
		String masked = JWT_PATTERN.matcher(input).replaceAll("[REDACTED]");
		masked = BEARER_SECRET_PATTERN.matcher(masked).replaceAll("$1[REDACTED]");
		masked = SECRET_KV_PATTERN.matcher(masked).replaceAll("$1$2[REDACTED]");
		masked = CARD_OR_NIK_PATTERN.matcher(masked).replaceAll("[REDACTED]");
		return masked;
	}

}
