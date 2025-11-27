<script setup lang="ts">
import { onMounted } from 'vue'
import { useTodoStore } from '@/stores/todo'
import type { TodoFilter } from '@/stores/todo'
import CardContent from '../Dashboard/CardContent.vue'
import TodoComponent from './TodoComponent.vue'

const todoStore = useTodoStore()

onMounted(() => {
  todoStore.fetchTodos()
})
</script>

<template>
  <CardContent :title="'Todo List'">
    <div class="filters">
      <button 
        :class="{ active: todoStore.filter === 'all' }"
        @click="todoStore.filter = 'all'"
      >
        All ({{ todoStore.todos.length }})
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
    
    <p v-if="todoStore.isLoading">Loading todos...</p>
    <p v-else-if="todoStore.error" class="error">{{ todoStore.error }}</p>
    <div v-else class="todo-item-container">
      <TodoComponent
        v-for="todo in todoStore.filteredTodos"
        :key="todo.id"
        :id="String(todo.id)"
        :title="todo.title"
        :assigned_to="todo.assigned_to"
        :due_date="todo.due_date ? new Date(todo.due_date) : null"
        :completed="todo.completed"
        @onToggleCompletion="todoStore.toggleCompletion(todo)"
        @onDeleteTodo="todoStore.deleteTodo(todo.id!)"
      />
    </div>
  </CardContent>
</template>

<style scoped>
.filters {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.filters button {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  background: var(--color-background);
  color: var(--color-text);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.filters button:hover {
  background: var(--color-background-soft);
}

.filters button.active {
  background: var(--color-background-mute);
  border-color: var(--color-border-hover);
  font-weight: 600;
}

.todo-item-container {
  margin-bottom: 0.5rem;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
}

@media (min-width: 1400px) {
  .todo-item-container {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 900px) {
  .todo-item-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .todo-item-container {
    grid-template-columns: 1fr;
  }
}
</style>
