import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import TodoService from '@/services/todo/TodoService'
import Todo from '@/model/todo/Todo'
import TodoGroup from '@/model/todo/TodoGroup'
import TodosView from '@/model/todo/TodosView'

const todoService = new TodoService()

export type TodoFilter = 'all' | 'active' | 'completed'

export const useTodoStore = defineStore('todo', () => {
  // State
  const ungroupedTodos = ref<Todo[]>([])
  const groupedTodos = ref<TodoGroup[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const filter = ref<TodoFilter>('all')

  // Getters
  const filteredUngroupedTodos = computed(() => {
    switch (filter.value) {
      case 'active':
        return ungroupedTodos.value.filter(todo => !todo.completed)
      case 'completed':
        return ungroupedTodos.value.filter(todo => todo.completed)
      default:
        return ungroupedTodos.value
    }
  })

  const filteredGroupedTodos = computed(() => {
    return groupedTodos.value.map(group => ({
      ...group,
      todos: group.todos.filter(todo => {
        switch (filter.value) {
          case 'active':
            return !todo.completed
          case 'completed':
            return todo.completed
          default:
            return true
        }
      })
    }))
  })

  const activeCount = computed(() => {
    const ungroupedActive = ungroupedTodos.value.filter(todo => !todo.completed).length
    const groupedActive = groupedTodos.value.reduce((count, group) => {
      return count + group.todos.filter(todo => !todo.completed).length
    }, 0)
    return ungroupedActive + groupedActive
  })

  const completedCount = computed(() => {
    const ungroupedCompleted = ungroupedTodos.value.filter(todo => todo.completed).length
    const groupedCompleted = groupedTodos.value.reduce((count, group) => {
      return count + group.todos.filter(todo => todo.completed).length
    }, 0)
    return ungroupedCompleted + groupedCompleted
  })

  const totalCount = computed(() => {
    const ungroupedTotal = ungroupedTodos.value.length
    const groupedTotal = groupedTodos.value.reduce((count, group) => {
      return count + group.todos.length
    }, 0)
    return ungroupedTotal + groupedTotal
  })

  // Actions
  async function fetchTodos() {
    isLoading.value = true
    error.value = null
    
    try {
      console.log('Fetching todos...')
      const data: TodosView = await todoService.getTodos()
      console.log('Todos retrieved:', data)
      ungroupedTodos.value = data.ungroupedTodos || []
      groupedTodos.value = data.groupedTodos || []
    } catch (err: any) {
      console.error('Error fetching todos:', err)
      error.value = err.message || 'Failed to fetch todos'
    } finally {
      isLoading.value = false
    }
  }

  async function addTodo(todo: Todo, groupId?: number) {
    error.value = null
    
    try {
      const newTodo = await todoService.addTodo(todo, groupId)
      console.log('Todo created:', newTodo)
      // Refresh the list after adding
      await fetchTodos()
    } catch (err: any) {
      console.error('Error creating todo:', err)
      error.value = err.message || 'Failed to create todo'
      throw err
    }
  }

  async function updateTodo(todo: Todo) {
    error.value = null
    
    // Find todo in ungrouped or grouped to determine its current groupId
    let index = ungroupedTodos.value.findIndex(t => t.id === todo.id)
    let previousTodo = index !== -1 ? ungroupedTodos.value[index] : null
    let groupIndex = -1
    let todoIndexInGroup = -1
    let currentGroupId: number | null = null

    if (index === -1) {
      // Search in groups
      for (let i = 0; i < groupedTodos.value.length; i++) {
        const group = groupedTodos.value[i]
        if (!group) continue
        todoIndexInGroup = group.todos.findIndex(t => t.id === todo.id)
        if (todoIndexInGroup !== -1) {
          groupIndex = i
          currentGroupId = group.id
          previousTodo = group.todos[todoIndexInGroup]
          break
        }
      }
    }
    
    // Optimistic update
    if (index !== -1) {
      ungroupedTodos.value[index] = { ...todo }
    } else if (groupIndex !== -1 && todoIndexInGroup !== -1) {
      const group = groupedTodos.value[groupIndex]
      if (group) {
        group.todos[todoIndexInGroup] = { ...todo }
      }
    }
    
    try {
      await todoService.updateTodo(todo, currentGroupId || undefined)
      console.log('Todo updated:', todo)
    } catch (err: any) {
      console.error('Error updating todo:', err)
      error.value = err.message || 'Failed to update todo'
      
      // Rollback on error
      if (index !== -1 && previousTodo) {
        ungroupedTodos.value[index] = previousTodo
      } else if (groupIndex !== -1 && todoIndexInGroup !== -1 && previousTodo) {
        const group = groupedTodos.value[groupIndex]
        if (group) {
          group.todos[todoIndexInGroup] = previousTodo
        }
      }
      
      throw err
    }
  }

  async function deleteTodo(id: number | string) {
    error.value = null
    
    // Find and remove from ungrouped or grouped
    const ungroupedIndex = ungroupedTodos.value.findIndex(t => t.id === id)
    let previousUngrouped: Todo[] | null = null
    let previousGrouped: TodoGroup[] | null = null
    
    if (ungroupedIndex !== -1) {
      previousUngrouped = [...ungroupedTodos.value]
      ungroupedTodos.value = ungroupedTodos.value.filter(todo => todo.id !== id)
    } else {
      // Search in groups
      previousGrouped = JSON.parse(JSON.stringify(groupedTodos.value))
      for (const group of groupedTodos.value) {
        group.todos = group.todos.filter(todo => todo.id !== id)
      }
    }
    
    try {
      await todoService.deleteTodo(String(id))
      console.log('Todo deleted:', id)
    } catch (err: any) {
      console.error('Error deleting todo:', err)
      error.value = err.message || 'Failed to delete todo'
      
      // Rollback on error
      if (previousUngrouped) {
        ungroupedTodos.value = previousUngrouped
      } else if (previousGrouped) {
        groupedTodos.value = previousGrouped
      }
      
      throw err
    }
  }

  async function toggleCompletion(todo: Todo) {
    const updatedTodo = { ...todo, completed: !todo.completed }
    await updateTodo(updatedTodo)
  }

  // Group management actions
  async function createGroup(title: string, description: string) {
    error.value = null
    
    try {
      await todoService.createGroup(title, description)
      await fetchTodos()
    } catch (err: any) {
      console.error('Error creating group:', err)
      error.value = err.message || 'Failed to create group'
      throw err
    }
  }

  async function deleteGroup(groupId: number) {
    error.value = null
    
    // Check if group is empty
    const group = groupedTodos.value.find(g => g.id === groupId)
    if (group && group.todos.length > 0) {
      error.value = 'Cannot delete group with todos'
      throw new Error('Cannot delete group with todos')
    }
    
    try {
      await todoService.deleteGroup(groupId)
      await fetchTodos()
    } catch (err: any) {
      console.error('Error deleting group:', err)
      error.value = err.message || 'Failed to delete group'
      throw err
    }
  }

  async function moveTodoToGroup(todoId: number, groupId: number | null) {
    error.value = null
    
    // Find the todo in ungrouped or grouped
    let todo = ungroupedTodos.value.find(t => t.id === todoId)
    
    if (!todo) {
      for (const group of groupedTodos.value) {
        todo = group.todos.find(t => t.id === todoId)
        if (todo) break
      }
    }
    
    if (!todo) {
      console.error('Todo not found:', todoId)
      return
    }
    
    try {
      await todoService.moveTodoToGroup(todo, groupId)
      await fetchTodos()
    } catch (err: any) {
      console.error('Error moving todo:', err)
      error.value = err.message || 'Failed to move todo'
      throw err
    }
  }

  function setFilter(newFilter: TodoFilter) {
    filter.value = newFilter
  }

  return {
    // State
    ungroupedTodos,
    groupedTodos,
    isLoading,
    error,
    filter,
    
    // Getters
    filteredUngroupedTodos,
    filteredGroupedTodos,
    activeCount,
    completedCount,
    totalCount,
    
    // Actions
    fetchTodos,
    addTodo,
    updateTodo,
    deleteTodo,
    toggleCompletion,
    createGroup,
    deleteGroup,
    moveTodoToGroup,
    setFilter
  }
})
