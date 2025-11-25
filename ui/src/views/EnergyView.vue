<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import EnergyCard from '@/components/Dashboard/EnergyCard.vue'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('energy'),
      '--module-color-rgb': hexToRgbStr(moduleColor('energy')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('energy')),
    }"
  >
    <div class="page-header">
      <h1 class="page-title">⚡ Energy</h1>
      <p class="page-subtitle">Track your energy consumption and costs</p>
    </div>
    <EnergyCard :compact="false" :data="{ gasYoYPercent: 8, electricityYoYPercent: -3 }" />
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 25, 118, 210), var(--module-tint-alpha, 0.04)),
    rgba(255, 255, 255, 0)
  );
  min-height: 100vh;
}

/* subtle left accent to visually connect with the dashboard cards */
main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: var(--module-color, #1976d2);
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
  color: var(--module-color, #1976d2);
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
