<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits(['onToggleCompletion', 'onDeleteTodo', 'onDragStart'])
const props = defineProps<{
  id: string
  title: string
  assigned_to: string | null
  due_date: Date | null
  completed: boolean
}>()

const isDragging = ref(false)

function onToggleCompletion() {
  emit('onToggleCompletion', { id: props.id })
}
function deleteTodo() {
  console.log(`Delete todo with id: ${props.id}`)
  emit('onDeleteTodo', { id: props.id })
}

function handleDragStart(event: DragEvent) {
  isDragging.value = true
  
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('todoId', props.id)
    
    // Create a custom drag image without background
    const target = event.currentTarget as HTMLElement
    if (target) {
      const clone = target.cloneNode(true) as HTMLElement
      clone.style.position = 'absolute'
      clone.style.top = '-9999px'
      clone.style.backgroundColor = 'transparent'
      clone.style.opacity = '0.6'
      document.body.appendChild(clone)
      
      event.dataTransfer.setDragImage(clone, -20, -20)
      
      // Remove clone after drag image is created
      setTimeout(() => {
        document.body.removeChild(clone)
      }, 0)
    }
  }
  emit('onDragStart', props.id, event)
}

function handleDragEnd() {
  isDragging.value = false
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
  <div 
    :class="['todo-item', { completed: props.completed, 'is-dragging-self': isDragging }]" 
    draggable="true"
    @dragstart="handleDragStart"
    @dragend="handleDragEnd"
    @click="onToggleCompletion"
  >
    <span class="delete-icon" @click.stop="deleteTodo()">
      <i
        class="pi pi-times"
        style="font-size: 1.2em; cursor: pointer"
      ></i>
    </span>
    <div>
      <span class="completion-icon">
        <i
          v-if="props.completed"
          class="pi pi-check"
          :style="{ fontSize: '1em', marginRight: '0.3em', color: 'var(--success)' }"
        ></i>
        <i
          v-else
          class="pi pi-circle"
          :style="{ fontSize: '1em', marginRight: '0.3em', color: 'var(--text-secondary)' }"
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
  background-color: rgba(124, 152, 133, 0.15);
  opacity: 0.7;
  text-decoration: line-through;
  color: var(--text-secondary);
}

.todo-item {
  padding: 0.75rem 1rem;
  padding-right: 2rem;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  position: relative;
  overflow: visible;
  cursor: grab;
  background: var(--surface);
  color: var(--text);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  box-shadow: 0 1px 3px var(--shadow-color);
}

.todo-item.is-dragging-self {
  opacity: 0.4 !important;
  transform: scale(0.95);
  cursor: grabbing !important;
}

.todo-item:active {
  cursor: grabbing;
}

.todo-item:hover {
  box-shadow: 0 3px 10px var(--shadow-color-hover);
  border-color: var(--primary);
  transform: translateY(-2px);
}

.completion-icon {
  cursor: pointer;
  margin-right: 0.5rem;
  flex-shrink: 0;
}

.delete-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  z-index: 10;
  background: var(--bg);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 2px var(--shadow-color);
  transition: all 0.2s;
  color: var(--accent);
}

.delete-icon:hover {
  background: var(--gentle-alert);
  color: var(--surface);
  transform: scale(1.1);
}

.todo-title {
  font-weight: 600;
  color: var(--text);
  flex: 1;
}

.todo-details {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

.assignee-images {
  display: flex;
  align-items: center;
  gap: 0.2rem;
}

.due-date {
  font-size: 0.85em;
  color: var(--text-secondary);
  white-space: nowrap;
}

.assignee-img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  vertical-align: middle;
  cursor: pointer;
  border: 2px solid var(--accent-light);
}
</style>
