<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    currentStreak?: number
    bestStreak?: number
    completedToday?: boolean
  }
}>()

const streakDisplay = computed(() => {
  const streak = props.data?.currentStreak ?? 0
  if (streak === 0) return '🔥 Start now'
  if (streak >= 30) return `🔥🔥🔥 ${streak}d`
  if (streak >= 7) return `🔥🔥 ${streak}d`
  return `🔥 ${streak}d`
})

const todayStatus = computed(() => {
  return props.data?.completedToday ? '✓ Done today' : '○ Not yet'
})
</script>

<template>
  <div class="habits-card">
    <template v-if="props.compact">
      <h3>Habits</h3>
      <div class="compact-peek">
        <span class="streak">{{ streakDisplay }}</span>
      </div>
      <div class="compact-status">{{ todayStatus }}</div>
    </template>

    <template v-else>
      <h2>Habits</h2>
      <p class="feature-description">Build better routines and track streaks.</p>
      <p class="feature-overview">
        Streak: {{ props.data?.currentStreak ?? 0 }}d — Best: {{ props.data?.bestStreak ?? 0 }}d
      </p>
      <p class="feature-go-to">Go to Habits -></p>
    </template>
  </div>
</template>

<style scoped>
.habits-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.habits-card h3,
.habits-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.habits-list {
  margin-top: 8px;
}
.habit {
  background: rgba(0, 0, 0, 0.06);
  padding: 8px;
  border-radius: 8px;
  margin-bottom: 6px;
}
.habit .label {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}
.habits-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.habits-card .feature-go-to {
  color: var(--module-color, #ffd699);
  font-weight: 600;
  margin-top: 12px;
}
</style>
