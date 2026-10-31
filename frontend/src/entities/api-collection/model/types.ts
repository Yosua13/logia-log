export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'HEAD' | 'OPTIONS'

export type ApiAuthType = 'NO_AUTH' | 'BEARER_TOKEN' | 'BASIC_AUTH' | 'API_KEY' | 'OAUTH2'

export interface ApiRequestDraft {
  name: string
  method: HttpMethod
  url: string
  authType: ApiAuthType
  headers: Array<{ key: string; value: string; enabled: boolean }>
  bodyPayload: string
}

