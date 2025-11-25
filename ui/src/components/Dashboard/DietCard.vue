<script setup lang="ts">
const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: { consumed?: string; allowed?: string }
}>()
</script>

<template>
  <div class="diet-card">
    <template v-if="props.compact">
      <h3>Diet</h3>
      <div class="diet-summary">{{ props.dataPeak ?? '' }}</div>
      <div class="diet-stats">
        <div class="stat">
          <div class="value">{{ props.data?.consumed ?? '0' }}</div>
          <div class="label">Consumed</div>
        </div>
        <div class="stat">
          <div class="value">{{ props.data?.allowed ?? '0' }}</div>
          <div class="label">Allowed</div>
        </div>
      </div>
    </template>

    <template v-else>
      <h2>Diet</h2>
      <p class="feature-description">Track your daily intake.</p>
      <p class="feature-overview">
        Today's: {{ props.data?.consumed ?? '-' }} / {{ props.data?.allowed ?? '-' }}
      </p>
      <p class="feature-go-to">Go to Diet -></p>
    </template>
  </div>
</template>

<style scoped>
.diet-card {
  /* keep the component root minimal - the dashboard shell supplies the surface and accents */
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.diet-card h3 {
  margin: 0 0 12px 0;
  font-size: 1.05rem;
  font-weight: 600;
}
.diet-summary {
  margin-top: 8px;
  font-size: 0.95rem;
  padding-left: 8px;
}
.diet-stats {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.stat {
  flex: 1;
  background: transparent; /* flush with dashboard shell */
  padding: 10px;
  border-radius: 6px;
}
.stat .value {
  color: var(--module-color, #fd79a8);
  font-weight: 700;
}
.diet-card h2 {
  margin: 0 0 12px 0;
  font-size: 1.5rem;
}
.diet-card .feature-description,
.diet-card .feature-overview {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.diet-card .feature-go-to {
  color: var(--module-color, #fd79a8);
  font-weight: 600;
  margin-top: 12px;
}
</style>
