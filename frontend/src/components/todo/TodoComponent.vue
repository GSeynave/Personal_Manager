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
    <div>
      <span class="completion-icon">
        <i
          v-if="props.completed"
          class="pi pi-check"
          style="font-size: 1em; margin-right: 0.3em; color: #0e6e0bff"
        ></i>
        <i
          v-else
          class="pi pi-times"
          style="font-size: 1em; margin-right: 0.3em; color: #e00b0bff"
        ></i>
      </span>
      <span class="todo-title">
        {{ props.title }}
      </span>
      <span class="delete-icon">
        <i
          class="pi pi-trash"
          style="font-size: 1em; margin-right: 0.3em; cursor: pointer"
          @click="deleteTodo()"
        ></i>
      </span>
    </div>
    <div class="todo-details">
      <div v-if="props.assigned_to === 'BOTH'">
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage('PHONWALAI')"
        />
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage('GAUTHIER')"
        />{{ props.assigned_to }}
      </div>
      <div class="assigned-to" v-else>
        <img
          class="assignee-img"
          :alt="props.assigned_to || 'Unassigned'"
          :src="getAssigneeImage(props.assigned_to)"
        />{{ props.assigned_to }}
      </div>
      <div class="due-date">{{ props.due_date }}</div>
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
  border: 1px solid #eee;
  border-radius: 4px;
  margin: 0.5rem 0;
  cursor: pointer;
}
.completion-icon {
  cursor: pointer;
  margin-right: 0.5rem;
}
.delete-icon {
  float: right;
  cursor: pointer;
  margin-left: 1rem;
}
.todo-title {
  font-weight: bold;
}
.todo-details {
  display: flex;
  flex-direction: row;
  gap: 0.5rem;
}

.assigned-to {
  width: 50%;
  text-align: left;
  font-size: 0.8em;
  color: #666;
}
.due-date {
  width: 50%;
  text-align: right;
  font-size: 0.8em;
  color: #666;
}
.assignee-img {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  margin-right: 0.3rem;
  vertical-align: middle;
}
</style>
