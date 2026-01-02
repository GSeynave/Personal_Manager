<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { HabitType, HabitFrequency, DayOfWeek } from '@/model/habits/HabitData'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { CirclePlus, X } from 'lucide-vue-next'

const props = defineProps<{
  mode: 'create' | 'edit'
  initialTitle?: string
  initialDescription?: string
  initialCategory?: string
  initialType?: HabitType
  initialFrequency?: HabitFrequency
  initialScheduledDays?: DayOfWeek[]
  initialNumberOfTimes?: number
  initialDuration?: number
}>()

const emit = defineEmits<{
  submit: [data: {
    title: string
    description: string
    category: string
    type: HabitType
    frequency: HabitFrequency
    scheduledDays: DayOfWeek[]
    numberOfTimes?: number
    duration?: number
  }]
  cancel: []
}>()

const title = ref(props.initialTitle || '')
const description = ref(props.initialDescription || '')
const category = ref(props.initialCategory || 'HEALTH')
const habitType = ref<HabitType>(props.initialType || 'YES_NO')
const frequency = ref<HabitFrequency>(props.initialFrequency || 'DAILY')
const scheduledDays = ref<DayOfWeek[]>(props.initialScheduledDays || [])
const numberOfTimes = ref<number | undefined>(props.initialNumberOfTimes)
const duration = ref<number | undefined>(props.initialDuration)

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
  { label: 'Yes/No', value: 'YES_NO' },
  { label: 'Numeric (Count)', value: 'NUMERIC' },
  { label: 'Duration (Minutes)', value: 'DURATION' }
]

const frequencyOptions = [
  { label: 'Daily', value: 'DAILY' },
  { label: 'Weekly (Select Days)', value: 'WEEKLY' }
]

const daysOfWeek: { label: string; value: DayOfWeek }[] = [
  { label: 'Mon', value: 'MONDAY' },
  { label: 'Tue', value: 'TUESDAY' },
  { label: 'Wed', value: 'WEDNESDAY' },
  { label: 'Thu', value: 'THURSDAY' },
  { label: 'Fri', value: 'FRIDAY' },
  { label: 'Sat', value: 'SATURDAY' },
  { label: 'Sun', value: 'SUNDAY' }
]

const showDaySelector = computed(() => frequency.value === 'WEEKLY')

function toggleDay(day: DayOfWeek) {
  const index = scheduledDays.value.indexOf(day)
  if (index > -1) {
    scheduledDays.value.splice(index, 1)
  } else {
    scheduledDays.value.push(day)
  }
}

function isDaySelected(day: DayOfWeek) {
  return scheduledDays.value.includes(day)
}

// Watch for prop changes (for edit mode)
watch(() => props.initialTitle, (val) => { if (val) title.value = val })
watch(() => props.initialDescription, (val) => { if (val) description.value = val })
watch(() => props.initialCategory, (val) => { if (val) category.value = val })
watch(() => props.initialType, (val) => { if (val) habitType.value = val })
watch(() => props.initialFrequency, (val) => { if (val) frequency.value = val })
watch(() => props.initialScheduledDays, (val) => { if (val) scheduledDays.value = [...val] })
watch(() => props.initialNumberOfTimes, (val) => { numberOfTimes.value = val })
watch(() => props.initialDuration, (val) => { duration.value = val })

// When frequency changes to DAILY, clear scheduled days
watch(frequency, (newFreq) => {
  if (newFreq === 'DAILY') {
    scheduledDays.value = []
  }
})

function handleSubmit() {
  if (!title.value.trim()) {
    alert('Please enter a habit title')
    return
  }

  if (frequency.value === 'WEEKLY' && scheduledDays.value.length === 0) {
    alert('Please select at least one day for weekly habits')
    return
  }

  emit('submit', {
    title: title.value,
    description: description.value,
    category: category.value,
    type: habitType.value,
    frequency: frequency.value,
    scheduledDays: scheduledDays.value,
    numberOfTimes: numberOfTimes.value,
    duration: duration.value
  })
}
</script>

<template>
  <div class="bg-card backdrop-blur-sm rounded-xl p-4 shadow-md">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-sm font-semibold text-foreground">
        {{ mode === 'create' ? 'Create Habit' : 'Edit Habit' }}
      </h3>
      <Button 
        variant="ghost" 
        size="icon"
        @click="emit('cancel')"
        class="h-6 w-6"
      >
        <X class="w-3 h-3" />
      </Button>
    </div>

    <div class="space-y-4">
      <div class="space-y-2">
        <Label for="title">Habit Title</Label>
        <Input 
          id="title" 
          v-model="title" 
          placeholder="Habit Title (required)"
          @keyup.enter="handleSubmit" 
        />
      </div>

      <div class="space-y-2">
        <Label for="description">Description</Label>
        <Textarea 
          id="description" 
          v-model="description" 
          placeholder="Habit Description (optional)"
          :rows="3" 
        />
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div class="space-y-2">
          <Label for="category">Category</Label>
          <Select v-model="category">
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

        <div class="space-y-2">
          <Label for="type">Type</Label>
          <Select v-model="habitType">
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

      <div class="space-y-2">
        <Label for="frequency">Frequency</Label>
        <Select v-model="frequency">
          <SelectTrigger id="frequency">
            <SelectValue placeholder="Select Frequency" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in frequencyOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div v-if="showDaySelector" class="space-y-2">
        <Label>Select Days</Label>
        <div class="flex gap-1 justify-between">
          <button
            v-for="day in daysOfWeek"
            :key="day.value"
            type="button"
            @click="toggleDay(day.value)"
            :class="[
              'w-10 h-10 rounded-full text-xs font-medium transition-colors',
              isDaySelected(day.value)
                ? 'bg-primary text-primary-foreground hover:bg-primary/90'
                : 'bg-secondary text-secondary-foreground hover:bg-secondary/80'
            ]"
          >
            {{ day.label }}
          </button>
        </div>
        <p v-if="scheduledDays.length === 0" class="text-xs text-muted-foreground">
          Select at least one day
        </p>
      </div>

      <div v-if="habitType === 'NUMERIC'" class="space-y-2">
        <Label for="numberOfTimes">Target Count</Label>
        <Input 
          id="numberOfTimes" 
          v-model.number="numberOfTimes" 
          type="number" 
          min="1"
          placeholder="Target number of times" 
        />
      </div>

      <div v-if="habitType === 'DURATION'" class="space-y-2">
        <Label for="duration">Target Duration (minutes)</Label>
        <Input 
          id="duration" 
          v-model.number="duration" 
          type="number" 
          min="1"
          placeholder="Target minutes" 
        />
      </div>

      <Button @click="handleSubmit" class="w-full">
        <CirclePlus class="w-4 h-4 mr-2" />
        {{ mode === 'create' ? 'Add Habit' : 'Update Habit' }}
      </Button>
    </div>
  </div>
</template>
