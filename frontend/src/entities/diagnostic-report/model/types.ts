export interface AuditDimensionItem {
  layerNumber: 1 | 2 | 3 | 4 | 5 | 6 | 7
  dimension: string
  summary: string
  actionableFix: string
}

export interface GithubDiagnosticReport {
  fingerprintHash: string
  repoUrl: string
  branchName: string
  commitSha: string
  layers: ReadonlyArray<AuditDimensionItem>
  suggestedPatchDiff: string
}

