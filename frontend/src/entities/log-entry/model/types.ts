export type LogSeverity = 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' | 'FATAL'

export interface LogEntry {
  id: string
  serviceName: string
  level: LogSeverity
  traceId?: string
  fingerprintHash?: string
  rawMessage: string
  stackTrace?: string
  occurrenceCount: number
  recurringAnomaly: boolean
  createdAt: string
}

