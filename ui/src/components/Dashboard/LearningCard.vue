<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: {
    activeCourses?: number
    skillsLearning?: number
    currentRead?: string
  }
}>()

const courseStatus = computed(() => {
  const courses = props.data?.activeCourses ?? 0
  if (courses > 0) return `📚 ${courses} courses`
  return '○ Start learning'
})

const skillsDisplay = computed(() => {
  const skills = props.data?.skillsLearning ?? 0
  const book = props.data?.currentRead
  const items = []
  if (skills > 0) items.push(`${skills} skills`)
  if (book) items.push(`Reading: ${book.substring(0, 20)}...`)
  return items.join(' • ') || 'No active learning'
})
</script>

<template>
  <div class="learning-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-graduation-cap" style="font-size: 1em; margin-right: 0.3em"></i>
        Learning
      </h3>
      <div class="compact-peek">
        <span class="status">{{ courseStatus }}</span>
      </div>
      <div class="compact-summary">{{ skillsDisplay }}</div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-graduation-cap" style="font-size: 1em; margin-right: 0.3em"></i>
        Learning
      </h2>
      <p class="feature-description">Courses, skills, and reading lists.</p>
      <p class="feature-overview">
        Courses: {{ props.data?.activeCourses ?? '0' }}, Skills:
        {{ props.data?.skillsLearning ?? '0' }}, Reading: {{ props.data?.currentRead ?? 'None' }}
      </p>
      <p class="feature-go-to">Go to Learning -></p>
    </template>
  </div>
</template>

<style scoped>
.learning-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.learning-card h3,
.learning-card h2 {
  margin: 0 0 12px 0;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}
.learning-card .course {
  background: rgba(0, 0, 0, 0.06);
  padding: 8px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.learning-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  margin: 8px 0;
}
.learning-card .feature-go-to {
  color: var(--module-color, #b3e5fc);
  font-weight: 600;
  margin-top: 12px;
}
</style>
