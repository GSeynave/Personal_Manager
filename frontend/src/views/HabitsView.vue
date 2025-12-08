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
    class="p-6 min-h-screen space-y-8"
  >
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Repeat class="w-8 h-8 text-health" />

        <h1 class="text-3xl font-bold text-foreground">Habits</h1>
      </div>
      <p class="text-sm text-health">Track your habit streaks and progress</p>
    </div>
    <div class="space-y-6 p-6">
    <HabitsList/>
    </div>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
