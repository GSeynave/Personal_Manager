<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useModuleStore } from '@/stores/modules'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

interface ModuleItem {
  id: string
  label: string
  icon: string
  to: string
}

interface ModuleCategory {
  id: string
  label: string
  icon: string
  expanded: boolean
  modules: ModuleItem[]
}

const categories = ref<ModuleCategory[]>([
  {
    id: 'wellness',
    label: 'Wellness & Health',
    icon: '🏥',
    expanded: true,
    modules: [
      { id: 'todo', label: 'Todo', icon: '✓', to: '/todo' },
      { id: 'diet', label: 'Diet', icon: '🍎', to: '/diet' },
      { id: 'sleep', label: 'Sleep', icon: '😴', to: '/sleep' },
      { id: 'fitness', label: 'Fitness', icon: '💪', to: '/' },
      { id: 'health', label: 'Health', icon: '❤️', to: '/' },
      { id: 'habits', label: 'Habits', icon: '🔄', to: '/' },
    ],
  },
  {
    id: 'productivity',
    label: 'Productivity & Work',
    icon: '💼',
    expanded: true,
    modules: [
      { id: 'projects', label: 'Projects', icon: '📊', to: '/' },
      { id: 'pomodoro', label: 'Pomodoro', icon: '⏲️', to: '/' },
      { id: 'calendar', label: 'Calendar', icon: '📅', to: '/calendar' },
      { id: 'notes', label: 'Notes', icon: '📝', to: '/' },
    ],
  },
  {
    id: 'personal',
    label: 'Personal Development',
    icon: '🎯',
    expanded: false,
    modules: [
      { id: 'goals', label: 'Goals', icon: '🎪', to: '/' },
      { id: 'learning', label: 'Learning', icon: '📚', to: '/' },
    ],
  },
  {
    id: 'lifestyle',
    label: 'Life Management',
    icon: '🌍',
    expanded: false,
    modules: [
      { id: 'finance', label: 'Finance', icon: '💰', to: '/accounting' },
      { id: 'energy', label: 'Energy', icon: '⚡', to: '/energy' },
      { id: 'poi', label: 'Places', icon: '📍', to: '/' },
    ],
  },
])

const isCollapsed = ref(false)

const moduleStore = useModuleStore()

// Use centralized module colors
// helpers: moduleColor(id) | hexToRgbStr(hex) | MODULE_COLORS

const toggleCategory = (categoryId: string) => {
  const category = categories.value.find((c) => c.id === categoryId)
  if (category) {
    category.expanded = !category.expanded
  }
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<template>
  <aside :class="['module-bar', { collapsed: isCollapsed }]">
    <!-- Collapse Toggle Button -->
    <button
      class="collapse-btn"
      @click="toggleCollapse"
      :title="isCollapsed ? 'Expand' : 'Collapse'"
    >
      <span class="collapse-icon">{{ isCollapsed ? '→' : '←' }}</span>
    </button>

    <!-- Categories -->
    <nav class="categories-nav">
      <div v-for="category in categories" :key="category.id" class="category-group">
        <!-- Category Header -->
        <button
          class="category-header"
          @click="toggleCategory(category.id)"
          :title="category.label"
        >
          <span class="category-icon">{{ category.icon }}</span>
          <span v-if="!isCollapsed" class="category-label">{{ category.label }}</span>
          <span v-if="!isCollapsed" class="expand-icon" :class="{ expanded: category.expanded }">
            ▶
          </span>
        </button>

        <!-- Category Modules -->
        <transition name="expand">
          <div v-if="category.expanded && !isCollapsed" class="modules-list">
            <div v-for="module in category.modules" :key="module.id" class="module-row">
              <RouterLink
                :to="module.to"
                class="module-link"
                :title="module.label"
                active-class="active"
                :style="{
                  '--module-color': moduleStore.enabled[module.id]
                    ? moduleColor(module.id)
                    : '#6b7280',
                  '--module-color-rgb': moduleStore.enabled[module.id]
                    ? hexToRgbStr(moduleColor(module.id))
                    : '107, 114, 128',
                  '--module-tint-alpha': moduleStore.enabled[module.id]
                    ? moduleTintAlpha(moduleColor(module.id))
                    : 0.02,
                }"
              >
                <span class="module-icon">{{ module.icon }}</span>
                <span class="module-label">{{ module.label }}</span>
              </RouterLink>

              <!-- compact clickable chip to toggle module visibility -->
              <button
                v-if="!isCollapsed"
                class="module-chip"
                :class="{ active: !!moduleStore.enabled[module.id] }"
                @click="() => moduleStore.toggleModule(module.id)"
                :title="module.label"
                :style="{
                  '--module-color': moduleStore.enabled[module.id]
                    ? moduleColor(module.id)
                    : '#9ca3af',
                  '--module-color-rgb': moduleStore.enabled[module.id]
                    ? hexToRgbStr(moduleColor(module.id))
                    : '156, 163, 175',
                  '--module-tint-alpha': moduleStore.enabled[module.id]
                    ? moduleTintAlpha(moduleColor(module.id))
                    : 0.02,
                }"
              >
                <span class="chip-dot" :aria-hidden="true"></span>
                <span class="chip-label">{{ moduleStore.enabled[module.id] ? 'On' : 'Off' }}</span>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </nav>

    <!-- Quick Actions (Bottom) -->
    <div v-if="!isCollapsed" class="quick-actions">
      <button class="action-btn" title="Settings">
        <span>⚙️</span>
        <span>Settings</span>
      </button>
      <button class="action-btn" title="Help">
        <span>❓</span>
        <span>Help</span>
      </button>
    </div>
  </aside>
</template>

<style scoped>
.module-bar {
  width: 280px;
  background: var(--bg-primary);
  backdrop-filter: blur(10px);
  border-right: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  transition: width var(--transition-slow);
  box-shadow: 2px 0 var(--shadow-md);
  position: relative;
  z-index: 900;
}

.module-bar.collapsed {
  width: 80px;
}

.collapse-btn {
  position: sticky;
  top: 0;
  width: 100%;
  padding: var(--spacing-md);
  background: rgba(var(--primary-rgb), 0.1);
  border: none;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background var(--transition-base);
}

.collapse-btn:hover {
  background: rgba(var(--primary-rgb), 0.15);
}

.collapse-icon {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--primary);
}

.categories-nav {
  flex: 1;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.category-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.category-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-secondary);
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  transition: all var(--transition-base);
  text-align: left;
}

.category-header:hover {
  background: rgba(var(--primary-rgb), 0.1);
  border-color: rgba(var(--primary-rgb), 0.2);
  color: var(--primary);
}

.category-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.category-label {
  flex: 1;
  font-size: var(--text-sm);
}

.expand-icon {
  font-size: 0.75rem;
  transition: transform var(--transition-base);
  flex-shrink: 0;
}

.expand-icon.expanded {
  transform: rotate(90deg);
}

.modules-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-left: var(--spacing-sm);
  border-left: 2px solid rgba(var(--primary-rgb), 0.2);
}

.module-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--transition-base);
  font-size: var(--text-sm);
}

.module-link:hover {
  background: rgba(var(--primary-rgb), 0.06);
  color: var(--primary);
}

.module-link.active {
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 0, 0, 0), 0.08),
    rgba(var(--module-color-rgb, 0, 0, 0), 0.04)
  );
  color: var(--module-color, var(--primary));
  font-weight: var(--fw-semibold);
}

.module-icon {
  font-size: 1rem;
  flex-shrink: 0;
}

.module-label {
  flex: 1;
}

.module-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* Make module links look like compact cards with a left accent */
.module-link {
  position: relative;
  padding-left: calc(var(--spacing-md) + 8px); /* room for left accent */
  /* subtle tint using module color so each link has a faint colored card effect */
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 127, 140, 141), var(--module-tint-alpha, 0.035)),
    rgba(var(--module-color-rgb, 127, 140, 141), calc(var(--module-tint-alpha, 0.035) / 2))
  );
  border: 1px solid transparent;
}
.module-link::before {
  /* left accent bar */
  content: '';
  position: absolute;
  left: 8px;
  top: 8px;
  bottom: 8px;
  width: 6px;
  border-radius: 4px;
  background: linear-gradient(
    180deg,
    rgba(var(--module-color-rgb, 127, 140, 141), 0.95),
    rgba(var(--module-color-rgb, 127, 140, 141), 0.9)
  );
}

/* When disabled (moduleColor is grey) tone down entire card */
.module-link[style*='#6b7280'],
.module-link[style*='#9ca3af'] {
  opacity: 0.72;
  filter: grayscale(40%);
}

.module-toggle {
  display: inline-flex;
  align-items: center;
  margin-left: 8px;
}

.module-toggle input[type='checkbox'] {
  appearance: none;
  width: 36px;
  height: 18px;
  background: var(--bg-secondary);
  border-radius: 999px;
  position: relative;
  outline: none;
  border: 1px solid var(--border-light);
  cursor: pointer;
}

.module-toggle input[type='checkbox']:checked {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
}

.module-toggle .toggle-knob {
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
  display: inline-block;
  margin-left: -26px;
  transform: translateX(0px);
  transition: transform var(--transition-base);
}

.module-toggle input[type='checkbox']:checked + .toggle-knob {
  transform: translateX(16px);
}

/* new compact chip toggle */
.module-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 999px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  cursor: pointer;
  font-size: 0.8rem;
  color: var(--text-secondary);
}
.module-chip .chip-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--module-color, rgba(0, 0, 0, 0.12));
  display: inline-block;
  opacity: 0.95;
}
.module-chip.active {
  background: linear-gradient(
    135deg,
    rgba(var(--module-color-rgb, 0, 0, 0), 0.12),
    rgba(var(--module-color-rgb, 0, 0, 0), 0.06)
  );
  color: var(--module-color, var(--primary));
}
.module-chip.active .chip-dot {
  background: var(--module-color, var(--primary));
}

.quick-actions {
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--text-secondary);
  transition: all var(--transition-base);
  font-size: var(--text-sm);
}

.action-btn:hover {
  background: rgba(var(--primary-rgb), 0.1);
  border-color: rgba(var(--primary-rgb), 0.2);
  color: var(--primary);
}

/* Expand/Collapse Animation */
.expand-enter-active,
.expand-leave-active {
  transition: all var(--transition-base);
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 1000px;
}

/* Scrollbar styling */
.module-bar::-webkit-scrollbar {
  width: 6px;
}

.module-bar::-webkit-scrollbar-track {
  background: transparent;
}

.module-bar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.module-bar::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.2);
}

@media (max-width: 1024px) {
  .module-bar {
    width: 240px;
  }

  .module-bar.collapsed {
    width: 70px;
  }
}

@media (max-width: 768px) {
  .module-bar {
    width: 200px;
  }

  .module-bar.collapsed {
    width: 60px;
  }

  .category-label,
  .module-label {
    font-size: 0.85rem;
  }
}
</style>
