<script setup lang="ts">
import { ref } from 'vue'
import { useTodoStore } from '@/stores/todo'
import Todo from '@/model/Todo'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { CirclePlus, X } from 'lucide-vue-next'
import Card from '../ui/card/Card.vue'

const todoStore = useTodoStore()
const authStore = useAuthStore()

const isFormOpen = ref(false)
const title = ref('')
const dueDate = ref('')
const selectedGroupId = ref<number | undefined>(undefined)

function toggleForm() {
  isFormOpen.value = !isFormOpen.value
  if (!isFormOpen.value) {
    // Clear form when closing
    title.value = ''
    dueDate.value = ''
    selectedGroupId.value = undefined
  }
}

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
    
    // Clear form and close
    title.value = ''
    dueDate.value = ''
    selectedGroupId.value = undefined
    isFormOpen.value = false
  } catch (err) {
    console.error('Error creating todo:', err)
  }
}

</script>

<template>
  <div class="max-w-xs">
    <!-- Toggle Button -->
    <Button 
      v-if="!isFormOpen"
      @click="toggleForm"
      class="bg-primary hover:bg-primary/80"
    >
      <CirclePlus class="w-4 h-4 mr-2 text-productivity" />
      Create Todo
    </Button>

    <!-- Form -->
    <Card v-else class="bg-card backdrop-blur-sm rounded-xl p-4 shadow-md">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-sm font-semibold text-foreground">Create Todo</h3>
        <Button 
          variant="ghost" 
          size="icon"
          @click="toggleForm"
          class="h-6 w-6"
        >
          <X class="h-3 w-3" />
        </Button>
      </div>

      <form @submit.prevent="createTodo" class="space-y-4">
        <div class="space-y-2">
          <Label for="title">Title</Label>
          <Input 
            id="title" 
            v-model="title" 
            placeholder="What needs to be done?" 
            required
            class="h-9"
          />
        </div>
        
        <div class="space-y-2">
          <Label for="dueDate">Due Date</Label>
          <Input 
            id="dueDate" 
            type="date" 
            v-model="dueDate" 
            class="h-9"
          />
        </div>
        
        <div class="space-y-2">
          <Label for="group">Group</Label>
          <Select v-model="selectedGroupId">
            <SelectTrigger id="group" class="h-9">
              <SelectValue placeholder="Select group" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem :value="null">Ungrouped</SelectItem>
              <SelectItem 
                v-for="group in todoStore.groupedTodos" 
                :key="group.id" 
                :value="group.id"
              >
                {{ group.title }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
        
        <Button type="submit" :disabled="todoStore.isLoading" class="w-full">
          <CirclePlus class="w-4 h-4 mr-2" />
          {{ todoStore.isLoading ? 'Creating...' : 'Add' }}
        </Button>
      </form>
    </Card>
  </div>
</template>

<style scoped>
/* No additional styles needed - using Tailwind classes */
</style>
