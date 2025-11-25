<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    notesCount?: number
    lastNote?: string
    todayNotes?: number
  }
}>()

const latestDisplay = computed(() => {
  if (!props.data?.lastNote) return 'No notes'
  const note = props.data.lastNote
  return note.length > 30 ? note.substring(0, 27) + '...' : note
})

const countDisplay = computed(() => {
  const total = props.data?.notesCount ?? 0
  const today = props.data?.todayNotes ?? 0
  if (today > 0) return `${today} today • ${total} total`
  return `${total} notes`
})
</script>

<template>
  <div class="notes-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-file-text" style="font-size: 1em; margin-right: 0.3em"></i>
        Notes
      </h3>
      <div class="compact-peek">
        <span class="latest">{{ latestDisplay }}</span>
      </div>
      <div class="compact-count">{{ countDisplay }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-file-text" style="font-size: 1em; margin-right: 0.3em"></i>
        Notes
      </h2>
      <p class="feature-description">Quick capture and AI-powered search.</p>
      <p class="feature-overview">
        Total notes: {{ props.data?.notesCount ?? '0' }}, Today:
        {{ props.data?.todayNotes ?? '0' }}, Latest:
        {{ props.data?.lastNote ?? 'Create first note' }}
      </p>
      <p class="feature-go-to">Go to Notes -></p>
    </template>
  </div>
</template>

<style scoped>
.notes-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.notes-card h3,
.notes-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.compact-peek {
  background: transparent;
  padding: 0;
  border-radius: 0;
}
.latest {
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
.notes-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.notes-card .feature-overview {
  color: rgba(255, 255, 255, 0.76);
  margin: 8px 0;
}
.notes-card .feature-go-to {
  color: var(--module-color, #c8e6c9);
  font-weight: 600;
  margin-top: 12px;
}
</style>
