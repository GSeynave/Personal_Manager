<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import SleepCard from '@/components/Dashboard/SleepCard.vue'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('sleep'),
      '--module-color-rgb': hexToRgbStr(moduleColor('sleep')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('sleep')),
    }"
  >
    <div class="page-header">
      <h1 class="page-title">😴 Sleep</h1>
      <p class="page-subtitle">Monitor your sleep quality and patterns</p>
    </div>
    <SleepCard :compact="false" :data="{ lastNightHours: 7, quality: 'fair', goalHours: 8 }" />
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 92, 107, 192), var(--module-tint-alpha, 0.04)),
    rgba(255, 255, 255, 0)
  );
  min-height: 100vh;
}

main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: var(--module-color, #5c6bc0);
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
  color: var(--module-color, #5c6bc0);
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
