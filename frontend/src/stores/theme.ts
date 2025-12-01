import { defineStore } from 'pinia'

export type Theme = 'natural' | 'cosmic' | 'zen' | 'dark'

export const useThemeStore = defineStore('theme', {
    state: () => ({
        theme: (localStorage.getItem('pm_theme') as Theme) || 'natural',
    }),
    actions: {
        init() {
            // apply theme on startup
            document.documentElement.setAttribute('data-theme', this.theme)
            if (this.theme === 'dark') {
                document.documentElement.classList.add('dark')
            } else {
                document.documentElement.classList.remove('dark')
            }
        },
        toggle() {
            // Cycle through themes: natural -> cosmic -> zen -> dark -> natural
            const themes: Theme[] = ['natural', 'cosmic', 'zen', 'dark']
            const currentIndex = themes.indexOf(this.theme)
            const nextIndex = (currentIndex + 1) % themes.length
            this.theme = themes[nextIndex]!
            this.apply()
        },
        set(theme: Theme) {
            this.theme = theme
            this.apply()
        },
        apply() {
            localStorage.setItem('pm_theme', this.theme)
            document.documentElement.setAttribute('data-theme', this.theme)
            
            if (this.theme === 'dark') {
                document.documentElement.classList.add('dark')
            } else {
                document.documentElement.classList.remove('dark')
            }
        },
    },
})
