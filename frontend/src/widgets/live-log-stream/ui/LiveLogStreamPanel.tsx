import type { LogEntry } from '@/entities/log-entry/model/types'
import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'
import { SharpButton } from '@/shared/ui/SharpButton'

interface LiveLogStreamPanelProps {
  logs: ReadonlyArray<LogEntry>
  paused: boolean
  onTogglePause: () => void
  onSelectForAudit: (hash: string) => void
}

export function LiveLogStreamPanel({
  logs,
  paused,
  onTogglePause,
  onSelectForAudit,
}: LiveLogStreamPanelProps) {
  return (
    <GoldFrameCard
      title="Live Log Stream (SSE / RabbitMQ Virtual Threads)"
      subtitle="FR-LOG-01 & FR-LOG-04 • Sub-500ms Latency • SHA-256 Stack Trace Deduplication"
      badge={paused ? 'PAUSED' : 'STREAMING'}
    >
      <div className="mb-3 flex items-center justify-between">
        <span className="font-mono text-xs text-[#C5A059]">
          Showing {logs.length} structured events
        </span>
        <SharpButton variant="secondary" onClick={onTogglePause}>
          {paused ? 'Resume Stream' : 'Pause Stream'}
        </SharpButton>
      </div>

      <div className="overflow-x-auto border border-[#9A7B2C]/60 bg-[#0A0A0A]">
        <table className="w-full border-collapse text-left font-mono text-xs">
          <thead>
            <tr className="border-b border-[#D4AF37]/60 bg-[#121212] text-[#D4AF37]">
              <th className="px-3 py-2">TIMESTAMP</th>
              <th className="px-3 py-2">LEVEL</th>
              <th className="px-3 py-2">SERVICE</th>
              <th className="px-3 py-2">MESSAGE &amp; TRACE ID</th>
              <th className="px-3 py-2 text-right">OCCURRENCES</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((item) => (
              <tr
                key={item.id}
                className="border-b border-[#9A7B2C]/25 hover:bg-[#121212]/90"
              >
                <td className="px-3 py-2 whitespace-nowrap text-[#C5A059]/80">
                  {item.createdAt}
                </td>
                <td className="px-3 py-2 font-bold">
                  <span
                    className={
                      item.level === 'ERROR' || item.level === 'FATAL'
                        ? 'text-[#F59E0B]'
                        : 'text-[#D4AF37]'
                    }
                  >
                    {item.level}
                  </span>
                </td>
                <td className="px-3 py-2 text-[#F5F5F5]">{item.serviceName}</td>
                <td className="px-3 py-2 text-[#E5E5E5]">
                  <div>{item.rawMessage}</div>
                  {item.traceId && (
                    <span className="mt-1 inline-block text-[10px] text-[#C5A059]">
                      X-Sentinel-Trace-Id: {item.traceId}
                    </span>
                  )}
                </td>
                <td className="px-3 py-2 text-right whitespace-nowrap">
                  <span className="border border-[#D4AF37]/50 bg-[#121212] px-2 py-0.5 text-[11px] text-[#FFD700]">
                    x{item.occurrenceCount}
                  </span>
                  {item.recurringAnomaly && item.fingerprintHash && (
                    <button
                      type="button"
                      onClick={() => onSelectForAudit(item.fingerprintHash!)}
                      className="ml-2 cursor-pointer bg-[#FFD700] px-2 py-0.5 text-[10px] font-bold text-[#0A0A0A] uppercase"
                    >
                      Recurring • Audit
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </GoldFrameCard>
  )
}

