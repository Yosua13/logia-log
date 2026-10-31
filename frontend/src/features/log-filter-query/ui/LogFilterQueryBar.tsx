import type { LogSeverity } from '@/entities/log-entry/model/types'

interface LogFilterQueryBarProps {
  query: string
  selectedLevel: LogSeverity | 'ALL'
  onQueryChange: (value: string) => void
  onLevelChange: (level: LogSeverity | 'ALL') => void
}

const LEVELS: ReadonlyArray<LogSeverity | 'ALL'> = ['ALL', 'INFO', 'WARN', 'ERROR', 'FATAL']

export function LogFilterQueryBar({
  query,
  selectedLevel,
  onQueryChange,
  onLevelChange,
}: LogFilterQueryBarProps) {
  return (
    <div className="flex flex-wrap items-center gap-3">
      <input
        type="text"
        value={query}
        onChange={(e) => onQueryChange(e.target.value)}
        placeholder="Filter service, X-Sentinel-Trace-Id, SQLState, atau Regex..."
        className="min-w-[260px] flex-1 border border-[#9A7B2C] bg-[#0A0A0A] px-3 py-2 font-mono text-xs text-[#F5F5F5] placeholder-[#C5A059]/50 focus:border-[#FFD700] focus:outline-none"
      />
      <div className="flex border border-[#9A7B2C]">
        {LEVELS.map((lvl) => (
          <button
            key={lvl}
            type="button"
            onClick={() => onLevelChange(lvl)}
            className={`cursor-pointer px-3 py-1.5 font-mono text-[11px] font-semibold transition-colors ${
              selectedLevel === lvl
                ? 'bg-[#FFD700] text-[#0A0A0A]'
                : 'bg-[#121212] text-[#C5A059] hover:text-[#FFD700]'
            }`}
          >
            {lvl}
          </button>
        ))}
      </div>
    </div>
  )
}

