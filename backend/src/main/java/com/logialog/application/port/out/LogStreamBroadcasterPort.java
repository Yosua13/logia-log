package com.logialog.application.port.out;

import com.logialog.domain.model.LogEntry;

public interface LogStreamBroadcasterPort {

	void broadcast(LogEntry logEntry);

}

