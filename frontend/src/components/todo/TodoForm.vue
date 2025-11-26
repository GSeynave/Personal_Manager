<script setup lang="ts">
import { ref } from 'vue'
import TodoService from '../../services/TodoService'
import Todo from '@/model/Todo'
import CardContent from '../Dashboard/CardContent.vue'

const todoService = new TodoService()
const todoForm = ref<Todo>(new Todo('', '', new Date(), '', false))

async function createTodo() {
  try {
    await todoService.addTodo(todoForm.value!)
    console.log('Todo created')
  } catch (err) {
    console.error('Error updating todo:', err)
  }
}
</script>

<template>
  <CardContent :title="'Create Todo'">
    <form class="todo-form-container" @submit.prevent="createTodo">
      <div class="form-group">
        <label for="title">Title:</label>
        <input type="text" id="title" v-model="todoForm.title" required />
      </div>
      <div class="form-group">
        <label for="dueDate">Due Date:</label>
        <input type="date" id="dueDate" v-model="todoForm.due_date" required />
      </div>
      <div class="form-group">
        <label for="assigned_to">Assigned To:</label>
        <select type="select" id="assigned_to" v-model="todoForm.assigned_to" required>
          <option value="PHONWALAI">Phonwalai</option>
          <option value="GAUTHIER">Gauthier</option>
          <option value="BOTH">Both</option>
        </select>
      </div>
      <button type="submit" class="submit-button">Create Todo</button>
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
