<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    poiCount?: number
    nearbyCount?: number
    lastVisited?: string
  }
}>()

const locationStatus = computed(() => {
  const nearby = props.data?.nearbyCount ?? 0
  if (nearby > 0) return `📍 ${nearby} nearby`
  return '○ No POI'
})

const summaryText = computed(() => {
  const total = props.data?.poiCount ?? 0
  const last = props.data?.lastVisited
  const items = []
  if (total > 0) items.push(`${total} places`)
  if (last) items.push(`Last: ${last}`)
  return items.join(' • ') || 'No POIs saved'
})
</script>

<style scoped>
.poi-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.poi-card h3,
.poi-card h2 {
  margin: 0 0 12px 0;
  padding-left: 8px;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.poi-list {
  margin-top: 8px;
}
.poi-item {
  background: rgba(0, 0, 0, 0.06);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.poi-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.poi-card .feature-go-to {
  color: var(--module-color, #e1bee7);
  font-weight: 600;
  margin-top: 12px;
}
</style>
color: white; } .poi-card .feature-description { color: rgba(255, 255, 255, 0.8); font-size: 0.9rem;
margin: 8px 0; } .poi-card .feature-overview { color: rgba(255, 255, 255, 0.9); font-size: 0.95rem;
margin: 8px 0; } .poi-card .feature-go-to { color: #e1bee7; font-weight: 600; margin-top: 12px; }
<style scoped>
.poi-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.poi-card h3,
.poi-card h2 {
  margin: 0 0 12px 0;
  padding-left: 8px;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.poi-list {
  margin-top: 8px;
}
.poi-item {
  background: rgba(0, 0, 0, 0.06);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.poi-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.poi-card .feature-go-to {
  color: var(--module-color, #e1bee7);
  font-weight: 600;
  margin-top: 12px;
}
</style>
