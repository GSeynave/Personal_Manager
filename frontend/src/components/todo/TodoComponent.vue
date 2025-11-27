<script setup lang="ts">
const emit = defineEmits(['onToggleCompletion', 'onDeleteTodo'])
const props = defineProps<{
  id: string
  title: string
  assigned_to: string | null
  due_date: Date | null
  completed: boolean
}>()

function onToggleCompletion() {
  emit('onToggleCompletion', { id: props.id })
}
function deleteTodo() {
  // Placeholder for delete functionality
  console.log(`Delete todo with id: ${props.id}`)
  emit('onDeleteTodo', { id: props.id })
}

function formatDate(date: Date | null): string {
  if (!date) return ''
  
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  
  return `${year}-${month}-${day}`
}

// Map available images in `src/assets/images` at build time.
// We use import.meta.glob with { eager: true, as: 'url' } so Vite returns
// a map of file paths -> resolved URLs. This makes lookups safe and
// avoids referencing missing files directly.
const images = import.meta.glob('../../assets/images/*.{jpg,png,svg}', {
  eager: true,
  as: 'url',
}) as Record<string, string>
const DEFAULT_IMAGE_PATH = '../../assets/images/default-avatar.svg'

function getAssigneeImage(name: string | null) {
  if (!name) return images[DEFAULT_IMAGE_PATH]

  const base = name.toLowerCase()
  const candidates = [
    `../../assets/images/${base}.jpg`,
    `../../assets/images/${base}.png`,
    `../../assets/images/${base}.svg`,
  ]

  for (const path of candidates) {
    if (path in images) return images[path]
  }

  // fallback to default avatar
  return images[DEFAULT_IMAGE_PATH]
}
</script>

<template>
  <div :class="['todo-item', { completed: props.completed }]" @click="onToggleCompletion">
    <span class="delete-icon" @click.stop="deleteTodo()">
      <i
        class="pi pi-times"
        style="font-size: 1.2em; color: #e00b0bff; cursor: pointer"
      ></i>
    </span>
    <div>
      <span class="completion-icon">
        <i
          v-if="props.completed"
          class="pi pi-check"
          style="font-size: 1em; margin-right: 0.3em; color: #0e6e0bff"
        ></i>
        <i
          v-else
          class="pi pi-circle"
          style="font-size: 1em; margin-right: 0.3em; color: #999"
        ></i>
      </span>
      <span class="todo-title">
        {{ props.title }}
      </span>
    </div>
    <div class="todo-details">
      <div v-if="props.assigned_to === 'BOTH'" class="assignee-images">
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :title="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage('PHONWALAI')"
        />
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :title="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage('GAUTHIER')"
        />
      </div>
      <div class="assignee-images" v-else>
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :title="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage(props.assigned_to)"
        />
      </div>
      <div class="due-date">{{ formatDate(props.due_date) }}</div>
    </div>
  </div>
</template>

<style scoped>
.todo-item.completed {
  background-color: #d4edda;
  text-decoration: line-through;
  color: #6c757d;
}
.todo-item {
  padding: 0.5rem;
  padding-right: 1.5rem;
  border: 1px solid #eee;
  border-radius: 4px;
  margin: 0.5rem 0;
  position: relative;
  overflow: visible;
  cursor: pointer;
}
.completion-icon {
  cursor: pointer;
  margin-right: 0.5rem;
}
.delete-icon {
  position: absolute;
  top: -8px;
  right: -8px;
  cursor: pointer;
  z-index: 10;
  background: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
.todo-title {
  font-weight: bold;
}
.todo-details {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
}

.assignee-images {
  display: flex;
  align-items: center;
  gap: 0.2rem;
}

.due-date {
  text-align: right;
  font-size: 0.8em;
  color: #666;
}
.assignee-img {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  vertical-align: middle;
  cursor: pointer;
}
</style>
