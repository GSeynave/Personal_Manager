<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Check, Circle, CircleCheck, X } from 'lucide-vue-next'

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
    
    const target = event.currentTarget as HTMLElement
    if (target) {
      const clone = target.cloneNode(true) as HTMLElement
      clone.style.position = 'absolute'
      clone.style.top = '-9999px'
      clone.style.backgroundColor = 'transparent'
      clone.style.opacity = '0.6'
      document.body.appendChild(clone)
      
      event.dataTransfer.setDragImage(clone, -20, -20)
      
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

  return images[DEFAULT_IMAGE_PATH]
}
</script>

<template>
  <div 
    :class="[
      'bg-card-item',
      'group relative flex items-center justify-between gap-4 p-4 border rounded-lg transition-all cursor-grab',
      'hover:shadow-md hover:border-primary hover:-translate-y-0.5',
      completed ? 'bg-muted/50 opacity-70 line-through' : 'bg-card',
      isDragging ? 'opacity-40 scale-95 cursor-grabbing' : ''
    ]" 
    draggable="true"
    @dragstart="handleDragStart"
    @dragend="handleDragEnd"
    @click="onToggleCompletion"
  >
    <Button
      variant="ghost"
      size="icon"
      class="absolute top-1 right-1 h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-destructive/10"
      @click.stop="deleteTodo"
    >
      <X class="w-4 h-4 text-destructive" />
    </Button>

    <div class="flex items-center gap-3 flex-1 min-w-0">
      <div class="flex-shrink-0">
        <CircleCheck v-if="completed" class="w-5 h-5 text-green-600" />
        <Circle v-else class="w-5 h-5 text-productivity" />
      </div>
      
      <span class="font-medium text-foreground truncate">
        {{ props.title }}
      </span>
    </div>
    
    <div class="flex items-center gap-3 flex-shrink-0">
      <div v-if="props.assigned_to === 'BOTH'" class="flex -space-x-2">
        <img
          class="h-6 w-6 rounded-full border-2 border-background"
          :alt="props.assigned_to"
          :title="props.assigned_to"
          :src="getAssigneeImage('PHONWALAI')"
        />
        <img
          class="h-6 w-6 rounded-full border-2 border-background"
          :alt="props.assigned_to"
          :title="props.assigned_to"
          :src="getAssigneeImage('GAUTHIER')"
        />
      </div>
      <img
        v-else
        class="h-6 w-6 rounded-full border-2 border-background"
        :alt="props.assigned_to || 'Unassigned'"
        :title="props.assigned_to || 'Unassigned'"
        :src="getAssigneeImage(props.assigned_to)"
      />
      
      <Badge v-if="props.due_date" variant="outline" class="text-xs">
        {{ formatDate(props.due_date) }}
      </Badge>
    </div>
  </div>
</template>

<style scoped>
div:active {
  cursor: grabbing;
}
</style>
