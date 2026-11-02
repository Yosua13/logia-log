import { GoldFrameCard } from '@/shared/ui/GoldFrameCard'

export function TargetConnectionSettingsPage() {
  return (
    <GoldFrameCard
      title="Dynamic Target Profile Slot (Target-Agnostic / Plug-and-Play)"
      subtitle="PRD Bagian 06.1 • Kosong secara Default • Dapat Dihubungkan ke Aplikasi Apa Saja"
      badge="AES-256-GCM"
    >
      <div className="space-y-3 text-xs text-[#E5E5E5]">
        <p>
          Logia Log tidak mengikatkan diri pada satu aplikasi simulasi tertentu. Anda dapat
          mendaftarkan atau mengosongkan profil koneksi target kapan saja melalui endpoint{' '}
          <code className="font-mono text-[#FFD700]">/api/v1/targets</code>:
        </p>
        <ul className="list-disc space-y-1.5 pl-5 font-mono text-[#C5A059]">
          <li>Base API URL (opsional untuk preset environment di API Studio)</li>
          <li>GitHub Repository URL &amp; Branch (opsional untuk 7-Layer Deep Code Audit)</li>
          <li>Read-Only PostgreSQL JDBC URL (opsional untuk Dynamic Schema Injection via information_schema)</li>
        </ul>
      </div>
    </GoldFrameCard>
  )
}

