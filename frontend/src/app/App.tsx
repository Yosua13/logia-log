import { useState } from 'react'
import { ApiTestingStudioPage } from '@/pages/api-testing-studio/ui/ApiTestingStudioPage'
import { IncidentDeepAuditPage } from '@/pages/incident-deep-audit/ui/IncidentDeepAuditPage'
import { LogExplorerPage } from '@/pages/log-explorer/ui/LogExplorerPage'
import { TargetConnectionSettingsPage } from '@/pages/target-settings/ui/TargetConnectionSettingsPage'
import type { WorkspaceRoute } from '@/shared/config/stack'
import { TacticalTabs, type TacticalTabItem } from '@/shared/ui/TacticalTabs'
import { CommandHeader } from '@/widgets/command-header/ui/CommandHeader'

const WORKSPACE_TABS: ReadonlyArray<TacticalTabItem<WorkspaceRoute>> = [
  { id: 'log-explorer', code: 'MOD-01', label: 'Grafana Log Explorer' },
  { id: 'api-studio', code: 'MOD-02', label: 'Postman API Studio' },
  { id: 'github-audit', code: 'MOD-03/04', label: '7-Layer GitHub AI Audit' },
  { id: 'target-settings', code: 'CFG', label: 'Target Profiles (Agnostic)' },
]

export function App() {
  const [activeRoute, setActiveRoute] = useState<WorkspaceRoute>('log-explorer')
  const [selectedFingerprint, setSelectedFingerprint] = useState(
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
  )

  const handleNavigateAudit = (fingerprintHash: string) => {
    setSelectedFingerprint(fingerprintHash)
    setActiveRoute('github-audit')
  }

  return (
    <div className="min-h-screen bg-[#0A0A0A] text-[#F5F5F5]">
      <CommandHeader />

      <div className="mx-auto max-w-7xl px-6 pt-4">
        <TacticalTabs
          items={WORKSPACE_TABS}
          activeId={activeRoute}
          onChange={setActiveRoute}
        />

        <main className="py-6">
          {activeRoute === 'log-explorer' && (
            <LogExplorerPage onNavigateAudit={handleNavigateAudit} />
          )}
          {activeRoute === 'api-studio' && <ApiTestingStudioPage />}
          {activeRoute === 'github-audit' && (
            <IncidentDeepAuditPage activeFingerprint={selectedFingerprint} />
          )}
          {activeRoute === 'target-settings' && <TargetConnectionSettingsPage />}
        </main>
      </div>
    </div>
  )
}

