import type { ReactNode } from 'react'

interface GoldFrameCardProps {
  title?: string
  subtitle?: string
  badge?: string
  tacticalCorners?: boolean
  children: ReactNode
  className?: string
}

export function GoldFrameCard({
  title,
  subtitle,
  badge,
  tacticalCorners = true,
  children,
  className = '',
}: GoldFrameCardProps) {
  return (
    <section
      className={`logialog-container p-5 ${tacticalCorners ? 'logialog-tactical-corners' : ''} ${className}`}
    >
      {(title || badge) && (
        <header className="mb-4 flex items-center justify-between gap-3 border-b border-[#9A7B2C]/50 pb-3">
          <div>
            {title && (
              <h2 className="text-sm font-bold tracking-wider text-[#D4AF37] uppercase">
                {title}
              </h2>
            )}
            {subtitle && (
              <p className="mt-0.5 text-xs text-[#C5A059]/80">{subtitle}</p>
            )}
          </div>
          {badge && (
            <span className="border border-[#D4AF37]/60 bg-[#121212] px-2.5 py-0.5 font-mono text-[11px] font-semibold text-[#FFD700]">
              {badge}
            </span>
          )}
        </header>
      )}
      {children}
    </section>
  )
}
