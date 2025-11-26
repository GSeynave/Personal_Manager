<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    activeProjects?: number
    tasksDue?: number
    teamMembers?: number
  }
}>()

const projectStatus = computed(() => {
  const active = props.data?.activeProjects ?? 0
  const due = props.data?.tasksDue ?? 0
  if (due > 0) return `⚠️ ${due} due`
  if (active > 0) return `✓ ${active} active`
  return '○ No projects'
})

const summaryText = computed(() => {
  const items = []
  if (props.data?.activeProjects) items.push(`${props.data.activeProjects} projects`)
  if (props.data?.teamMembers) items.push(`${props.data.teamMembers} team`)
  return items.join(' • ') || 'No active projects'
})
</script>

<template>
  <div class="projects-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-list" style="font-size: 1em; margin-right: 0.3em"></i>
        Projects
      </h3>
      <div class="compact-peek">
        <span class="status">{{ projectStatus }}</span>
      </div>
      <div class="compact-summary">{{ summaryText }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-list" style="font-size: 1em; margin-right: 0.3em"></i>
        Projects
      </h2>
      <p class="feature-description">Track projects, milestones, and team collaboration.</p>
      <p class="feature-overview">
        Active: {{ props.data?.activeProjects ?? '0' }}, Tasks due:
        {{ props.data?.tasksDue ?? '0' }}, Team: {{ props.data?.teamMembers ?? '0' }}
      </p>
      <p class="feature-go-to">Go to Projects -></p>
    </template>
  </div>
</template>

<style scoped>
.projects-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.projects-card h3,
.projects-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.compact-peek {
  /* layout only; shell controls surface */
  padding: 0;
  background: transparent;
  border-radius: 0;
  text-align: center;
}
.status {
  display: block;
}
.compact-summary {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 8px;
  text-align: center;
}
.projects-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.projects-card .feature-overview {
  color: rgba(255, 255, 255, 0.76);
  margin: 8px 0;
}
.projects-card .feature-go-to {
  color: var(--module-color, #90caf9);
  font-weight: 600;
  margin-top: 12px;
}
</style>
