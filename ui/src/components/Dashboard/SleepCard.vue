<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    lastNightHours?: number
    quality?: 'good' | 'fair' | 'poor'
    goalHours?: number
  }
}>()

const qualityDisplay = computed(() => {
  const q = props.data?.quality ?? 'fair'
  return { good: '😴', fair: '😐', poor: '😤' }[q] || '😐'
})

const statusText = computed(() => {
  const hours = props.data?.lastNightHours ?? 0
  const goal = props.data?.goalHours ?? 8
  if (hours >= goal) return `${hours}h ✓`
  return `${hours}h (${goal}h target)`
})
</script>

<template>
  <div class="sleep-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-moon" style="font-size: 1em; margin-right: 0.3em"></i>
        Sleep
      </h3>
      <div class="compact-peek">
        <span class="quality">{{ qualityDisplay }}</span>
        <span class="hours">{{ statusText }}</span>
      </div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-moon" style="font-size: 1em; margin-right: 0.3em"></i>
        Sleep
      </h2>
      <p class="feature-description">Track sleep patterns and quality for better rest.</p>
      <p class="feature-overview">
        Last night: {{ props.data?.lastNightHours ?? '7' }}h ({{ props.data?.quality ?? 'fair' }}) —
        Goal: {{ props.data?.goalHours ?? '8' }}h
      </p>
      <p class="feature-go-to">Go to Sleep -></p>
    </template>
  </div>
</template>

<style scoped>
.sleep-card {
  /* minimal root - shell controls surface/accent */
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.sleep-card h3 {
  margin: 0 0 12px 0;
  font-size: 1.05rem;
  font-weight: 600;
}
.compact-peek {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent; /* flush with dashboard shell */
  padding: 10px 12px;
  border-radius: 6px;
}
.quality {
  font-size: 1.4rem;
  flex-shrink: 0;
  color: var(--module-color, #3742fa);
}
.hours {
  font-weight: 700;
  color: var(--module-color, #3742fa);
}
.sleep-card h2 {
  margin: 0 0 12px 0;
  font-size: 1.5rem;
}
.sleep-card .feature-description,
.sleep-card .feature-overview {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.sleep-card .feature-go-to {
  color: var(--module-color, #3742fa);
  font-weight: 600;
  margin-top: 12px;
}
</style>
