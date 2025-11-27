import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import TodoService from '@/services/TodoService'
import Todo from '@/model/Todo'

const todoService = new TodoService()

export type TodoFilter = 'all' | 'active' | 'completed'

export const useTodoStore = defineStore('todo', () => {
  // State
  const todos = ref<Todo[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const filter = ref<TodoFilter>('all')

  // Getters
  const filteredTodos = computed(() => {
    switch (filter.value) {
      case 'active':
        return todos.value.filter(todo => !todo.completed)
      case 'completed':
        return todos.value.filter(todo => todo.completed)
      default:
        return todos.value
    }
  })

  const activeCount = computed(() => {
    return todos.value.filter(todo => !todo.completed).length
  })

  const completedCount = computed(() => {
    return todos.value.filter(todo => todo.completed).length
  })

  // Actions
  async function fetchTodos() {
    isLoading.value = true
    error.value = null
    
    try {
      console.log('Fetching todos...')
      const data = await todoService.getTodos()
      console.log('Todos retrieved:', data)
      todos.value = data
    } catch (err: any) {
      console.error('Error fetching todos:', err)
      error.value = err.message || 'Failed to fetch todos'
    } finally {
      isLoading.value = false
    }
  }

  async function addTodo(todo: Todo) {
    isLoading.value = true
    error.value = null
    
    try {
      const newTodo = await todoService.addTodo(todo)
      console.log('Todo created:', newTodo)
      // Refresh the list after adding
      await fetchTodos()
    } catch (err: any) {
      console.error('Error creating todo:', err)
      error.value = err.message || 'Failed to create todo'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateTodo(todo: Todo) {
    error.value = null
    
    // Optimistic update: update local state immediately
    const index = todos.value.findIndex(t => t.id === todo.id)
    const previousTodo = index !== -1 ? todos.value[index] : null
    
    if (index !== -1) {
      todos.value[index] = { ...todo }
    }
    
    try {
      await todoService.updateTodo(todo)
      console.log('Todo updated:', todo)
    } catch (err: any) {
      console.error('Error updating todo:', err)
      error.value = err.message || 'Failed to update todo'
      
      // Rollback on error
      if (index !== -1 && previousTodo) {
        todos.value[index] = previousTodo
      }
      
      throw err
    }
  }

  async function deleteTodo(id: number | string) {
    error.value = null
    
    // Optimistic delete: remove from local state immediately
    const previousTodos = [...todos.value]
    todos.value = todos.value.filter(todo => todo.id !== id)
    
    try {
      await todoService.deleteTodo(String(id))
      console.log('Todo deleted:', id)
    } catch (err: any) {
      console.error('Error deleting todo:', err)
      error.value = err.message || 'Failed to delete todo'
      
      // Rollback on error
      todos.value = previousTodos
      
      throw err
    }
  }

  async function toggleCompletion(todo: Todo) {
    const updatedTodo = { ...todo, completed: !todo.completed }
    await updateTodo(updatedTodo)
  }

  function setFilter(newFilter: TodoFilter) {
    filter.value = newFilter
  }

  return {
    // State
    todos,
    isLoading,
    error,
    filter,
    
    // Getters
    filteredTodos,
    activeCount,
    completedCount,
    
    // Actions
    fetchTodos,
    addTodo,
    updateTodo,
    deleteTodo,
    toggleCompletion,
    setFilter
  }
})
