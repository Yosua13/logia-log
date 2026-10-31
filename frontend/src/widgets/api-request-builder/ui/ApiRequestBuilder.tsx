import { useState } from 'react'
import type { ApiAuthType, HttpMethod } from '@/entities/api-collection/model/types'
import { AuthTypeSelector } from '@/features/auth-type-selector/ui/AuthTypeSelector'
import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'
import { SharpButton } from '@/shared/ui/SharpButton'

const METHODS: ReadonlyArray<HttpMethod> = [
  'GET',
  'POST',
  'PUT',
  'PATCH',
  'DELETE',
  'HEAD',
  'OPTIONS',
]

export function ApiRequestBuilder() {
  const [method, setMethod] = useState<HttpMethod>('POST')
  const [url, setUrl] = useState('{{BASE_URL}}/api/v1/orders/checkout')
  const [authType, setAuthType] = useState<ApiAuthType>('BEARER_TOKEN')
  const [tracePreview] = useState('sntl-019a39f0-7b2c-7000-9f1a-882dc83e96a1')

  return (
    <GoldFrameCard
      title="Interactive API Request Studio"
      subtitle="FR-API-01..07 • Auto-Injects X-Sentinel-Trace-Id for Instant Log & AI Correlation"
      badge="PROXY OVERHEAD < 15MS"
    >
      <div className="flex flex-wrap gap-2">
        <select
          value={method}
          onChange={(e) => setMethod(e.target.value as HttpMethod)}
          className="border border-[#D4AF37] bg-[#121212] px-3 py-2 font-mono text-xs font-bold text-[#FFD700]"
        >
          {METHODS.map((m) => (
            <option key={m} value={m}>
              {m}
            </option>
          ))}
        </select>

        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          className="min-w-[280px] flex-1 border border-[#9A7B2C] bg-[#0A0A0A] px-3 py-2 font-mono text-xs text-[#F5F5F5] focus:border-[#FFD700] focus:outline-none"
        />

        <SharpButton variant="primary">Send Request</SharpButton>
      </div>

      <div className="mt-4 flex flex-wrap items-center justify-between gap-4 border-t border-[#9A7B2C]/40 pt-4">
        <AuthTypeSelector value={authType} onChange={setAuthType} />
        <div className="font-mono text-xs text-[#C5A059]">
          Auto Header:{' '}
          <code className="border border-[#9A7B2C]/60 bg-[#121212] px-2 py-0.5 text-[#FFD700]">
            X-Sentinel-Trace-Id: {tracePreview}
          </code>
        </div>
      </div>
    </GoldFrameCard>
  )
}

