import { defineStore } from 'pinia'

export type Theme = 'light' | 'dark'

export const useThemeStore = defineStore('theme', {
    state: () => ({
        theme: (localStorage.getItem('pm_theme') as Theme) || 'light',
    }),
    actions: {
        init() {
            // apply theme on startup
            if (this.theme === 'dark') {
                document.documentElement.classList.add('dark')
            } else {
                document.documentElement.classList.remove('dark')
            }
        },
        toggle() {
            // Toggle between light and dark
            this.theme = this.theme === 'light' ? 'dark' : 'light'
            this.apply()
        },
        set(theme: Theme) {
            this.theme = theme
            this.apply()
        },
        apply() {
            localStorage.setItem('pm_theme', this.theme)
            
            if (this.theme === 'dark') {
                document.documentElement.classList.add('dark')
            } else {
                document.documentElement.classList.remove('dark')
            }
        },
    },
})
