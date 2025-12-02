<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import HabitsList from '@/components/habits/HabitsList.vue';
import { Repeat } from 'lucide-vue-next'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('habits'),
      '--module-color-rgb': hexToRgbStr(moduleColor('habits')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('habits')),
    }"
  >
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Repeat class="w-8 h-8 text-health" />

        <h1 class="text-3xl font-bold text-foreground">Habits</h1>
      </div>
      <p class="text-sm text-health">Track your habit streaks and progress</p>
    </div>
    <HabitsList/>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  min-height: 100vh;
}

main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  border-radius: 4px 0 0 4px;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}

.page-title {
  margin: 0 0 6px 0;
  font-size: 2rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-subtitle {
  margin: 0;
  font-size: 0.95rem;
  color: var(--text-secondary);
}
</style>
