export interface TacticalTabItem<T extends string> {
  id: T
  label: string
  code?: string
}

interface TacticalTabsProps<T extends string> {
  items: ReadonlyArray<TacticalTabItem<T>>
  activeId: T
  onChange: (id: T) => void
}

export function TacticalTabs<T extends string>({
  items,
  activeId,
  onChange,
}: TacticalTabsProps<T>) {
  return (
    <nav className="flex flex-wrap border-b border-[#9A7B2C]/60 bg-[#0A0A0A]" aria-label="Workspace navigation">
      {items.map((item) => {
        const isActive = item.id === activeId
        return (
          <button
            key={item.id}
            type="button"
            onClick={() => onChange(item.id)}
            className={`cursor-pointer border-r border-[#9A7B2C]/40 px-4 py-2.5 text-xs font-semibold tracking-wider uppercase transition-colors ${
              isActive
                ? 'border-b-2 border-b-[#FFD700] bg-[#121212] text-[#FFD700]'
                : 'text-[#C5A059] hover:bg-[#121212]/70 hover:text-[#D4AF37]'
            }`}
          >
            {item.code && (
              <span className="mr-2 font-mono text-[10px] opacity-75">[{item.code}]</span>
            )}
            {item.label}
          </button>
        )
      })}
    </nav>
  )
}

