import { LOGIA_LOG_STACK } from '@/shared/config/stack'

export function CommandHeader() {
  return (
    <header className="border-b border-[#D4AF37] bg-[#0A0A0A] px-6 py-4">
      <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center border border-[#D4AF37] bg-[#121212] font-mono text-sm font-bold text-[#FFD700]">
            LOGIA
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-base font-bold tracking-widest text-[#D4AF37] uppercase">
                Logia Log
              </h1>
              <span className="border border-[#9A7B2C] bg-[#121212] px-2 py-0.5 font-mono text-[10px] font-semibold text-[#FFD700]">
                v3.1 • TARGET-AGNOSTIC
              </span>
            </div>
            <p className="text-xs text-[#C5A059]/85">
              Unified Observability (Grafana) • API Studio (Postman) • 7-Layer GitHub AI Auditor
            </p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2 font-mono text-[11px]">
          <span className="border border-[#9A7B2C]/70 bg-[#121212] px-2.5 py-1 text-[#C5A059]">
            JVM: <strong className="text-[#FFD700]">Java 25.0.5.0.2 LTS</strong>
          </span>
          <span className="border border-[#9A7B2C]/70 bg-[#121212] px-2.5 py-1 text-[#C5A059]">
            Boot: <strong className="text-[#FFD700]">4.1.2</strong>
          </span>
          <span className="border border-[#9A7B2C]/70 bg-[#121212] px-2.5 py-1 text-[#C5A059]">
            DB: <strong className="text-[#FFD700]">PG 18.4</strong>
          </span>
          <span className="border border-[#9A7B2C]/70 bg-[#121212] px-2.5 py-1 text-[#C5A059]" title={LOGIA_LOG_STACK.redisSaas}>
            Cache: <strong className="text-[#FFD700]">Upstash SaaS</strong>
          </span>
        </div>
      </div>
    </header>
  )
}
