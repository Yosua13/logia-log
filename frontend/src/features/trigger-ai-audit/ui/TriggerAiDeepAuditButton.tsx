import { SharpButton } from '@/shared/ui/SharpButton'

interface TriggerAiDeepAuditButtonProps {
  fingerprintHash: string
  onTrigger?: (hash: string) => void
}

export function TriggerAiDeepAuditButton({
  fingerprintHash,
  onTrigger,
}: TriggerAiDeepAuditButtonProps) {
  return (
    <SharpButton
      variant="primary"
      onClick={() => onTrigger?.(fingerprintHash)}
      title={`Fingerprint: ${fingerprintHash}`}
    >
      Analyze GitHub Code (7-Layer AI)
    </SharpButton>
  )
}

