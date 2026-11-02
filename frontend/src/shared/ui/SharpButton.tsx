import type { ButtonHTMLAttributes, ReactNode } from 'react'

interface SharpButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost'
  children: ReactNode
}

export function SharpButton({
  variant = 'primary',
  className = '',
  children,
  ...props
}: SharpButtonProps) {
  const variantClass =
    variant === 'primary'
      ? 'logialog-btn-primary'
      : variant === 'secondary'
        ? 'logialog-btn-secondary'
        : 'logialog-btn-ghost'

  return (
    <button
      type="button"
      className={`${variantClass} px-4 py-2 text-xs transition-colors ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}
