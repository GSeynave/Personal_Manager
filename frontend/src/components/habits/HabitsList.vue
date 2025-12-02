<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useHabitStore } from '@/stores/habits'
import HabitsData from '@/model/HabitData'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Label } from '@/components/ui/label'
import { Check, Plus, Pencil, Trash2, Calendar, Clock, Tag, Info, ChartLine, CheckCircle2 } from 'lucide-vue-next'

const habitStore = useHabitStore()

const habits = ref<HabitsData[] | null>(null)
const newHabitTitle = ref('')
const newHabitDescription = ref('')
const newHabitCategory = ref('HEALTH')
const newHabitType = ref('boolean')
const newHabitUnit = ref('')
const showHabitForm = ref(false)
const activeHabitId = ref<number | null>(null)

// Options for select dropdowns
const categoryOptions = [
  { label: 'Health', value: 'HEALTH' },
  { label: 'Fitness', value: 'FITNESS' },
  { label: 'Productivity', value: 'PRODUCTIVITY' },
  { label: 'Wellness', value: 'WELLNESS' },
  { label: 'Learning', value: 'LEARNING' },
  { label: 'Social', value: 'SOCIAL' },
  { label: 'Other', value: 'OTHER' }
]

const typeOptions = [
  { label: 'Boolean (Yes/No)', value: 'boolean' },
  { label: 'Count', value: 'count' },
  { label: 'Duration', value: 'duration' }
]

// For logging
const showLogDialog = ref(false)
const logDialogHabitId = ref<number | null>(null)
const logCountValue = ref(1)
const logDurationValue = ref(15)

// For editing
const editingHabitId = ref<number | null>(null)
const editTitle = ref('')
const editDescription = ref('')

// Get the active habit
const activeHabit = computed(() => {
  if (activeHabitId.value === null) return null
  return habitStore.habits.find(h => h.id === activeHabitId.value) || null
})

// Get log count for a habit
function getLogCount(habitId: number): number {
  const habit = habitStore.habits.find(h => h.id === habitId)
  return habit?.logs?.length || 0
}

// Check if habit was logged today
function isLoggedToday(habitId: number): boolean {
  const habit = habitStore.habits.find(h => h.id === habitId)
  if (!habit?.logs || habit.logs.length === 0) return false
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  return habit.logs.some(log => {
    const logDate = new Date(log.completion_date)
    logDate.setHours(0, 0, 0, 0)
    return logDate.getTime() === today.getTime()
  })
}

function setActiveHabit(habitId: number | null) {
  activeHabitId.value = habitId
}

onMounted(() => {
  habitStore.fetchHabits().then(() => {
    if (habitStore.habits.length > 0) {
      habits.value = habitStore.habits
      activeHabitId.value = habitStore.habits[0]?.id ?? null
    }
  })
})

async function handleCreateHabit() {
  if (!newHabitTitle.value.trim()) {
    alert('Please enter a habit title')
    return
  }
  
  try {
    await habitStore.addHabit(
      newHabitTitle.value,
      newHabitDescription.value,
      newHabitCategory.value,
      newHabitType.value,
      newHabitUnit.value || undefined
    )
    // Reset form
    newHabitTitle.value = ''
    newHabitDescription.value = ''
    newHabitCategory.value = 'HEALTH'
    newHabitType.value = 'boolean'
    newHabitUnit.value = ''
    showHabitForm.value = false
  } catch (err) {
    console.error('Failed to create habit:', err)
    alert('Failed to create habit. Please try again.')
  }
}

async function handleDeleteHabit(habitId: number) {
  if (!confirm('Are you sure you want to delete this habit? This action cannot be undone.')) {
    return
  }
  
  try {
    await habitStore.deleteHabit(habitId)
    if (activeHabitId.value === habitId) {
      activeHabitId.value = habitStore.habits[0]?.id ?? null
    }
  } catch (err) {
    console.error('Failed to delete habit:', err)
    alert('Failed to delete habit. Please try again.')
  }
}

function startEditHabit(habit: HabitsData) {
  editingHabitId.value = habit.id
  editTitle.value = habit.title
  editDescription.value = habit.description
}

function cancelEdit() {
  editingHabitId.value = null
  editTitle.value = ''
  editDescription.value = ''
}

async function saveEdit(habitId: number) {
  if (!editTitle.value.trim()) {
    alert('Please enter a habit title')
    return
  }
  
  const habit = habitStore.habits.find(h => h.id === habitId)
  if (!habit) return
  
  try {
    const updatedHabit = new HabitsData(
      habit.id,
      editTitle.value,
      editDescription.value,
      habit.category,
      habit.logs,
      habit.type,
      habit.unit
    )
    await habitStore.updateHabit(updatedHabit)
    cancelEdit()
  } catch (err) {
    console.error('Failed to update habit:', err)
    alert('Failed to update habit. Please try again.')
  }
}

// Logging functions based on habit type
async function handleLogHabit(habitId: number, habitType: string) {
  if (isLoggedToday(habitId)) {
    alert('This habit has already been logged today!')
    return
  }
  
  if (habitType === 'boolean') {
    await logHabitCompletion(habitId)
  } else {
    logDialogHabitId.value = habitId
    logCountValue.value = 1
    logDurationValue.value = 15
    showLogDialog.value = true
  }
}

async function logHabitCompletion(habitId: number) {
  try {
    const today = new Date().toISOString()
    await habitStore.addHabitLog(habitId, today)
    await habitStore.fetchHabits()
  } catch (err) {
    console.error('Failed to log habit:', err)
    alert('Failed to log habit. Please try again.')
  }
}

async function confirmLogDialog() {
  if (logDialogHabitId.value === null) return
  
  try {
    const today = new Date().toISOString()
    await habitStore.addHabitLog(logDialogHabitId.value, today)
    await habitStore.fetchHabits()
    showLogDialog.value = false
    logDialogHabitId.value = null
  } catch (err) {
    console.error('Failed to log habit:', err)
    alert('Failed to log habit. Please try again.')
  }
}

function cancelLogDialog() {
  showLogDialog.value = false
  logDialogHabitId.value = null
}

function getHabitTypeDisplay(type: string): string {
  return type.charAt(0).toUpperCase() + type.slice(1)
}
</script>

<template>
  <div v-if="habitStore.isLoading" class="p-4">
    <div class="text-muted-foreground">Loading habits...</div>
  </div>
  <div v-else-if="habitStore.error" class="p-4">
    <div class="text-destructive">{{ habitStore.error }}</div>
  </div>
  
  <div v-else class="habit-container space-y-6 p-6">
    <!-- First section: Create new habits -->
    <Card class="create-habit-section">
      <CardContent class="pt-6">
        <Button 
          v-if="!showHabitForm" 
          @click="showHabitForm = true" 
          class="w-full"
        >
          <Plus class="w-4 h-4 mr-2" />
          Create New Habit
        </Button>
        
        <div v-else class="space-y-4">
          <div>
            <Label for="new-title">Habit Title</Label>
            <Input 
              id="new-title"
              v-model="newHabitTitle" 
              placeholder="Habit Title (required)"
              @keyup.enter="handleCreateHabit"
            />
          </div>
          
          <div>
            <Label for="new-description">Description</Label>
            <Textarea 
              id="new-description"
              v-model="newHabitDescription" 
              placeholder="Habit Description (optional)"
              :rows="3"
            />
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <Label for="category">Category</Label>
              <Select v-model="newHabitCategory">
                <SelectTrigger id="category">
                  <SelectValue placeholder="Select Category" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="option in categoryOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
            
            <div>
              <Label for="type">Type</Label>
              <Select v-model="newHabitType">
                <SelectTrigger id="type">
                  <SelectValue placeholder="Select Type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="option in typeOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          
          <div v-if="newHabitType !== 'boolean'">
            <Label for="unit">Unit</Label>
            <Input 
              id="unit"
              v-model="newHabitUnit" 
              :placeholder="newHabitType === 'count' ? 'Unit (e.g., times, reps)' : 'Unit (e.g., minutes, hours)'"
            />
          </div>
          
          <div class="flex gap-2">
            <Button @click="handleCreateHabit" class="flex-1">
              <Check class="w-4 h-4 mr-2" />
              Add Habit
            </Button>
            <Button @click="showHabitForm = false" variant="outline">
              Cancel
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Second section: Quick access habit cards (grid) -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card 
        v-for="habit in habitStore.habits" 
        :key="habit.id" 
        class="cursor-pointer transition-all hover:shadow-lg"
        :class="{ 'ring-2 ring-green-500': isLoggedToday(habit.id) }"
        @click="handleLogHabit(habit.id, habit.type)"
      >
        <CardHeader>
          <div class="flex justify-between items-start">
            <CardTitle class="text-lg">{{ habit.title }}</CardTitle>
            <Button 
              variant="ghost" 
              size="icon"
              @click.stop="startEditHabit(habit)"
            >
              <Pencil class="w-4 h-4" />
            </Button>
          </div>
          <CardDescription>{{ habit.description }}</CardDescription>
        </CardHeader>
        
        <CardFooter class="flex-col items-start gap-2">
          <div class="flex items-center gap-2 w-full">
            <Badge variant="secondary">{{ habit.category }}</Badge>
            <span class="text-sm text-muted-foreground ml-auto flex items-center gap-1">
              <Clock class="w-3 h-3" />
              {{ getLogCount(habit.id) }} logs
            </span>
          </div>
          <Badge v-if="isLoggedToday(habit.id)" variant="default" class="w-full justify-center">
            <Check class="w-3 h-3 mr-1" />
            Logged Today
          </Badge>
        </CardFooter>
      </Card>
    </div>

    <!-- Third section: Habit tracking view -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- Left Sidebar with Habit List -->
      <Card class="lg:col-span-1">
        <CardHeader>
          <CardTitle class="text-base">Your Habits</CardTitle>
        </CardHeader>
        
        <CardContent class="p-2">
          <div class="space-y-1">
            <div 
              v-for="habit in habitStore.habits" 
              :key="habit.id"
              class="flex items-center justify-between p-3 rounded-md cursor-pointer transition-colors"
              :class="{ 
                'bg-accent': activeHabitId === habit.id,
                'hover:bg-accent/50': activeHabitId !== habit.id
              }"
              @click="setActiveHabit(habit.id)"
            >
              <span class="text-sm font-medium flex items-center gap-2">
                <CheckCircle2 v-if="isLoggedToday(habit.id)" class="w-4 h-4 text-green-500" />
                {{ habit.title }}
              </span>
              <Badge variant="outline" class="text-xs">{{ getLogCount(habit.id) }}</Badge>
            </div>
          </div>
        </CardContent>
      </Card>
      
      <!-- Right Content Area for Tracking -->
      <Card class="lg:col-span-3">
        <CardContent class="pt-6">
          <div v-if="activeHabit" class="space-y-6">
            <!-- Edit Mode -->
            <div v-if="editingHabitId === activeHabit.id" class="space-y-4">
              <div>
                <Label for="edit-title">Habit Title</Label>
                <Input 
                  id="edit-title"
                  v-model="editTitle" 
                  placeholder="Habit Title"
                />
              </div>
              <div>
                <Label for="edit-description">Description</Label>
                <Textarea 
                  id="edit-description"
                  v-model="editDescription" 
                  placeholder="Habit Description"
                  :rows="4"
                />
              </div>
              <div class="flex gap-2">
                <Button @click="saveEdit(activeHabit.id)" class="flex-1">
                  <Check class="w-4 h-4 mr-2" />
                  Save
                </Button>
                <Button @click="cancelEdit" variant="outline">
                  Cancel
                </Button>
              </div>
            </div>
            
            <!-- View Mode -->
            <div v-else>
              <div class="space-y-4">
                <div class="flex justify-between items-start">
                  <h2 class="text-2xl font-bold">{{ activeHabit.title }}</h2>
                  <div class="flex gap-2">
                    <Button 
                      variant="ghost" 
                      size="icon"
                      @click="startEditHabit(activeHabit)"
                    >
                      <Pencil class="w-4 h-4" />
                    </Button>
                    <Button 
                      variant="ghost" 
                      size="icon"
                      @click="handleDeleteHabit(activeHabit.id)"
                    >
                      <Trash2 class="w-4 h-4 text-destructive" />
                    </Button>
                  </div>
                </div>
                
                <p class="text-muted-foreground">{{ activeHabit.description }}</p>
                
                <div class="flex flex-wrap gap-4 text-sm">
                  <span class="flex items-center gap-2">
                    <Tag class="w-4 h-4" />
                    <strong>{{ activeHabit.category }}</strong>
                  </span>
                  <span class="flex items-center gap-2">
                    <Info class="w-4 h-4" />
                    <strong>{{ getHabitTypeDisplay(activeHabit.type) }}</strong>
                  </span>
                  <span v-if="activeHabit.unit" class="flex items-center gap-2">
                    <ChartLine class="w-4 h-4" />
                    <strong>{{ activeHabit.unit }}</strong>
                  </span>
                  <span class="flex items-center gap-2">
                    <Clock class="w-4 h-4" />
                    <strong>{{ getLogCount(activeHabit.id) }} logs</strong>
                  </span>
                </div>
                
                <Button 
                  @click="handleLogHabit(activeHabit.id, activeHabit.type)" 
                  :disabled="isLoggedToday(activeHabit.id)"
                  :variant="isLoggedToday(activeHabit.id) ? 'secondary' : 'default'"
                  class="w-full"
                >
                  <component :is="isLoggedToday(activeHabit.id) ? Check : Plus" class="w-4 h-4 mr-2" />
                  {{ isLoggedToday(activeHabit.id) ? 'Logged Today' : 'Log Completion' }}
                </Button>
              </div>
              
              <Separator class="my-6" />
              
              <div class="space-y-4">
                <div v-if="activeHabit.logs && activeHabit.logs.length > 0">
                  <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                    <Clock class="w-5 h-5" />
                    Recent Activity
                  </h3>
                  <div class="space-y-2">
                    <div 
                      v-for="log in activeHabit.logs.slice().reverse()" 
                      :key="log.id" 
                      class="flex items-center justify-between p-3 bg-accent/50 rounded-md"
                    >
                      <span class="flex items-center gap-2 text-sm">
                        <Calendar class="w-4 h-4" />
                        {{ new Date(log.completion_date).toLocaleDateString() }}
                      </span>
                      <span class="flex items-center gap-2 text-sm text-muted-foreground">
                        <Clock class="w-4 h-4" />
                        {{ new Date(log.completion_date).toLocaleTimeString() }}
                      </span>
                    </div>
                  </div>
                </div>
                
                <div v-else class="text-center text-muted-foreground py-8">
                  <p>No activity yet. Start logging to track your progress!</p>
                </div>
                
                <div class="bg-muted p-6 rounded-lg text-center">
                  <p class="text-muted-foreground text-sm">Graph/Calendar visualization coming soon</p>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center text-muted-foreground py-8">
            <p>Select a habit to view details</p>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>

  <!-- Log Dialog for count/duration habits -->
  <Dialog v-model:open="showLogDialog">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Log Habit Completion</DialogTitle>
        <DialogDescription>
          Enter the value for this habit log.
        </DialogDescription>
      </DialogHeader>
      
      <div class="space-y-4 py-4">
        <div v-if="logDialogHabitId !== null">
          <Label for="log-value">Value</Label>
          <Input 
            id="log-value"
            v-model="logCountValue" 
            type="number"
            :min="1"
          />
        </div>
      </div>
      
      <DialogFooter>
        <Button @click="confirmLogDialog">Confirm</Button>
        <Button @click="cancelLogDialog" variant="outline">Cancel</Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>

</template>

<style scoped>
.habit-container {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
