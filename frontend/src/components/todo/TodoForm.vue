<script setup lang="ts">
import { ref } from 'vue'
import { useTodoStore } from '@/stores/todo'
import Todo from '@/model/Todo'
import CardContent from '../Dashboard/CardContent.vue'
import { useAuthStore } from '@/stores/auth'

const todoStore = useTodoStore()
const authStore = useAuthStore()

const title = ref('')
const dueDate = ref('')
const selectedGroupId = ref<number | undefined>(undefined)

async function createTodo() {
  try {
    const userTag = authStore.userIdentity?.userTag || authStore.userEmail || ''
    
    const newTodo = new Todo(
      title.value,
      false,
      undefined,
      dueDate.value || null,
      userTag
    )
    
    console.log('Creating todo:', newTodo, 'in group:', selectedGroupId.value)
    await todoStore.addTodo(newTodo, selectedGroupId.value)
    console.log('Todo created successfully')
    
    // Clear form
    title.value = ''
    dueDate.value = ''
    selectedGroupId.value = undefined
  } catch (err) {
    console.error('Error creating todo:', err)
  }
}
</script>

<template>
  <CardContent :title="'Create Todo'">
    <form class="todo-form-container" @submit.prevent="createTodo">
      <div class="form-group">
        <label for="title">Title:</label>
        <input type="text" id="title" v-model="title" required />
      </div>
      <div class="form-group">
        <label for="dueDate">Due Date:</label>
        <input type="date" id="dueDate" v-model="dueDate" />
      </div>
      <div class="form-group">
        <label for="group">Group:</label>
        <select id="group" v-model="selectedGroupId">
          <option :value="undefined">Ungrouped</option>
          <option v-for="group in todoStore.groupedTodos" :key="group.id" :value="group.id">
            {{ group.title }}
          </option>
        </select>
      </div>
      <button type="submit" class="submit-button" :disabled="todoStore.isLoading">
        {{ todoStore.isLoading ? 'Creating...' : 'Create Todo' }}
      </button>
    </form>
  </CardContent>
</template>

<style scoped>
.todo-form-container {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: flex-end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.form-group label {
  font-weight: 600;
  color: var(--text);
  font-size: 0.9em;
}

.form-group input {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--accent);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
  font-size: 1em;
  transition: all 0.2s;
  min-width: 200px;
}

.form-group select {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--accent);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
  font-size: 1em;
  transition: all 0.2s;
  min-width: 200px;
  cursor: pointer;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(232, 149, 111, 0.1);
}

.form-group input:hover,
.form-group select:hover {
  border-color: var(--primary);
}

.submit-button {
  padding: 0.5rem 1.5rem;
  background: var(--primary);
  color: var(--surface);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
  font-size: 1em;
}

.submit-button:hover:not(:disabled) {
  background: var(--accent);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(232, 149, 111, 0.3);
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
