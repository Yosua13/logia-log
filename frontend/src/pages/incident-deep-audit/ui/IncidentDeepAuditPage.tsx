import type { AuditDimensionItem } from '@/entities/diagnostic-report/model/types'
import { TriggerAiDeepAuditButton } from '@/features/trigger-ai-audit/ui/TriggerAiDeepAuditButton'
import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'

const SEVEN_LAYERS: ReadonlyArray<AuditDimensionItem> = [
  {
    layerNumber: 1,
    dimension: '1. Syntax & Annotations',
    summary: 'Validasi Bean Validation (@NotBlank, @Size) antara RequestDTO vs JPA Entity pada Java 25.',
    actionableFix: 'Tambahkan @Size(max = 50) pada field customerReference di CreateOrderRequest.',
  },
  {
    layerNumber: 2,
    dimension: '2. Query & DB Tuning',
    summary: 'Kecocokan DDL PostgreSQL 18.4, deteksi N+1 Query, dan evaluasi indeks GIN/B-Tree.',
    actionableFix: 'Gunakan JOIN FETCH pada relasi items atau sesuaikan panjang kolom VARCHAR(150).',
  },
  {
    layerNumber: 3,
    dimension: '3. Business Logic & Edge Cases',
    summary: 'Pemeriksaan null-safety dan akurasi kalkulasi finansial (BigDecimal vs Double).',
    actionableFix: 'Ganti tipe Double menjadi BigDecimal dengan RoundingMode.HALF_EVEN.',
  },
  {
    layerNumber: 4,
    dimension: '4. Version Compatibility',
    summary: 'Audit pom.xml / package.json terhadap Java 25.0.5.0.2 LTS, Spring Boot 4.1.2 & Jakarta EE 11.',
    actionableFix: 'Pastikan seluruh starter mengikuti modul resmi Spring Boot 4.1.2.',
  },
  {
    layerNumber: 5,
    dimension: '5. Execution & Transaction Flow',
    summary: 'Evaluasi batas @Transactional agar I/O HTTP eksternal tidak menahan koneksi HikariCP.',
    actionableFix: 'Pindahkan pemanggilan REST eksternal keluar dari metode @Transactional.',
  },
  {
    layerNumber: 6,
    dimension: '6. Memory Leak & Resource Safety',
    summary: 'Deteksi kebocoran stream, static Map tanpa batas, dan ThreadLocal pada Virtual Threads.',
    actionableFix: 'Gunakan try-with-resources dan ScopedValue pada Java 25.',
  },
  {
    layerNumber: 7,
    dimension: '7. Security Vulnerability Audit',
    summary: 'Pemindaian SQL Injection, Mass Assignment, JWT validation, dan PII masking.',
    actionableFix: 'Gunakan named parameter (:param) dan tutup eksposur stack trace mentah.',
  },
]

interface IncidentDeepAuditPageProps {
  activeFingerprint: string
}

export function IncidentDeepAuditPage({ activeFingerprint }: IncidentDeepAuditPageProps) {
  return (
    <div className="space-y-5">
      <GoldFrameCard
        title="7-Layer Deep GitHub Code Analysis & AI Resolution Engine"
        subtitle={`Target Fingerprint: ${activeFingerprint}`}
        badge="SLA < 5S"
      >
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <p className="text-xs text-[#C5A059]">
            Menggabungkan Stack Trace tersanitasi (`[REDACTED]`), DDL Read-Only PostgreSQL 18.4, dan
            kode sumber branch/commit GitHub secara paralel.
          </p>
          <TriggerAiDeepAuditButton fingerprintHash={activeFingerprint} />
        </div>

        <div className="grid gap-3 md:grid-cols-2">
          {SEVEN_LAYERS.map((layer) => (
            <div
              key={layer.layerNumber}
              className="border border-[#9A7B2C]/60 bg-[#121212] p-3.5"
            >
              <h3 className="font-mono text-xs font-bold text-[#FFD700] uppercase">
                {layer.dimension}
              </h3>
              <p className="mt-1 text-xs text-[#E5E5E5]">{layer.summary}</p>
              <p className="mt-2 border-l-2 border-[#D4AF37] pl-2 font-mono text-[11px] text-[#C5A059]">
                Fix: {layer.actionableFix}
              </p>
            </div>
          ))}
        </div>
      </GoldFrameCard>
    </div>
  )
}

