<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    sessionsToday?: number
    focusTime?: number
    targetSessions?: number
  }
}>()

const sessionStatus = computed(() => {
  const today = props.data?.sessionsToday ?? 0
  const target = props.data?.targetSessions ?? 4
  if (today >= target) return `✓ Goal reached`
  return `${today} / ${target} sessions`
})

const focusDisplay = computed(() => {
  const mins = props.data?.focusTime ?? 0
  if (mins >= 60) return `${Math.floor(mins / 60)}h${mins % 60}m`
  return `${mins}m`
})
</script>

<template>
  <div class="pomodoro-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-clock" style="font-size: 1em; margin-right: 0.3em"></i>
        Pomodoro
      </h3>
      <div class="compact-peek">
        <span class="timer">🍅</span>
        <span class="time">{{ focusDisplay }}</span>
      </div>
      <div class="compact-status">{{ sessionStatus }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-clock" style="font-size: 1em; margin-right: 0.3em"></i>
        Pomodoro
      </h2>
      <p class="feature-description">Focus sessions and productivity tracking.</p>
      <p class="feature-overview">
        Today: {{ props.data?.sessionsToday ?? '0' }} /
        {{ props.data?.targetSessions ?? '4' }} sessions, Focus time: {{ focusDisplay }}
      </p>
      <p class="feature-go-to">Go to Pomodoro -></p>
    </template>
  </div>
</template>

<style scoped>
.pomodoro-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.pomodoro-card h3,
.pomodoro-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.compact-peek {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  padding: 0;
}
.timer {
  font-size: 1.4rem;
  font-weight: 700;
}
.time {
  font-weight: 600;
  font-size: 1rem;
}
.compact-status {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 8px;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.pomodoro-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.pomodoro-card .feature-overview {
  color: rgba(255, 255, 255, 0.76);
  margin: 8px 0;
}
.pomodoro-card .feature-go-to {
  color: var(--module-color, #e1bee7);
  font-weight: 600;
  margin-top: 12px;
}
</style>
