<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import TodoService from '../../services/TodoService'
import Todo from '@/model/Todo'
import CardContent from '../Dashboard/CardContent.vue'
import TodoComponent from './TodoComponent.vue'

const todoService = new TodoService()
const todos = ref<Todo[]>([])
const fetchTodos = async () => {
  try {
    console.log('fetching todos...')
    const data = await todoService.getTodos()
    console.log('data retrieved', data)
    todos.value = data
  } catch (err) {
    console.error('Error fetching todos:', err)
  } finally {
    console.info('Todo ended')
  }
}

const todoListSorted = computed(() => {
  // Return a sorted shallow copy to avoid mutating the original reactive array
  return todos.value.slice().sort((a, b) => {
    if (a.completed !== b.completed) {
      return a.completed ? 1 : -1 // Incomplete first
    }
    return 0 // Both have no due date
  })
})

async function toggleTodoCompletion(todo: Todo) {
  try {
    todo.completed = !todo.completed
    await todoService.updateTodo(todo)
    console.log('Todo updated', todo)
  } catch (err) {
    console.error('Error updating todo:', err)
  }
}
async function deleteTodo(id: string) {
  try {
    await todoService.deleteTodo(id)
    todos.value = todos.value.filter((todo) => todo.id !== id)
    console.log('Todo deleted', id)
  } catch (err) {
    console.error('Error deleting todo:', err)
  }
}
onMounted(fetchTodos)
</script>

<template>
  <CardContent :title="'Todo List'">
    <p>This is the Todo List component.</p>
    <div class="todo-item-container">
      <TodoComponent
        v-for="todo in todoListSorted"
        :key="todo.id"
        :id="todo.id"
        :title="todo.title"
        :assigned_to="todo.assigned_to"
        :due_date="todo.due_date"
        :completed="todo.completed"
        @onToggleCompletion="toggleTodoCompletion(todo)"
        @onDeleteTodo="deleteTodo(todo.id)"
      />
    </div>
  </CardContent>
</template>

<style scoped>
.todo-item-container {
  margin-bottom: 0.5rem;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
}
</style>
