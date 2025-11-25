<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    appointments?: number
    medications?: number
    symptoms?: boolean
  }
}>()

const statusDisplay = computed(() => {
  const sympt = props.data?.symptoms
  if (sympt) return '⚠️ Check-in needed'
  return '✓ All clear'
})

const summaryText = computed(() => {
  const apps = props.data?.appointments ?? 0
  const meds = props.data?.medications ?? 0
  const items = []
  if (apps > 0) items.push(`${apps} appt`)
  if (meds > 0) items.push(`${meds} meds`)
  return items.length > 0 ? items.join(' • ') : 'No active items'
})
</script>

<template>
  <div class="health-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-heart-fill" style="font-size: 1em; margin-right: 0.3em"></i>
        Health
      </h3>
      <div class="compact-peek">
        <span class="status">{{ statusDisplay }}</span>
      </div>
      <div class="compact-summary">{{ summaryText }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-heart-fill" style="font-size: 1em; margin-right: 0.3em"></i>
        Health
      </h2>
      <p class="feature-description">Manage medications, appointments, and symptoms.</p>
      <p class="feature-overview">
        Appointments: {{ props.data?.appointments ?? '0' }}, Medications:
        {{ props.data?.medications ?? '0' }}, Symptoms:
        {{ props.data?.symptoms ? 'reported' : 'none' }}
      </p>
      <p class="feature-go-to">Go to Health -></p>
    </template>
  </div>
</template>

<style scoped>
.health-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.health-card h3,
.health-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.health-card .metric {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--module-color, #ef4444);
}
.health-card .metric-sub {
  color: rgba(255, 255, 255, 0.8);
}
.health-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.health-card .feature-overview {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.95rem;
  margin: 8px 0;
}
.health-card .feature-go-to {
  color: var(--module-color, #ffcdd2);
  font-weight: 600;
  margin-top: 12px;
}
</style>
