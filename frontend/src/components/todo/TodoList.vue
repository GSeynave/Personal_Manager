<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useTodoStore } from '@/stores/todo'
import { Button } from '@/components/ui/button'
import { Infinity, Plus, X, CircleCheck, Circle } from 'lucide-vue-next'
import TodoComponent from './TodoComponent.vue'

const todoStore = useTodoStore()
const showGroupForm = ref(false)
const newGroupTitle = ref('')
const newGroupDescription = ref('')
const draggedTodoId = ref<number | null>(null)
const draggedTodoSourceGroupId = ref<number | null | undefined>(undefined)
const dropTargetGroupId = ref<number | null>(null)
const activeGroupId = ref<number | null>(null)

const activeGroup = computed(() => {
  if (activeGroupId.value === null) {
    return null
  }
  return todoStore.filteredGroupedTodos.find(g => g.id === activeGroupId.value) || null
})

const activeTodos = computed(() => {
  if (activeGroupId.value === null) {
    return todoStore.filteredUngroupedTodos
  }
  return activeGroup.value?.todos || []
})

function getGroupCount(groupId: number): number {
  const group = todoStore.groupedTodos.find(g => g.id === groupId)
  return group?.todos.length || 0
}

function getGroupCompletedCount(groupId: number): number {
  const group = todoStore.groupedTodos.find(g => g.id === groupId)
  return group?.todos.filter(t => t.completed).length || 0
}

function setActiveGroup(groupId: number | null) {
  activeGroupId.value = groupId
}

onMounted(() => {
  todoStore.fetchTodos().then(() => {
    if (todoStore.filteredUngroupedTodos.length > 0) {
      activeGroupId.value = null
    } else {
      const groupWithTodos = todoStore.filteredGroupedTodos.find(g => g.todos.length > 0)
      activeGroupId.value = groupWithTodos?.id ?? null
    }
  })
})

async function handleCreateGroup() {
  if (!newGroupTitle.value.trim()) return
  
  try {
    await todoStore.createGroup(newGroupTitle.value, newGroupDescription.value)
    newGroupTitle.value = ''
    newGroupDescription.value = ''
    showGroupForm.value = false
  } catch (err) {
    console.error('Failed to create group:', err)
  }
}

function handleDragStart(todoId: string, event: DragEvent) {
  draggedTodoId.value = Number(todoId)
  draggedTodoSourceGroupId.value = activeGroupId.value
}

function handleDragOver(event: DragEvent, groupId: number | null) {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
  dropTargetGroupId.value = groupId
}

function handleDragLeave(event: DragEvent, groupId: number | null) {
  const relatedTarget = event.relatedTarget as HTMLElement
  const currentTarget = event.currentTarget as HTMLElement
  
  if (!currentTarget.contains(relatedTarget)) {
    if (dropTargetGroupId.value === groupId) {
      dropTargetGroupId.value = null
    }
  }
}

async function handleDrop(event: DragEvent, groupId: number | null) {
  event.preventDefault()
  
  if (draggedTodoId.value !== null) {
    if (draggedTodoSourceGroupId.value === groupId) {
      console.log('Todo already in this group, skipping update')
    } else {
      try {
        await todoStore.moveTodoToGroup(draggedTodoId.value, groupId)
      } catch (err) {
        console.error('Failed to move todo:', err)
      }
    }
  }
  
  draggedTodoId.value = null
  draggedTodoSourceGroupId.value = undefined
  dropTargetGroupId.value = null
}

function handleDragEnd() {
  draggedTodoId.value = null
  draggedTodoSourceGroupId.value = undefined
  dropTargetGroupId.value = null
}
</script>

<template>
    <p v-if="todoStore.isLoading" class="text-muted-foreground text-center py-4">Loading todos...</p>
    <p v-else-if="todoStore.error" class="text-destructive text-center py-4">{{ todoStore.error }}</p>
    
    <div v-else class="flex gap-3 min-h-[500px] bg-background">
      <div class="w-64 flex-shrink-0 flex flex-col bg-card backdrop-blur-sm rounded-xl p-3 shadow-md">
        <div class="flex flex-row gap-2 mb-4">
          <Button 
            variant="ghost"
            class="justify-start"
            :class="todoStore.filter === 'all' ? 'bg-primary/15 text-primary font-semibold' : 'hover:bg-background/60'"
            @click="todoStore.filter = 'all'"
          >
            <Infinity/> ({{ todoStore.totalCount }})
          </Button>
          <Button 
            variant="ghost"
            class="justify-start"
            :class="todoStore.filter === 'active' ? 'bg-primary/15 text-productivity font-semibold' : 'hover:bg-background/60 text-productivity'"
            @click="todoStore.filter = 'active'"
          >
            <Circle/> ({{ todoStore.activeCount }})
          </Button>
          <Button 
            variant="ghost"
            class="justify-start"
            :class="todoStore.filter === 'completed' ? 'bg-primary/15 text-green-600 font-semibold' : 'hover:bg-background/60 text-green-600'"
            @click="todoStore.filter = 'completed'"
          >
            <CircleCheck/> ({{ todoStore.completedCount }})
          </Button>
        </div>
        
        <div class="flex-1 overflow-y-auto flex flex-col gap-1">
          <div 
            class="px-4 py-3 cursor-pointer transition-all rounded-lg flex items-center justify-between"
            :class="[
              activeGroupId === null ? 'bg-primary/15 font-semibold text-primary' : 'hover:bg-background/40',
              dropTargetGroupId === null && draggedTodoId !== null ? 'bg-primary/25 ring-2 ring-primary/50' : ''
            ]"
            @click="setActiveGroup(null)"
            @dragover="handleDragOver($event, null)"
            @dragleave="handleDragLeave($event, null)"
            @drop="handleDrop($event, null)"
          >
            <span>Ungrouped</span>
            <span class="bg-background/80 px-2 py-0.5 rounded-full text-xs font-bold">
              <span :class="todoStore.ungroupedTodos.filter(t => t.completed).length === todoStore.ungroupedTodos.length ? 'text-green-600' : 'text-productivity'">{{ todoStore.ungroupedTodos.filter(t => t.completed).length }}</span>
              <span class="text-foreground/50"> / </span>
              <span class="text-green-600">{{ todoStore.ungroupedTodos.length }}</span>
            </span>
          </div>
          
          <div class="h-px bg-border/50 my-2"></div>
          
          <div 
            v-for="group in todoStore.groupedTodos" 
            :key="group.id"
            class="px-4 py-3 cursor-pointer transition-all rounded-lg flex items-center justify-between relative"
            :class="[
              activeGroupId === group.id ? 'bg-primary/15 font-semibold text-primary' : 'hover:bg-background/40',
              dropTargetGroupId === group.id && draggedTodoId !== null ? 'bg-primary/25 ring-2 ring-primary/50' : ''
            ]"
            @click="setActiveGroup(group.id)"
            @dragover="handleDragOver($event, group.id)"
            @dragleave="handleDragLeave($event, group.id)"
            @drop="handleDrop($event, group.id)"
          >
            <span>{{ group.title }}</span>
            <div class="flex items-center gap-2">
              <span class="bg-background/80 px-2 py-0.5 rounded-full text-xs font-bold">
                <span :class="getGroupCompletedCount(group.id) === getGroupCount(group.id) ? 'text-green-600' : 'text-productivity'">{{ getGroupCompletedCount(group.id) }}</span>
                <span class="text-foreground/50"> / </span>
                <span class="text-primary">{{ getGroupCount(group.id) }}</span>
              </span>
              <Button 
                v-if="getGroupCount(group.id) === 0"
                variant="ghost"
                size="icon"
                class="h-5 w-5 opacity-60 hover:opacity-100"
                :title="'Delete group'"
                @click.stop="todoStore.deleteGroup(group.id)"
              >
                <X class="h-4 w-4" />
              </Button>
            </div>
          </div>
          
          <div class="h-px bg-border/50 my-2"></div>
          
          <Button 
            variant="outline"
            class="mx-0 mb-2 border-dashed border-border/50 bg-background/60 hover:bg-background/80"
            @click="showGroupForm = !showGroupForm"
          >
            <Plus class="h-4 w-4 mr-2" />
            {{ showGroupForm ? 'Cancel' : 'New Group' }}
          </Button>
          
          <div v-if="showGroupForm" class="flex flex-col gap-2 p-3 bg-background/80 rounded-lg">
            <input 
              v-model="newGroupTitle" 
              placeholder="Group title" 
              class="px-3 py-2 border-0 rounded-md bg-card text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary/50"
              @keyup.enter="handleCreateGroup"
            />
            <input 
              v-model="newGroupDescription" 
              placeholder="Description (optional)" 
              class="px-3 py-2 border-0 rounded-md bg-card text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary/50"
              @keyup.enter="handleCreateGroup"
            />
            <Button size="sm" @click="handleCreateGroup">Create</Button>
          </div>
        </div>
      </div>
      
      <div class="flex-1 p-6 overflow-y-auto bg-card backdrop-blur-sm rounded-xl shadow-md">
        <div v-if="activeTodos.length > 0" class="flex flex-col gap-3">
          <TodoComponent
            v-for="todo in activeTodos"
            :key="todo.id"
            :id="String(todo.id)"
            :title="todo.title"
            :assigned_to="todo.assigned_to"
            :due_date="todo.due_date ? new Date(todo.due_date) : null"
            :completed="todo.completed"
            @onToggleCompletion="todoStore.toggleCompletion(todo)"
            @onDeleteTodo="todoStore.deleteTodo(todo.id!)"
            @onDragStart="(todoId, event) => handleDragStart(todoId, event)"
            @dragend="handleDragEnd"
          />
        </div>
        
        <div v-else class="flex items-center justify-center h-full">
          <p class="text-muted-foreground italic text-lg">No todos in this group</p>
        </div>
      </div>
    </div>
</template>

<style scoped>
@media (max-width: 768px) {
  .flex {
    flex-direction: column;
  }
  
  .w-64 {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid hsl(var(--border));
    max-height: 300px;
  }
}
</style>
