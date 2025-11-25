import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
    state: () => ({
        theme: (localStorage.getItem('pm_theme') as 'light' | 'dark') || 'light',
    }),
    actions: {
        init() {
            // apply theme on startup
            document.documentElement.setAttribute('data-theme', this.theme)
        },
        toggle() {
            this.theme = this.theme === 'light' ? 'dark' : 'light'
            localStorage.setItem('pm_theme', this.theme)
            document.documentElement.setAttribute('data-theme', this.theme)
        },
        set(theme: 'light' | 'dark') {
            this.theme = theme
            localStorage.setItem('pm_theme', this.theme)
            document.documentElement.setAttribute('data-theme', this.theme)
        },
    },
})
