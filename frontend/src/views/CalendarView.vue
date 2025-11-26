<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import CalendarCard from '@/components/Dashboard/CalendarCard.vue'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('calendar'),
      '--module-color-rgb': hexToRgbStr(moduleColor('calendar')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('calendar')),
    }"
  >
    <div class="page-header">
      <h1 class="page-title">📅 Calendar</h1>
      <p class="page-subtitle">View your events and schedule</p>
    </div>
    <CalendarCard
      :compact="false"
      :data="{ nextEvent: 'Team meeting', eventsToday: 3, timeUntil: '2h' }"
    />
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 0, 137, 123), var(--module-tint-alpha, 0.04)),
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
  background: var(--module-color, #00897b);
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
  color: var(--module-color, #00897b);
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
