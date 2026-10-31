export interface TargetProfile {
  id: string
  serviceName: string
  baseApiUrl?: string
  githubRepoUrl?: string
  githubBranch: string
  readonlyDbJdbcUrl?: string
  isActive: boolean
}

