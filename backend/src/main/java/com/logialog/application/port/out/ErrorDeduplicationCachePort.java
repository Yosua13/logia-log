package com.logialog.application.port.out;

import java.time.Duration;

public interface ErrorDeduplicationCachePort {

	/**
	 * Increments the occurrence counter for the specified error fingerprint hash.
	 * Atomically sets the expiration TTL on the first occurrence within the window.
	 *
	 * @param fingerprintHash SHA-256 error fingerprint
	 * @param window deduplication time window (e.g., 5 minutes)
	 * @return incremented occurrence count
	 */
	long incrementAndGetOccurrence(String fingerprintHash, Duration window);

	/**
	 * Attempts to acquire an AI diagnostic trigger lock/cooldown for a specific error fingerprint.
	 * Employs Redis atomic SET NX EX semantics so that an error only triggers an LLM/GitHub audit
	 * at most once per cooldown window (mitigating Log Flood risk per PRD Section 09).
	 *
	 * @param fingerprintHash SHA-256 error fingerprint
	 * @param cooldown cooldown duration (e.g., 15 minutes)
	 * @return true if the lock was successfully acquired, false if still in cooldown
	 */
	boolean tryAcquireAiCooldownLock(String fingerprintHash, Duration cooldown);

	/**
	 * Checks if an AI diagnostic trigger is currently locked/in cooldown for the specified error fingerprint.
	 *
	 * @param fingerprintHash SHA-256 error fingerprint
	 * @return true if currently in cooldown
	 */
	boolean isAiInCooldown(String fingerprintHash);

	/**
	 * Clears the deduplication counter and cooldown lock for testing and maintenance.
	 *
	 * @param fingerprintHash SHA-256 error fingerprint
	 */
	void reset(String fingerprintHash);

}
