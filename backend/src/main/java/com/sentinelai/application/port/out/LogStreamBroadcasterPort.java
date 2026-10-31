package com.sentinelai.application.port.out;

import com.sentinelai.domain.model.LogEntry;

public interface LogStreamBroadcasterPort {

	void broadcast(LogEntry logEntry);

}

