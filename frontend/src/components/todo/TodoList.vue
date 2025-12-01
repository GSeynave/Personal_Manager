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

// Get unfiltered count for each group
function getGroupCount(groupId: number): number {
  const group = todoStore.groupedTodos.find(g => g.id === groupId)
  return group?.todos.length || 0
}

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
    <p v-if="todoStore.isLoading">Loading todos...</p>
    <p v-else-if="todoStore.error" class="error">{{ todoStore.error }}</p>
    
    <div v-else class="todo-container">
      <!-- Left Sidebar -->
      <div class="sidebar">
        <!-- Filter Buttons -->
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
        </div>
        
        <!-- Group List -->
        <div class="groups-list">
          <div 
            class="group-item"
            :class="{ 
              'group-active': activeGroupId === null,
              'group-drop-target': dropTargetGroupId === null && draggedTodoId !== null
            }"
            @click="setActiveGroup(null)"
            @dragover="handleDragOver($event, null)"
            @dragleave="handleDragLeave($event, null)"
            @drop="handleDrop($event, null)"
          >
            <span class="group-name">Ungrouped</span>
            <span class="group-count">{{ todoStore.ungroupedTodos.length }}</span>
          </div>
          
          <div class="separator"></div>
          
          <div 
            v-for="group in todoStore.groupedTodos" 
            :key="group.id"
            class="group-item"
            :class="{ 
              'group-active': activeGroupId === group.id,
              'group-drop-target': dropTargetGroupId === group.id && draggedTodoId !== null
            }"
            @click="setActiveGroup(group.id)"
            @dragover="handleDragOver($event, group.id)"
            @dragleave="handleDragLeave($event, group.id)"
            @drop="handleDrop($event, group.id)"
          >
            <span class="group-name">{{ group.title }}</span>
            <span class="group-count">{{ getGroupCount(group.id) }}</span>
            <button 
              v-if="getGroupCount(group.id) === 0"
              class="group-delete-btn"
              :title="'Delete group'"
              @click.stop="todoStore.deleteGroup(group.id)"
            >
              ×
            </button>
          </div>
          
          <div class="separator"></div>
          
          <button class="create-group-btn" @click="showGroupForm = !showGroupForm">
            {{ showGroupForm ? 'Cancel' : '+ New Group' }}
          </button>
          
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
            <button @click="handleCreateGroup">Create</button>
          </div>
        </div>
      </div>
      
      <!-- Right Content Area -->
      <div class="content-area">
        <div v-if="activeTodos.length > 0" class="todo-list">
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
</template>

<style scoped>
.todo-container {
  display: flex;
  gap: 1.5rem;
  min-height: 500px;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  background: var(--bg-soft);
  box-shadow: 0 2px 8px var(--shadow-color);
}

/* Left Sidebar */
.sidebar {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-right: 2px solid var(--border-color);
  background: var(--bg);
  border-radius: 16px 0 0 16px;
}

.filters {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.75rem;
  background: var(--bg-soft);
}

.filters button {
  width: 90%;
  padding: 0.75rem 1rem;
  border: none;
  background: var(--surface);
  color: var(--text);
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 1px 3px var(--shadow-color);
}

.filters button:hover {
  background: var(--bg);
  transform: translateX(4px);
  box-shadow: 0 2px 6px var(--shadow-color-hover);
  color: var(--primary);
}

.filters button.active {
  background: linear-gradient(135deg, var(--primary) 0%, var(--accent) 100%);
  color: var(--surface);
  font-weight: 600;
  box-shadow: 0 3px 8px var(--shadow-color-hover);
  transform: translateX(4px);
}

/* Groups List */
.groups-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.separator {
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--border-color-hover) 50%, transparent 100%);
  margin: 0.75rem 1rem;
}

.group-item {
  padding: 0.75rem 1rem;
  margin: 0 0.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 10px;
  position: relative;
  color: var(--text);
}

.group-item:hover {
  background: var(--bg);
  transform: translateX(4px);
  box-shadow: 0 2px 8px var(--shadow-color);
}

.group-active {
  background: linear-gradient(90deg, var(--accent-light) 0%, var(--bg) 100%);
  font-weight: 600;
  border-left: 4px solid var(--primary);
  padding-left: calc(1rem - 4px);
  box-shadow: 0 2px 8px var(--shadow-color-hover);
  color: var(--primary);
}

.group-drop-target {
  background: linear-gradient(90deg, var(--primary) 0%, var(--accent) 100%);
  color: var(--surface);
  font-weight: 600;
  box-shadow: 0 4px 12px var(--shadow-color-hover);
  transform: scale(1.02);
}

.group-name {
  flex: 1;
}

.group-count {
  background: var(--accent-light);
  color: var(--primary);
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-left: 0.5rem;
  box-shadow: 0 1px 3px var(--shadow-color);
}

.group-active .group-count {
  background: var(--primary);
  color: var(--surface);
}

.group-drop-target .group-count {
  background: rgba(255, 255, 255, 0.3);
  color: var(--surface);
}

.group-delete-btn {
  background: none;
  border: none;
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s;
  padding: 0;
  margin-left: 0.5rem;
  color: var(--gentle-alert);
}

.group-delete-btn:hover {
  opacity: 1;
}

.create-group-btn {
  margin: 0.5rem 0.75rem;
  padding: 0.65rem 1rem;
  background: var(--bg);
  color: var(--primary);
  border: 2px dashed var(--border-color-hover);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.create-group-btn:hover {
  background: var(--primary);
  color: var(--surface);
  border-color: var(--primary);
  border-style: solid;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color-hover);
}

.group-form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.5rem;
  background: var(--bg-soft);
  border-top: 1px solid var(--border-color);
}

.group-form input {
  padding: 0.5rem;
  border: 1px solid var(--border-color-hover);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
  font-size: 0.9rem;
}

.group-form input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--shadow-color);
}

.group-form button {
  padding: 0.5rem;
  background: var(--primary);
  color: var(--surface);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.9rem;
}

.group-form button:hover {
  background: var(--accent);
  box-shadow: 0 2px 8px var(--shadow-color-hover);
}

/* Right Content Area */
.content-area {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
  background: var(--bg-soft);
  border-radius: 0 16px 16px 0;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--text-secondary);
  font-style: italic;
}

.empty-state p {
  margin: 0;
  font-size: 1.1rem;
}

.error {
  color: var(--gentle-alert);
  padding: 1rem;
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .todo-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--accent);
    max-height: 300px;
  }
  
  .filters {
    flex-direction: row;
    overflow-x: auto;
  }
  
  .filters button {
    white-space: nowrap;
    border-right: 1px solid var(--accent);
    border-bottom: none;
  }
  
  .filters button:last-child {
    border-right: none;
  }
}
</style>
