<script setup lang="ts">
import { computed } from 'vue'
import type HabitData from '@/model/habits/HabitData'
import { Card, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Clock, Check, Calendar } from 'lucide-vue-next'

const props = defineProps<{
  habit: HabitData
  logCount: number
  isLogged: boolean
  isCompleted: boolean
  canLogToday?: boolean
  currentValue?: number
  goalValue?: number
}>()

const emit = defineEmits<{
  click: []
}>()

const cardClasses = computed(() => {
  if (props.isCompleted) {
    return 'ring-2 ring-green-500 bg-green-50 dark:bg-green-950/20 cursor-default'
  }
  if (props.canLogToday === false) {
    return 'opacity-50 cursor-not-allowed'
  }
  if (props.isLogged) {
    return 'ring-2 ring-yellow-500 bg-yellow-50 dark:bg-yellow-950/20 cursor-pointer'
  }
  return 'cursor-pointer'
})

const progressPercentage = computed(() => {
  if (!props.goalValue || props.goalValue === 0) return 0
  return Math.min(((props.currentValue || 0) / props.goalValue) * 100, 100)
})
</script>

<template>
  <Card 
    class="transition-all hover:shadow-md"
    :class="cardClasses"
    @click="!isCompleted && canLogToday !== false && emit('click')"
  >
    <CardHeader class="pb-3">
      <div class="flex justify-between items-start gap-2">
        <div class="flex-1 min-w-0">
          <CardTitle class="text-base truncate">{{ habit.title }}</CardTitle>
          <div class="flex items-center gap-2 mt-1">
            <Badge variant="secondary" class="text-xs">{{ habit.category }}</Badge>
            <span class="text-xs text-muted-foreground flex items-center gap-1">
              <Clock class="w-3 h-3" />
              {{ logCount }}
            </span>
          </div>
        </div>
      </div>
    </CardHeader>

    <CardFooter class="pt-0 pb-3">
      <!-- Completed state -->
      <Badge v-if="isCompleted" variant="default" class="w-full justify-center bg-green-600 hover:bg-green-600 text-xs py-1">
        <Check class="w-3 h-3 mr-1" />
        Completed!
      </Badge>
      
      <!-- Partially logged state with progress bar -->
      <div v-else-if="isLogged" class="w-full space-y-1">
        <div v-if="habit.habitType === 'NUMERIC'" class="space-y-1">
          <div class="flex justify-between text-xs text-muted-foreground">
            <span>{{ currentValue || 0 }} / {{ goalValue || 0 }}</span>
            <span>{{ Math.round(progressPercentage) }}%</span>
          </div>
          <div class="w-full bg-secondary rounded-full h-1.5">
            <div class="bg-yellow-500 h-1.5 rounded-full transition-all" 
              :style="{ width: progressPercentage + '%' }">
            </div>
          </div>
        </div>
        <div v-else-if="habit.habitType === 'DURATION'" class="space-y-1">
          <div class="flex justify-between text-xs text-muted-foreground">
            <span>{{ currentValue || 0 }} / {{ goalValue || 0 }} min</span>
            <span>{{ Math.round(progressPercentage) }}%</span>
          </div>
          <div class="w-full bg-secondary rounded-full h-1.5">
            <div class="bg-yellow-500 h-1.5 rounded-full transition-all" 
              :style="{ width: progressPercentage + '%' }">
            </div>
          </div>
        </div>
        <Badge v-else variant="default" class="w-full justify-center bg-yellow-600 hover:bg-yellow-600 text-xs py-1">
          <Clock class="w-3 h-3 mr-1" />
          Partial
        </Badge>
      </div>
      
      <!-- Not logged state -->
      <Badge v-else-if="canLogToday === false" variant="secondary" class="w-full justify-center text-xs py-1 opacity-70">
        <Calendar class="w-3 h-3 mr-1" />
        Not scheduled
      </Badge>
      <Badge v-else variant="outline" class="w-full justify-center text-xs py-1">
        <span v-if="habit.habitType === 'NUMERIC'">
          {{ goalValue || 0 }}x
        </span>
        <span v-else-if="habit.habitType === 'DURATION'">
          {{ goalValue || 0 }} min
        </span>
        <span v-else>Log</span>
      </Badge>
    </CardFooter>
  </Card>
</template>
