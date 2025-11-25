<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    gasYoYPercent?: number
    electricityYoYPercent?: number
    // optional live pricing data
    currentPrice?: number
    averagePrice?: number
    // explicit flag if available
    isCheaper?: boolean
  }
}>()

const gasDisplay = computed(() => {
  const pct = props.data?.gasYoYPercent ?? 0
  const sign = pct > 0 ? '+' : ''
  const trend = pct > 0 ? '📈' : pct < 0 ? '📉' : '→'
  return { trend, text: `${sign}${pct}%` }
})

const electricityDisplay = computed(() => {
  const pct = props.data?.electricityYoYPercent ?? 0
  const sign = pct > 0 ? '+' : ''
  const trend = pct > 0 ? '📈' : pct < 0 ? '📉' : '→'
  return { trend, text: `${sign}${pct}%` }
})

const priceStatus = computed(() => {
  // Prefer an explicit flag if provided
  if (typeof props.data?.isCheaper === 'boolean') {
    return props.data.isCheaper
      ? { key: 'cheaper', label: 'Cheaper now' }
      : { key: 'normal', label: 'Normal price' }
  }

  const current = props.data?.currentPrice
  const avg = props.data?.averagePrice
  if (typeof current === 'number' && typeof avg === 'number') {
    if (current <= avg * 0.95) return { key: 'cheaper', label: 'Cheaper now' }
    if (current >= avg * 1.05) return { key: 'expensive', label: 'Expensive now' }
    return { key: 'normal', label: 'Normal price' }
  }

  return { key: 'unknown', label: 'Price: N/A' }
})
</script>

<template>
  <div class="energy-card">
    <template v-if="props.compact">
      <h3 class="header-with-badge">
        <i class="pi pi-sun" style="font-size: 1em; margin-right: 0.3em"></i>
        Energy
        <span v-if="priceStatus" class="price-badge" :data-state="priceStatus.key">{{
          priceStatus.label
        }}</span>
      </h3>
      <div class="compact-metrics">
        <div class="metric gas">
          <span class="icon">🔥</span>
          <span class="label">Gas</span>
          <span class="value">{{ gasDisplay.text }}</span>
        </div>
        <div class="metric electricity">
          <span class="icon">⚡</span>
          <span class="label">Electricity</span>
          <span class="value">{{ electricityDisplay.text }}</span>
        </div>
      </div>
      <div class="compact-note">vs last year</div>
    </template>

    <template v-else>
      <h2 class="header-with-badge">
        <i class="pi pi-sun" style="font-size: 1em; margin-right: 0.3em"></i>
        Energy
        <span v-if="priceStatus" class="price-badge" :data-state="priceStatus.key">{{
          priceStatus.label
        }}</span>
      </h2>
      <p class="feature-description">Monitor energy usage and identify periods of lower costs.</p>
      <p class="feature-overview">
        Gas: {{ (props.data as any)?.gasYoYPercent ?? '-5' }}% vs last year. Electricity:
        {{ (props.data as any)?.electricityYoYPercent ?? '3' }}% vs last year.
      </p>
      <p class="feature-go-to">Go to Energy -></p>
    </template>
  </div>
</template>

<style scoped>
.energy-card {
  /* minimal root: dashboard shell provides surface and accent */
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.energy-card h3 {
  margin: 0 0 12px 0;
  font-size: 1.05rem;
  font-weight: 600;
}
.compact-metrics {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.metric {
  flex: 1;
  text-align: center;
  padding: 12px 8px;
  border-radius: 6px;
  background: transparent; /* flush with dashboard shell */
}
.metric .icon {
  display: block;
  font-size: 1.6rem;
  margin-bottom: 4px;
}
.metric .label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.72);
  text-transform: uppercase;
}
.metric .value {
  font-weight: 700;
  font-size: 1.05rem;
  color: var(--module-color, #4a90e2);
}
.compact-note {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.65);
  margin-top: 8px;
  text-align: center;
}
/* price badge */
.header-with-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.price-badge {
  margin-left: 8px;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}
.price-badge[data-state='cheaper'] {
  background: rgba(34, 197, 94, 0.12);
  color: #22c55e;
}
.price-badge[data-state='normal'] {
  background: rgba(255, 255, 255, 0.04);
  color: #9ca3af;
}
.price-badge[data-state='expensive'] {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}
.price-badge[data-state='unknown'] {
  background: rgba(255, 255, 255, 0.04);
  color: #9ca3af;
}
.energy-card h2 {
  margin: 0 0 12px 0;
  font-size: 1.5rem;
}
.energy-card .feature-description,
.energy-card .feature-overview {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.energy-card .feature-go-to {
  color: var(--module-color, #4a90e2);
  font-weight: 600;
  margin-top: 12px;
}
</style>
