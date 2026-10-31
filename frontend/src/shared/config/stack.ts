export const SENTINEL_STACK = {
  version: '3.1.0-enterprise',
  javaRuntime: 'Java 25.0.5.0.2 LTS (HotSpot 64-Bit Server VM)',
  backendFramework: 'Spring Boot 4.1.2 (Clean Hexagonal + Virtual Threads)',
  database: 'PostgreSQL 18.4 (Partitioned + GIN JSONB + UUIDv7)',
  broker: 'RabbitMQ 4.x (Exchange + DLQ)',
  redisSaas: 'Upstash Serverless Redis Free Tier (TLS rediss://)',
  frontendRuntime: 'Node.js v26.13.0 • React 19 • TypeScript • FSD',
} as const

export type WorkspaceRoute =
  | 'log-explorer'
  | 'api-studio'
  | 'github-audit'
  | 'target-settings'

