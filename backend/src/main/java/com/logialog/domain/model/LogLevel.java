package com.logialog.domain.model;

public enum LogLevel {
	TRACE,
	DEBUG,
	INFO,
	WARN,
	ERROR,
	FATAL;

	public boolean isErrorSeverity() {
		return this == ERROR || this == FATAL;
	}
}
