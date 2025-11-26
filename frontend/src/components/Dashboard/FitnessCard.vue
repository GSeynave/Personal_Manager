<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: { workoutTime?: number; caloriesBurned?: number; goal?: number }
}>()

const progressPercent = computed(() => {
  const burned = props.data?.caloriesBurned ?? 0
  const goal = props.data?.goal ?? 500
  return Math.min(100, Math.round((burned / Math.max(1, goal)) * 100))
})
</script>

<template>
  <div class="fitness-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-bolt" style="font-size: 1em; margin-right: 0.3em"></i>
        Fitness
      </h3>
      <div class="compact-peek">
        <span class="status">{{ props.data?.workoutTime ?? 0 }}m</span>
        <span class="metric">{{ props.data?.caloriesBurned ?? 0 }} kcal</span>
      </div>
      <div class="compact-progress">
        <div class="progress">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
        <div class="progress-text">{{ progressPercent }}%</div>
      </div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-bolt" style="font-size: 1em; margin-right: 0.3em"></i>
        Fitness
      </h2>
      <p class="feature-description">Track workouts and monitor your fitness progress.</p>
      <p class="feature-overview">
        Today: {{ props.data?.workoutTime ?? '0' }}min workout,
        {{ props.data?.caloriesBurned ?? '0' }} kcal burned (Goal:
        {{ props.data?.goal ?? '500' }} kcal)
      </p>
    </template>
  </div>
</template>

<style scoped>
.fitness-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.fitness-card h3,
.fitness-card h2 {
  margin: 0 0 12px 0;
}
.compact-peek {
  /* centralized shell controls visual surface; keep layout only */
  display: flex;
  gap: 12px;
  align-items: center;
  background: transparent;
  padding: 0;
}
.status {
  font-weight: 700;
  color: var(--module-color, #ffa726);
}
.metric {
  font-weight: 700;
  color: var(--module-color, #ffa726);
}
.progress {
  height: 8px;
  background: rgba(0, 0, 0, 0.06);
  border-radius: 999px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.06), rgba(0, 0, 0, 0.06)),
    var(--module-color, #ffa726);
  transition: width 0.3s ease;
}
.progress-text {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.85);
  text-align: center;
  margin-top: 6px;
}
.feature-description,
.feature-overview {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.feature-go-to {
  color: var(--module-color, #ffa726);
  font-weight: 600;
  margin-top: 12px;
}
.fitness-card h2 {
  margin: 0 0 12px 0;
  font-size: 1.5rem;
}
</style>
