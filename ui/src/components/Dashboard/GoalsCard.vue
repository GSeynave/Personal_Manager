<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    activeGoals?: number
    completedGoals?: number
    progressPercent?: number
  }
}>()

const goalStatus = computed(() => {
  const active = props.data?.activeGoals ?? 0
  const completed = props.data?.completedGoals ?? 0
  if (completed > 0) return `✓ ${completed} done`
  if (active > 0) return `${active} goals`
  return 'Set a goal'
})

const progressDisplay = computed(() => {
  const pct = props.data?.progressPercent ?? 0
  return `${pct}% progress`
})
</script>

<template>
  <div class="goals-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-star" style="font-size: 1em; margin-right: 0.3em"></i>
        Goals
      </h3>
      <div class="compact-peek">
        <span class="status">{{ goalStatus }}</span>
      </div>
      <div class="compact-progress">
        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: (props.data?.progressPercent ?? 0) + '%' }"
          ></div>
        </div>
        <span class="progress-text">{{ progressDisplay }}</span>
      </div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-star" style="font-size: 1em; margin-right: 0.3em"></i>
        Goals
      </h2>
      <p class="feature-description">Long-term goal tracking with milestones.</p>
      <p class="feature-overview">
        Active: {{ props.data?.activeGoals ?? '0' }}, Completed:
        {{ props.data?.completedGoals ?? '0' }}, Progress: {{ props.data?.progressPercent ?? '0' }}%
      </p>
      <p class="feature-go-to">Go to Goals -></p>
    </template>
  </div>
</template>

<style scoped>
.goals-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.goals-card h3,
.goals-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.compact-peek {
  background: transparent;
  padding: 0;
  border-radius: 0;
  font-weight: 600;
  font-size: 0.95rem;
  text-align: center;
}
.status {
  display: block;
}
.compact-progress {
  margin-top: 10px;
}
.progress-bar {
  background: rgba(255, 255, 255, 0.06);
  height: 6px;
  border-radius: 3px;
  overflow: hidden;
}
.progress-fill {
  background: var(--module-color, #ffd54f);
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}
.progress-text {
  display: block;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 6px;
  text-align: center;
}
.goal-item {
  background: rgba(0, 0, 0, 0.06);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.goal-item .title {
  font-weight: 700;
}
.goals-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.goals-card .feature-go-to {
  color: var(--module-color, #fff9c4);
  font-weight: 600;
  margin-top: 12px;
}
</style>
