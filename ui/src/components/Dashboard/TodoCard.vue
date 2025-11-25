<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    title?: string
    priority?: 'high' | 'medium' | 'low'
    daysUntilDue?: number
    isOverdue?: boolean
  }
}>()

const priorityDisplay = computed(() => {
  const p = props.data?.priority ?? 'medium'
  return { high: '🔴', medium: '🟡', low: '🟢' }[p] || '🟡'
})

const dueDisplay = computed(() => {
  if (props.data?.isOverdue) return 'Overdue'
  const days = props.data?.daysUntilDue ?? 0
  if (days === 0) return 'Today'
  if (days === 1) return 'Tomorrow'
  return `In ${days} days`
})
</script>

<template>
  <div class="todo-card">
    <template v-if="props.compact">
      <div class="compact">
        <h3>
          <i class="pi pi-check-square" style="font-size: 1em; margin-right: 0.3em"></i>
          Todo
        </h3>
        <div class="compact-peek">
          <span class="priority">{{ priorityDisplay }}</span>
          <span class="title">{{ props.data?.title ?? 'No tasks' }}</span>
        </div>
        <div class="compact-due">{{ dueDisplay }}</div>
      </div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-check-square" style="font-size: 1em; margin-right: 0.3em"></i>
        Todo
      </h2>
      <p class="feature-description">Organize your tasks and stay productive.</p>
      <p class="feature-overview">Next task: {{ (props.data as any)?.title ?? 'Buy groceries' }}</p>
      <p class="feature-go-to">Go to Todo -></p>
    </template>
  </div>
</template>

<style scoped>
.todo-card {
  /* minimal root - dashboard shell supplies the surface and accents */
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.todo-card h3 {
  margin: 0 0 12px 0;
  font-size: 1.05rem;
  font-weight: 600;
}

.compact-peek {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 0.95rem;
  background: transparent; /* flush with dashboard shell */
  padding: 10px 12px;
  border-radius: 6px;
}

.priority {
  font-size: 1.15rem;
  flex-shrink: 0;
  color: var(--module-color, #6c5ce7);
  text-shadow: 0 1px 0 rgba(0, 0, 0, 0.3);
}

.title {
  font-weight: 600;
  color: var(--card-foreground, #fff);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* make the data peak more visible: filled pill using module color */
.compact-due {
  font-size: 0.78rem;
  color: #fff;
  background: var(--module-color, #6c5ce7); /* keep pill accent, but remove inner gradient */
  margin-top: 8px;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  text-align: center;
  font-weight: 700;
  padding: 6px 10px;
  border-radius: 999px;
  display: inline-block;
}

.todo-card h2 {
  margin: 0 0 12px 0;
  font-size: 1.5rem;
}

.todo-card .feature-description {
  color: rgba(255, 255, 255, 0.78);
  font-size: 0.9rem;
  margin: 8px 0;
}

.todo-card .feature-overview {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.95rem;
  margin: 8px 0;
}

.todo-card .feature-go-to {
  color: var(--module-color, #6c5ce7);
  font-weight: 600;
  margin-top: 12px;
}
</style>
