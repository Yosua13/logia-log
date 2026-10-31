import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'
import { ApiRequestBuilder } from '@/widgets/api-request-builder/ui/ApiRequestBuilder'

export function ApiTestingStudioPage() {
  return (
    <div className="space-y-5">
      <ApiRequestBuilder />

      <GoldFrameCard
        title="Response Inspector & Trace-ID Log Correlation"
        subtitle="FR-API-06 & FR-API-07 • Pretty / Raw / Headers + Direct Jump to Server Stack Trace"
        badge="READY"
      >
        <p className="font-mono text-xs text-[#C5A059]">
          Kirim request HTTP ke target REST API mana pun (Plug-and-Play). Apabila respons menghasilkan
          HTTP 4xx/5xx dari layanan yang mengirim log ke SentinelAI, tombol korelasi otomatis akan
          menghubungkan <code className="text-[#FFD700]">X-Sentinel-Trace-Id</code> dengan log server &amp;
          audit GitHub AI.
        </p>
      </GoldFrameCard>
    </div>
  )
}

