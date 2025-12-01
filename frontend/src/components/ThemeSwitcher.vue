<script setup lang="ts">
import { useThemeStore, type Theme } from '@/stores/theme'

const themeStore = useThemeStore()

const themes: { value: Theme; label: string; icon: string; description: string }[] = [
  { value: 'natural', label: 'Natural', icon: '🌿', description: 'Warm earthy tones' },
  { value: 'cosmic', label: 'Cosmic', icon: '🌌', description: 'Deep space vibes' },
  { value: 'zen', label: 'Zen', icon: '☯️', description: 'Minimal cool grays' },
  { value: 'dark', label: 'Dark', icon: '🌙', description: 'Dark mode' },
]

function selectTheme(theme: Theme) {
  themeStore.set(theme)
}
</script>

<template>
  <div class="theme-switcher">
    <button 
      v-for="theme in themes" 
      :key="theme.value"
      :class="['theme-option', { active: themeStore.theme === theme.value }]"
      @click="selectTheme(theme.value)"
      :title="theme.description"
    >
      <span class="theme-icon">{{ theme.icon }}</span>
      <span class="theme-label">{{ theme.label }}</span>
    </button>
  </div>
</template>

<style scoped>
.theme-switcher {
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
  background: var(--bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 8px var(--shadow-color);
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  padding: 0.75rem 1rem;
  background: var(--surface);
  border: 2px solid var(--border-color);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 70px;
}

.theme-option:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color-hover);
}

.theme-option.active {
  background: linear-gradient(135deg, var(--primary) 0%, var(--accent) 100%);
  border-color: var(--primary);
  color: var(--surface);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color-hover);
}

.theme-icon {
  font-size: 1.5rem;
  line-height: 1;
}

.theme-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.theme-option.active .theme-label {
  color: var(--surface);
}

/* Compact version for small spaces */
.theme-switcher.compact {
  flex-direction: row;
  gap: 0.25rem;
}

.theme-switcher.compact .theme-option {
  padding: 0.5rem;
  min-width: auto;
}

.theme-switcher.compact .theme-label {
  display: none;
}

.theme-switcher.compact .theme-icon {
  font-size: 1.25rem;
}
</style>
