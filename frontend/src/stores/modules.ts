import { defineStore } from 'pinia'

type EnabledMap = Record<string, boolean>

const DEFAULT_ORDER = ['finance', 'energy', 'todo', 'calendar', 'diet', 'sleep']

export const useModuleStore = defineStore('modules', {
    state: () => ({
        // visibility map keyed by module key used in widgets
        enabled: ((): EnabledMap => {
            try {
                const raw = localStorage.getItem('pm_modules')
                if (raw) return JSON.parse(raw)
            } catch { }
            // default: only show the prioritized modules
            const map: EnabledMap = {}
                // enable defaults
                ;[...DEFAULT_ORDER].forEach((k) => (map[k] = true))
            return map
        })(),
        order: ((): string[] => {
            try {
                const raw = localStorage.getItem('pm_modules_order')
                if (raw) return JSON.parse(raw)
            } catch { }
            return DEFAULT_ORDER.slice()
        })(),
    }),
    actions: {
        toggleModule(key: string) {
            this.enabled[key] = !this.enabled[key]
            localStorage.setItem('pm_modules', JSON.stringify(this.enabled))
        },
        setOrder(order: string[]) {
            this.order = order.slice()
            localStorage.setItem('pm_modules_order', JSON.stringify(this.order))
        },
        enable(key: string) {
            this.enabled[key] = true
            localStorage.setItem('pm_modules', JSON.stringify(this.enabled))
        },
        disable(key: string) {
            this.enabled[key] = false
            localStorage.setItem('pm_modules', JSON.stringify(this.enabled))
        },
    },
})
