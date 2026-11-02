package com.logialog.domain.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Pure Domain Service (FR-LOG-04): Menghitung SHA-256 fingerprint dari stack trace
 * dengan menormalisasi timestamp, UUID, memory reference, dan parameter dinamis.
 */
@Component
public class StackTraceFingerprinter {

	private static final Pattern UUID_PATTERN = Pattern.compile(
			"\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b");
	private static final Pattern HEX_ADDRESS_PATTERN = Pattern.compile("@[0-9a-fA-F]{5,16}\\b");
	private static final Pattern ISO_TIMESTAMP_PATTERN = Pattern.compile(
			"\\d{4}-\\d{2}-\\d{2}[T\\s]\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:?\\d{2})?");
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

	public String computeFingerprint(String serviceName, String rawMessage, String stackTrace) {
		String source = (stackTrace != null && !stackTrace.isBlank()) ? stackTrace : rawMessage;
		String normalized = normalize(source == null ? "" : source);
		String canonical = (serviceName == null ? "unknown" : serviceName.trim().toLowerCase()) + "::" + normalized;

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashBytes);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 algorithm must be available in Java 25 runtime", ex);
		}
	}

	String normalize(String text) {
		String step = ISO_TIMESTAMP_PATTERN.matcher(text).replaceAll("<TS>");
		step = UUID_PATTERN.matcher(step).replaceAll("<UUID>");
		step = HEX_ADDRESS_PATTERN.matcher(step).replaceAll("@<ADDR>");
		return WHITESPACE_PATTERN.matcher(step.trim()).replaceAll(" ");
	}

}
