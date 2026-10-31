import { useMemo, useState } from 'react'
import type { LogEntry, LogSeverity } from '@/entities/log-entry/model/types'
import { LogFilterQueryBar } from '@/features/log-filter-query/ui/LogFilterQueryBar'
import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'
import { LiveLogStreamPanel } from '@/widgets/live-log-stream/ui/LiveLogStreamPanel'

const INITIAL_LOGS: ReadonlyArray<LogEntry> = [
  {
    id: '1',
    serviceName: 'order-service',
    level: 'ERROR',
    traceId: 'sntl-019a39f0-7b2c-7000-9f1a-882dc83e96a1',
    fingerprintHash: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    rawMessage: 'PSQLException: ERROR: value too long for type character varying(50) [SQLState: 22001]',
    occurrenceCount: 5,
    recurringAnomaly: true,
    createdAt: '2026-10-31T11:48:10Z',
  },
  {
    id: '2',
    serviceName: 'payment-gateway-adapter',
    level: 'WARN',
    traceId: 'sntl-019a39ef-4410-7000-8a11-112233445566',
    rawMessage: 'External HTTP call detected inside active @Transactional boundary (HikariPool hold=1840ms)',
    occurrenceCount: 2,
    recurringAnomaly: false,
    createdAt: '2026-10-31T11:47:54Z',
  },
  {
    id: '3',
    serviceName: 'sentinelai-backend',
    level: 'INFO',
    rawMessage: 'Upstash Redis SaaS deduplication window active (TLS rediss:// • Virtual Threads enabled)',
    occurrenceCount: 1,
    recurringAnomaly: false,
    createdAt: '2026-10-31T11:47:01Z',
  },
]

interface LogExplorerPageProps {
  onNavigateAudit: (fingerprintHash: string) => void
}

export function LogExplorerPage({ onNavigateAudit }: LogExplorerPageProps) {
  const [query, setQuery] = useState('')
  const [selectedLevel, setSelectedLevel] = useState<LogSeverity | 'ALL'>('ALL')
  const [paused, setPaused] = useState(false)

  const filteredLogs = useMemo(() => {
    return INITIAL_LOGS.filter((log) => {
      const matchesLevel = selectedLevel === 'ALL' || log.level === selectedLevel
      const q = query.trim().toLowerCase()
      const matchesQuery =
        !q ||
        log.serviceName.toLowerCase().includes(q) ||
        log.rawMessage.toLowerCase().includes(q) ||
        (log.traceId?.toLowerCase().includes(q) ?? false)
      return matchesLevel && matchesQuery
    })
  }, [query, selectedLevel])

  return (
    <div className="space-y-5">
      <GoldFrameCard
        title="Grafana-Style Log Query & Histogram Explorer"
        subtitle="FR-LOG-02 & FR-LOG-03 • PostgreSQL 18.4 Partitioned JSONB + GIN Index"
        badge="REAL-TIME"
        tacticalCorners={false}
      >
        <LogFilterQueryBar
          query={query}
          selectedLevel={selectedLevel}
          onQueryChange={setQuery}
          onLevelChange={setSelectedLevel}
        />
      </GoldFrameCard>

      <LiveLogStreamPanel
        logs={filteredLogs}
        paused={paused}
        onTogglePause={() => setPaused((p) => !p)}
        onSelectForAudit={onNavigateAudit}
      />
    </div>
  )
}

