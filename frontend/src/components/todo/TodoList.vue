<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useTodoStore } from '@/stores/todo'
import type { TodoFilter } from '@/stores/todo'
import CardContent from '../Dashboard/CardContent.vue'
import TodoComponent from './TodoComponent.vue'
import type TodoGroup from '@/model/TodoGroup'

const todoStore = useTodoStore()
const showGroupForm = ref(false)
const newGroupTitle = ref('')
const newGroupDescription = ref('')
const draggedTodoId = ref<number | null>(null)
const draggedTodoSourceGroupId = ref<number | null | undefined>(undefined)
const dropTargetGroupId = ref<number | null>(null)
const activeGroupId = ref<number | null>(null) // null = ungrouped

// Determine which group/todos to display
const activeGroup = computed(() => {
  if (activeGroupId.value === null) {
    return null // Display ungrouped
  }
  return todoStore.filteredGroupedTodos.find(g => g.id === activeGroupId.value) || null
})

const activeTodos = computed(() => {
  if (activeGroupId.value === null) {
    return todoStore.filteredUngroupedTodos
  }
  return activeGroup.value?.todos || []
})

function setActiveGroup(groupId: number | null) {
  activeGroupId.value = groupId
}

onMounted(() => {
  todoStore.fetchTodos().then(() => {
    // Set initial active group
    if (todoStore.filteredUngroupedTodos.length > 0) {
      activeGroupId.value = null // Show ungrouped
    } else {
      // Find first group with todos
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

function updateDragPreview(event: DragEvent) {
  // No longer needed
}

function handleDragOver(event: DragEvent, groupId: number | null) {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
  dropTargetGroupId.value = groupId
}

function handleDragLeave(event: DragEvent, groupId: number | null) {
  // Only clear if we're leaving to a non-child element
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
    // Check if dropping in the same group
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
  <CardContent :title="'Todo List'">
    <div class="filters">
      <button 
        :class="{ active: todoStore.filter === 'all' }"
        @click="todoStore.filter = 'all'"
      >
        All ({{ todoStore.totalCount }})
      </button>
      <button 
        :class="{ active: todoStore.filter === 'active' }"
        @click="todoStore.filter = 'active'"
      >
        Active ({{ todoStore.activeCount }})
      </button>
      <button 
        :class="{ active: todoStore.filter === 'completed' }"
        @click="todoStore.filter = 'completed'"
      >
        Completed ({{ todoStore.completedCount }})
      </button>
      
      <button class="create-group-btn" @click="showGroupForm = !showGroupForm">
        {{ showGroupForm ? 'Cancel' : '+ New Group' }}
      </button>
    </div>
    
    <div v-if="showGroupForm" class="group-form">
      <input 
        v-model="newGroupTitle" 
        placeholder="Group title" 
        @keyup.enter="handleCreateGroup"
      />
      <input 
        v-model="newGroupDescription" 
        placeholder="Description (optional)" 
        @keyup.enter="handleCreateGroup"
      />
      <button @click="handleCreateGroup">Create Group</button>
    </div>
    
    <p v-if="todoStore.isLoading">Loading todos...</p>
    <p v-else-if="todoStore.error" class="error">{{ todoStore.error }}</p>
    
    <div v-else class="todo-view">
      <!-- Group Navigation Chips -->
      <div class="group-chips">
        <div 
          class="group-chip"
          :class="{ 
            'chip-active': activeGroupId === null,
            'chip-drop-target': dropTargetGroupId === null && draggedTodoId !== null
          }"
          @click="setActiveGroup(null)"
          @dragover="handleDragOver($event, null)"
          @dragleave="handleDragLeave($event, null)"
          @drop="handleDrop($event, null)"
        >
          <span class="chip-name">Ungrouped</span>
          <span class="chip-count">{{ todoStore.filteredUngroupedTodos.length }}</span>
        </div>
        
        <div 
          v-for="group in todoStore.filteredGroupedTodos" 
          :key="group.id"
          class="group-chip"
          :class="{ 
            'chip-active': activeGroupId === group.id,
            'chip-drop-target': dropTargetGroupId === group.id && draggedTodoId !== null
          }"
          @click="setActiveGroup(group.id)"
          @dragover="handleDragOver($event, group.id)"
          @dragleave="handleDragLeave($event, group.id)"
          @drop="handleDrop($event, group.id)"
        >
          <span class="chip-name">{{ group.title }}</span>
          <span class="chip-count">{{ group.todos.length }}</span>
          <button 
            v-if="group.todos.length === 0"
            class="chip-delete-btn"
            :title="'Delete group'"
            @click.stop="todoStore.deleteGroup(group.id)"
          >
            ×
          </button>
        </div>
      </div>
      
      <!-- Active Group Display -->
      <div class="active-group-container">
        <div v-if="activeTodos.length > 0" class="todo-grid">
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
        
        <div v-else class="empty-state">
          <p>No todos in this group</p>
        </div>
      </div>
    </div>
  </CardContent>
</template>

<style scoped>
.filters {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.filters button {
  padding: 0.5rem 1rem;
  border: 1px solid var(--accent);
  background: var(--surface);
  color: var(--text);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.filters button:hover {
  background: var(--bg);
  border-color: var(--primary);
}

.filters button.active {
  background: var(--primary);
  border-color: var(--primary);
  color: var(--surface);
  font-weight: 600;
}

.create-group-btn {
  margin-left: auto;
  background: var(--accent) !important;
  color: var(--text) !important;
}

.create-group-btn:hover {
  background: var(--primary) !important;
  color: var(--surface) !important;
}

.group-form {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  padding: 1rem;
  background: var(--bg);
  border-radius: 8px;
  border: 1px solid var(--accent);
}

.group-form input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid var(--accent);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
}

.group-form input:focus {
  outline: none;
  border-color: var(--primary);
}

.group-form button {
  padding: 0.5rem 1rem;
  background: var(--primary);
  color: var(--surface);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.group-form button:hover {
  background: var(--accent);
}

.todo-view {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.group-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  padding: 1rem;
  background: var(--surface);
  border-radius: 12px;
  border: 1px solid var(--accent);
}

.group-chip {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.75rem;
  background: var(--bg);
  border: 2px solid var(--accent);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.85rem;
  position: relative;
}

.group-chip:hover {
  border-color: var(--primary);
  background: rgba(232, 149, 111, 0.1);
  transform: translateY(-2px);
}

.chip-active {
  background: var(--primary);
  border-color: var(--primary);
  color: var(--surface);
  font-weight: 600;
}

.chip-drop-target {
  background: var(--primary);
  border-color: var(--primary);
  color: var(--surface);
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(232, 149, 111, 0.4);
}

.chip-name {
  font-weight: 600;
}

.chip-count {
  background: rgba(0, 0, 0, 0.1);
  padding: 0.15rem 0.5rem;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
}

.chip-active .chip-count,
.chip-drop-target .chip-count {
  background: rgba(255, 255, 255, 0.3);
}

.chip-delete-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  line-height: 1;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s;
  padding: 0 0 0 0.25rem;
  color: inherit;
}

.chip-delete-btn:hover {
  opacity: 1;
}

.active-group-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.todo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
}

@media (min-width: 1400px) {
  .todo-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 900px) {
  .todo-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .todo-grid {
    grid-template-columns: 1fr;
  }
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--text-secondary);
  font-style: italic;
}

.empty-state p {
  margin: 0;
  font-size: 1.1rem;
}

@media (max-width: 600px) {
  .todo-item-container {
    grid-template-columns: 1fr;
  }
}
</style>
