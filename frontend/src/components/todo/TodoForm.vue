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
    
    console.log('Creating todo:', newTodo)
    await todoStore.addTodo(newTodo)
    console.log('Todo created successfully')
    
    // Clear form
    title.value = ''
    dueDate.value = ''
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
  align-items: center;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.submit-button {
  align-self: flex-end;
  padding: 0.5rem 1rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  align-self: center;
}
</style>
