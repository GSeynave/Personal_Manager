<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    nextEvent?: string
    eventsToday?: number
    timeUntil?: string
  }
}>()

const nextEventDisplay = computed(() => {
  if (!props.data?.nextEvent) return 'No events'
  const time = props.data?.timeUntil ?? 'soon'
  return `${props.data.nextEvent} (${time})`
})

const todayCount = computed(() => {
  const count = props.data?.eventsToday ?? 0
  return count === 0 ? 'Free day' : `${count} events`
})
</script>

<template>
  <div class="calendar-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-calendar" style="font-size: 1em; margin-right: 0.3em"></i>
        Calendar
      </h3>
      <div class="compact-peek">
        <span class="next">{{ nextEventDisplay }}</span>
      </div>
      <div class="compact-count">{{ todayCount }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-calendar" style="font-size: 1em; margin-right: 0.3em"></i>
        Calendar
      </h2>
      <p class="feature-description">Smart scheduling and time blocking.</p>
      <p class="feature-overview">
        Next: {{ props.data?.nextEvent ?? 'No events' }} ({{ props.data?.timeUntil ?? 'TBD' }}),
        Today: {{ props.data?.eventsToday ?? '0' }} events
      </p>
      <p class="feature-go-to">Go to Calendar -></p>
    </template>
  </div>
</template>

<style scoped>
.calendar-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.calendar-card h3,
.calendar-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.compact-peek {
  background: transparent;
  padding: 0;
  border-radius: 0;
}
.next {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.compact-count {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.68);
  margin-top: 8px;
  text-align: center;
}
.calendar-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.calendar-card .feature-overview {
  color: rgba(255, 255, 255, 0.76);
  margin: 8px 0;
}
.calendar-card .feature-go-to {
  color: var(--module-color, #80deea);
  font-weight: 600;
  margin-top: 12px;
}
</style>
