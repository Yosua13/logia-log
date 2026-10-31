package com.sentinelai.application.port.out;

import java.time.Duration;

public interface ErrorDeduplicationCachePort {

	long incrementAndGetOccurrence(String fingerprintHash, Duration window);

}

