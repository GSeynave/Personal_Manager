<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useHabitStore } from '@/stores/habits'
import { useHabitHelpers } from '@/composables/useHabitHelpers'
import Sortable from 'sortablejs'
import HabitData, { type HabitType, type HabitFrequency, type DayOfWeek } from '@/model/habits/HabitData'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { CirclePlus, CheckCircle2, Clock, GripVertical } from 'lucide-vue-next'
import HabitCard from './HabitCard.vue'
import HabitForm from './HabitForm.vue'
import HabitLogDialog from './HabitLogDialog.vue'
import HabitDetailView from './HabitDetailView.vue'

const habitStore = useHabitStore()
const habitHelpers = useHabitHelpers(
  computed(() => habitStore.habitLogs),
  computed(() => habitStore.habits)
)

const showHabitForm = ref(false)
const activeHabitId = ref<number | null>(null)
const showLogDialog = ref(false)
const logDialogHabitId = ref<number | null>(null)
const logDialogType = ref<HabitType>('YES_NO')
const logInitialCount = ref<number | undefined>(undefined)
const logInitialDuration = ref<number | undefined>(undefined)
const habitListRef = ref<HTMLElement | null>(null)

// Edit mode
const editingHabitId = ref<number | null>(null)

const activeHabit = computed(() => {
  if (activeHabitId.value === null) return null
  return habitStore.habits.find(h => h.id === activeHabitId.value) || null
})

const activeHabitLogs = computed(() => {
  if (activeHabitId.value === null) return []
  return habitStore.habitLogs.get(activeHabitId.value) || []
})

onMounted(async () => {
  await habitStore.fetchHabits()
  if (habitStore.habits.length > 0) {
    activeHabitId.value = habitStore.habits[0]?.id ?? null
    for (const habit of habitStore.habits) {
      if (habit.id) {
        await habitStore.fetchHabitLogs(habit.id)
      }
    }
  }
  
  // Initialize sortable
  if (habitListRef.value) {
    Sortable.create(habitListRef.value, {
      animation: 150,
      handle: '.drag-handle',
      ghostClass: 'opacity-50',
      onEnd: (evt) => {
        if (evt.oldIndex !== undefined && evt.newIndex !== undefined) {
          const items = [...habitStore.habits]
          const [removed] = items.splice(evt.oldIndex, 1)
          if (removed) {
            items.splice(evt.newIndex, 0, removed)
            habitStore.habits = items
          }
        }
      }
    })
  }
})

// Habit CRUD operations
async function handleCreateHabit(data: {
  title: string
  description: string
  category: string
  type: HabitType
  frequency: HabitFrequency
  scheduledDays: DayOfWeek[]
  numberOfTimes?: number
  duration?: number
}) {
  try {
    await habitStore.addHabit(
      data.title,
      data.description,
      data.category,
      data.type,
      data.frequency,
      data.scheduledDays,
      data.numberOfTimes,
      data.duration
    )
    showHabitForm.value = false
  } catch (err) {
    console.error('Failed to create habit:', err)
    alert('Failed to create habit. Please try again.')
  }
}

async function handleUpdateHabit(habitId: number, data: {
  title: string
  description: string
  category: string
  type: HabitType
  frequency: HabitFrequency
  scheduledDays: DayOfWeek[]
  numberOfTimes?: number
  duration?: number
}) {
  try {
    await habitStore.updateHabit(
      habitId,
      data.title,
      data.description,
      data.category,
      data.type,
      data.frequency,
      data.scheduledDays,
      data.numberOfTimes,
      data.duration
    )
    editingHabitId.value = null
  } catch (err) {
    console.error('Failed to update habit:', err)
    alert('Failed to update habit. Please try again.')
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

// Helper function to check if today is a scheduled day
function canLogToday(habit: HabitData): boolean {
  const frequency = habit.frequency || 'DAILY'
  if (frequency === 'DAILY') return true
  
  if (frequency === 'WEEKLY') {
    const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
    const today = dayNames[new Date().getDay()]
    return habit.scheduledDays?.includes(today as any) ?? false
  }
  
  return true
}

// Logging operations
async function openLogDialog(habitId: number, habitType: HabitType) {
  // Find the habit to check if logging is allowed today
  const habit = habitStore.habits.find(h => h.id === habitId)
  if (habit) {
    // Check if today is a scheduled day
    const frequency = habit.frequency || 'DAILY'
    if (frequency === 'WEEKLY') {
      const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
      const today = dayNames[new Date().getDay()]
      if (!habit.scheduledDays?.includes(today as any)) {
        const dayLabels = {
          MONDAY: 'Monday', TUESDAY: 'Tuesday', WEDNESDAY: 'Wednesday',
          THURSDAY: 'Thursday', FRIDAY: 'Friday', SATURDAY: 'Saturday', SUNDAY: 'Sunday'
        }
        const scheduledDayNames = habit.scheduledDays?.map(d => dayLabels[d as keyof typeof dayLabels]).join(', ') || ''
        alert(`This habit can only be logged on: ${scheduledDayNames}`)
        return
      }
    }
  }

  if (habitHelpers.isCompletedToday(habitId)) {
    alert('Goal already completed for today!')
    return
  }

  logDialogHabitId.value = habitId
  logDialogType.value = habitType

  // Pre-fill with existing values if partially logged
  const logs = habitStore.habitLogs.get(habitId)
  const today = new Date().toISOString().split('T')[0]!
  const todayLog = logs?.find(log => {
    const logDate = log.date || log.createdAt?.split('T')[0]
    return logDate === today
  })

  if (todayLog) {
    logInitialCount.value = todayLog.numberOfTimes
    logInitialDuration.value = todayLog.duration
  } else {
    logInitialCount.value = undefined
    logInitialDuration.value = undefined
  }

  if (habitType === 'YES_NO') {
    // Directly log for YES_NO type
    await handleLogSubmit({ completed: true })
  } else {
    showLogDialog.value = true
  }
}

async function handleLogSubmit(data: { completed?: boolean, numberOfTimes?: number, duration?: number }) {
  if (!logDialogHabitId.value) return

  try {
    const today = new Date().toISOString().split('T')[0]!
    await habitStore.addHabitLog(logDialogHabitId.value, today, data.completed, data.numberOfTimes, data.duration)
    showLogDialog.value = false
  } catch (err: any) {
    console.error('Failed to log habit:', err)
    
    // Extract error message from backend response
    let errorMessage = 'Failed to log habit. Please try again.'
    if (err.response?.data?.message) {
      errorMessage = err.response.data.message
    } else if (err.message) {
      errorMessage = err.message
    }
    
    alert(errorMessage)
  }
}

function setActiveHabit(habitId: number | null) {
  activeHabitId.value = habitId
  editingHabitId.value = null
}

function startEdit(habit: HabitData) {
  editingHabitId.value = habit.id ?? null
}
</script>

<template>
  <div v-if="habitStore.isLoading" class="p-4">
    <div class="text-muted-foreground">Loading habits...</div>
  </div>
  <div v-else-if="habitStore.error" class="p-4">
    <div class="text-destructive">{{ habitStore.error }}</div>
  </div>

  <div v-else class="habit-container space-y-6">
    <!-- Create new habits section -->
    <div class="max-w-xs">
      <Button v-if="!showHabitForm" @click="showHabitForm = true" class="bg-primary hover:bg-primary/80">
        <CirclePlus class="w-4 h-4 mr-2 text-health" />
        Create New Habit
      </Button>

      <HabitForm
        v-else
        mode="create"
        @submit="handleCreateHabit"
        @cancel="showHabitForm = false"
      />
    </div>

    <!-- Quick access habit cards grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <HabitCard
        v-for="habit in habitStore.habits"
        :key="habit.id"
        :habit="habit"
        :log-count="habit.id ? habitHelpers.getLogCount(habit.id) : 0"
        :is-logged="habit.id ? habitHelpers.isLoggedToday(habit.id) : false"
        :is-completed="habit.id ? habitHelpers.isCompletedToday(habit.id) : false"
        :can-log-today="canLogToday(habit)"
        :current-value="habit.id ? habitHelpers.getTodayLogValue(habit.id, habit.habitType === 'NUMERIC' ? 'numberOfTimes' : 'duration') : undefined"
        :goal-value="habit.habitType === 'NUMERIC' ? habit.numberOfTimes : habit.duration"
        @click="habit.id && openLogDialog(habit.id, habit.habitType)"
      />
    </div>

    <!-- Habit tracking view -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- Left Sidebar with Habit List -->
      <Card class="lg:col-span-1">
        <CardHeader>
          <CardTitle class="text-base">Your Habits</CardTitle>
        </CardHeader>

        <CardContent class="p-2">
          <div ref="habitListRef" class="space-y-1">
            <div
              v-for="habit in habitStore.habits"
              :key="habit.id"
              class="flex items-center justify-between p-3 rounded-md cursor-pointer transition-colors group"
              :class="{
                'bg-accent': activeHabitId === habit.id,
                'hover:bg-accent/50': activeHabitId !== habit.id
              }"
              @click="habit.id && setActiveHabit(habit.id)"
            >
              <div class="flex items-center gap-2 flex-1 min-w-0">
                <GripVertical class="w-4 h-4 text-muted-foreground opacity-0 group-hover:opacity-100 transition-opacity drag-handle cursor-grab active:cursor-grabbing shrink-0" />
                <CheckCircle2 v-if="habit.id && habitHelpers.isCompletedToday(habit.id)" class="w-4 h-4 text-green-500 shrink-0" />
                <Clock v-else-if="habit.id && habitHelpers.isLoggedToday(habit.id)" class="w-4 h-4 text-yellow-500 shrink-0" />
                <span class="text-sm font-medium truncate">
                  {{ habit.title }}
                </span>
              </div>
              <Badge variant="outline" class="text-xs shrink-0">
                {{ habit.id ? habitHelpers.getLogCount(habit.id) : 0 }}
              </Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Right Content Area -->
      <div v-if="activeHabit && activeHabit.id" class="lg:col-span-3">
        <!-- Edit Mode -->
        <Card v-if="editingHabitId === activeHabit.id">
          <CardContent class="pt-6">
            <HabitForm
              mode="edit"
              :initial-title="activeHabit.title"
              :initial-description="activeHabit.description"
              :initial-category="activeHabit.category"
              :initial-type="activeHabit.habitType"
              :initial-frequency="activeHabit.frequency"
              :initial-scheduled-days="activeHabit.scheduledDays"
              :initial-number-of-times="activeHabit.numberOfTimes"
              :initial-duration="activeHabit.duration"
              @submit="(data) => activeHabit?.id && handleUpdateHabit(activeHabit.id, data)"
              @cancel="editingHabitId = null"
            />
          </CardContent>
        </Card>

        <!-- View Mode -->
        <HabitDetailView
          v-else
          :habit="activeHabit"
          :logs="activeHabitLogs"
          :is-logged-today="habitHelpers.isLoggedToday(activeHabit.id!)"
          :is-completed-today="habitHelpers.isCompletedToday(activeHabit.id!)"
          :get-date-status="(date) => habitHelpers.getDateStatus(activeHabit?.id!, date)"
          :get-status-color="habitHelpers.getStatusColor"
          :format-date="habitHelpers.formatDate"
          @log="openLogDialog(activeHabit.id, activeHabit.habitType)"
          @edit="startEdit(activeHabit)"
          @delete="handleDeleteHabit(activeHabit.id)"
        />
      </div>

      <Card v-else class="lg:col-span-3">
        <CardContent class="pt-6">
          <div class="text-center text-muted-foreground py-8">
            <p>Select a habit to view details</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Log Dialog -->
    <HabitLogDialog
      v-model:open="showLogDialog"
      :habit-type="logDialogType"
      :initial-count="logInitialCount"
      :initial-duration="logInitialDuration"
      @submit="handleLogSubmit"
    />
  </div>
</template>
