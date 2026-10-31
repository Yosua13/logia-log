import type { ApiAuthType } from '@/entities/api-collection/model/types'

interface AuthTypeSelectorProps {
  value: ApiAuthType
  onChange: (value: ApiAuthType) => void
}

const AUTH_OPTIONS: ReadonlyArray<{ value: ApiAuthType; label: string }> = [
  { value: 'NO_AUTH', label: 'No Auth' },
  { value: 'BEARER_TOKEN', label: 'Bearer Token (JWT)' },
  { value: 'BASIC_AUTH', label: 'Basic Auth (Base64)' },
  { value: 'API_KEY', label: 'API Key (Header / Query)' },
  { value: 'OAUTH2', label: 'OAuth 2.0' },
]

export function AuthTypeSelector({ value, onChange }: AuthTypeSelectorProps) {
  return (
    <div className="flex items-center gap-3">
      <label className="font-mono text-xs tracking-wider text-[#C5A059] uppercase">
        Auth Type:
      </label>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value as ApiAuthType)}
        className="border border-[#9A7B2C] bg-[#0A0A0A] px-3 py-1.5 font-mono text-xs text-[#FFD700] focus:border-[#FFD700] focus:outline-none"
      >
        {AUTH_OPTIONS.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  )
}

