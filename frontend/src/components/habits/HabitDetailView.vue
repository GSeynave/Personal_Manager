<script setup lang="ts">
import { ref, computed } from 'vue'
import type HabitData from '@/model/habits/HabitData'
import type HabitLog from '@/model/habits/HabitLog'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { 
  Calendar, Clock, Check, Plus, ChartLine, CheckCircle2, 
  Pencil, Trash2, Tag, Info, CalendarDays, TrendingUp 
} from 'lucide-vue-next'

const props = defineProps<{
  habit: HabitData
  logs: HabitLog[]
  isLoggedToday: boolean
  isCompletedToday: boolean
  getDateStatus: (date: Date) => 'complete' | 'partial' | 'unlogged'
  getStatusColor: (status: 'complete' | 'partial' | 'unlogged') => string
  formatDate: (dateStr?: string) => string
}>()

const emit = defineEmits<{
  log: []
  edit: []
  delete: []
}>()

const currentMonth = ref(new Date())
const viewTab = ref('recent')

// Check if today is a scheduled day for logging
const canLogToday = computed(() => {
  // If frequency is not set, treat as DAILY (backward compatibility)
  if (!props.habit.frequency || props.habit.frequency === 'DAILY') {
    return true
  }
  
  if (props.habit.frequency === 'WEEKLY') {
    const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
    const today = dayNames[new Date().getDay()]
    return props.habit.scheduledDays?.includes(today as any) ?? false
  }
  
  return true
})

const logButtonTooltip = computed(() => {
  if (!canLogToday.value) {
    const dayLabels = {
      MONDAY: 'Monday',
      TUESDAY: 'Tuesday',
      WEDNESDAY: 'Wednesday',
      THURSDAY: 'Thursday',
      FRIDAY: 'Friday',
      SATURDAY: 'Saturday',
      SUNDAY: 'Sunday'
    }
    const scheduledDayNames = props.habit.scheduledDays?.map(d => dayLabels[d]).join(', ') || ''
    return `This habit can only be logged on: ${scheduledDayNames}`
  }
  return ''
})

// Check if a specific date is scheduled for logging
function isDateScheduled(date: Date): boolean {
  // If frequency is not set or DAILY, all days are scheduled
  if (!props.habit.frequency || props.habit.frequency === 'DAILY') {
    return true
  }
  
  if (props.habit.frequency === 'WEEKLY') {
    const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
    const dayOfWeek = dayNames[date.getDay()]
    return props.habit.scheduledDays?.includes(dayOfWeek as any) ?? false
  }
  
  return true
}

// Calendar helper functions
function getDaysInMonth(date: Date): Date[] {
  const year = date.getFullYear()
  const month = date.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const days: Date[] = []
  
  const startDay = firstDay.getDay()
  for (let i = 0; i < startDay; i++) {
    days.push(new Date(year, month, -startDay + i + 1))
  }
  
  for (let day = 1; day <= lastDay.getDate(); day++) {
    days.push(new Date(year, month, day))
  }
  
  return days
}

function previousMonth() {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() - 1, 1)
}

function nextMonth() {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1, 1)
}

function formatMonthYear(date: Date): string {
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
}

function isCurrentMonth(date: Date): boolean {
  return date.getMonth() === currentMonth.value.getMonth()
}

// Calculate completion rate for graph
const completionData = computed(() => {
  const dataByDate: Record<string, { rate: number, value?: number, completed?: boolean }> = {}
  
  props.logs.forEach(log => {
    const dateStr = log.date || log.createdAt?.split('T')[0]
    if (!dateStr) return
    
    // Check if this date should be included based on schedule
    const logDate = new Date(dateStr)
    if (!isDateScheduled(logDate)) return
    
    let rate = 0
    let value: number | undefined
    let completed: boolean | undefined
    
    switch (props.habit.habitType) {
      case 'YES_NO':
        completed = log.completed
        rate = log.completed ? 100 : 0
        break
      case 'NUMERIC':
        value = log.numberOfTimes
        if (props.habit.numberOfTimes && log.numberOfTimes !== undefined) {
          rate = Math.min((log.numberOfTimes / props.habit.numberOfTimes) * 100, 100)
        }
        break
      case 'DURATION':
        value = log.duration
        if (props.habit.duration && log.duration !== undefined) {
          rate = Math.min((log.duration / props.habit.duration) * 100, 100)
        }
        break
    }
    
    dataByDate[dateStr] = { rate, value, completed }
  })
  
  return Object.entries(dataByDate)
    .map(([date, data]) => ({ date, ...data }))
    .sort((a, b) => a.date.localeCompare(b.date))
    .slice(-30)
})

function getConsistencyLabel(entry: { rate: number, value?: number, completed?: boolean }): string {
  switch (props.habit.habitType) {
    case 'YES_NO':
      return entry.completed ? '✓' : '✗'
    case 'NUMERIC':
      return `${entry.value || 0}/${props.habit.numberOfTimes || 0}`
    case 'DURATION':
      return `${entry.value || 0}/${props.habit.duration || 0}min`
    default:
      return ''
  }
}
</script>

<template>
  <Card class="lg:col-span-3">
    <CardContent class="pt-6">
      <div class="space-y-6">
        <div class="space-y-4">
          <div class="flex justify-between items-start">
            <h2 class="text-2xl font-bold">{{ habit.title }}</h2>
            <div class="flex gap-2">
              <Button variant="ghost" size="icon" @click="emit('edit')">
                <Pencil class="w-4 h-4 text-muted-foreground" />
              </Button>
              <Button variant="ghost" size="icon" @click="emit('delete')">
                <Trash2 class="w-4 h-4 text-destructive" />
              </Button>
            </div>
          </div>

          <p class="text-muted-foreground">{{ habit.description }}</p>

          <div class="flex flex-wrap gap-2">
            <Badge variant="secondary" class="flex items-center gap-1">
              <Tag class="w-3 h-3" />
              {{ habit.category }}
            </Badge>
            <Badge variant="outline" class="flex items-center gap-1">
              <Info class="w-3 h-3" />
              <span v-if="habit.habitType === 'YES_NO'">Yes/No</span>
              <span v-else-if="habit.habitType === 'NUMERIC'">Count: {{ habit.numberOfTimes }}</span>
              <span v-else-if="habit.habitType === 'DURATION'">{{ habit.duration }} min</span>
            </Badge>
          </div>

          <Button 
            @click="emit('log')"
            :disabled="isCompletedToday || !canLogToday"
            :variant="isCompletedToday ? 'secondary' : 'default'" 
            :title="logButtonTooltip"
            class="w-full"
          >
            <component :is="isCompletedToday ? Check : Plus" class="w-4 h-4 mr-2" />
            {{ isCompletedToday ? 'Goal Completed!' : (!canLogToday ? 'Not Scheduled Today' : (isLoggedToday ? 'Update Log' : 'Log Completion')) }}
          </Button>
        </div>

        <Separator class="my-6" />

        <Tabs v-model="viewTab" class="w-full">
          <TabsList class="grid w-full grid-cols-4">
            <TabsTrigger value="recent">
              <Clock class="w-4 h-4 mr-2" />
              Recent
            </TabsTrigger>
            <TabsTrigger value="calendar">
              <CalendarDays class="w-4 h-4 mr-2" />
              Calendar
            </TabsTrigger>
            <TabsTrigger value="graph">
              <TrendingUp class="w-4 h-4 mr-2" />
              Graph
            </TabsTrigger>
            <TabsTrigger value="consistency">
              <ChartLine class="w-4 h-4 mr-2" />
              Consistency
            </TabsTrigger>
          </TabsList>

          <!-- Recent Activity Tab -->
          <TabsContent value="recent" class="mt-6">
            <div v-if="logs.length > 0">
              <div class="space-y-2">
                <div v-for="log in logs.slice().reverse()" :key="log.id"
                  class="flex items-center justify-between p-3 bg-accent/50 rounded-md">
                  <span class="flex items-center gap-2 text-sm">
                    <Calendar class="w-4 h-4 text-muted-foreground" />
                    {{ log.date || log.createdAt?.split('T')[0] }}
                  </span>
                  <span v-if="habit.habitType === 'YES_NO' && log.completed" class="flex items-center gap-2 text-sm text-muted-foreground">
                    <Check class="w-4 h-4 text-green-500" />
                    Completed
                  </span>
                  <span v-if="habit.habitType === 'NUMERIC' && log.numberOfTimes !== undefined" class="flex items-center gap-2 text-sm text-muted-foreground">
                    <ChartLine class="w-4 h-4 text-muted-foreground" />
                    {{ log.numberOfTimes }} times
                    <Check v-if="habit.numberOfTimes && log.numberOfTimes >= habit.numberOfTimes" class="w-4 h-4 text-green-500" />
                  </span>
                  <span v-if="habit.habitType === 'DURATION' && log.duration !== undefined" class="flex items-center gap-2 text-sm text-muted-foreground">
                    <Clock class="w-4 h-4 text-muted-foreground" />
                    {{ log.duration }} min
                    <Check v-if="habit.duration && log.duration >= habit.duration" class="w-4 h-4 text-green-500" />
                  </span>
                </div>
              </div>
            </div>
            <div v-else class="text-center text-muted-foreground py-8">
              <p>No activity yet. Start logging to track your progress!</p>
            </div>
          </TabsContent>

          <!-- Calendar View Tab -->
          <TabsContent value="calendar" class="mt-6">
            <div class="space-y-4">
              <div class="flex items-center justify-between">
                <Button variant="outline" size="sm" @click="previousMonth">
                  ← Previous
                </Button>
                <h3 class="text-lg font-semibold">{{ formatMonthYear(currentMonth) }}</h3>
                <Button variant="outline" size="sm" @click="nextMonth">
                  Next →
                </Button>
              </div>

              <div class="grid grid-cols-7 gap-1">
                <div v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="day"
                  class="text-center text-xs font-semibold text-muted-foreground py-2">
                  {{ day }}
                </div>

                <div v-for="(date, index) in getDaysInMonth(currentMonth)" :key="index"
                  class="aspect-square flex items-center justify-center rounded-md text-xs transition-colors"
                  :class="[
                    isCurrentMonth(date) && !isDateScheduled(date) ? 'bg-gray-100 dark:bg-gray-800 opacity-40 cursor-not-allowed' : getStatusColor(getDateStatus(date)),
                    !isCurrentMonth(date) ? 'opacity-30' : ''
                  ]">
                  <span :class="isCurrentMonth(date) ? 'font-semibold' : ''">
                    {{ date.getDate() }}
                  </span>
                </div>
              </div>

              <div class="flex items-center justify-center gap-4 pt-4 text-xs flex-wrap">
                <div class="flex items-center gap-2">
                  <div class="w-4 h-4 rounded bg-green-500"></div>
                  <span>Complete</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-4 h-4 rounded bg-orange-500"></div>
                  <span>Partial</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-4 h-4 rounded bg-gray-300 dark:bg-gray-700"></div>
                  <span>Unlogged</span>
                </div>
                <div v-if="habit.frequency === 'WEEKLY'" class="flex items-center gap-2">
                  <div class="w-4 h-4 rounded bg-gray-100 dark:bg-gray-800 border border-border"></div>
                  <span>Not Scheduled</span>
                </div>
              </div>
            </div>
          </TabsContent>

          <!-- Graph View Tab -->
          <TabsContent value="graph" class="mt-6">
            <div class="space-y-4">
              <h3 class="text-lg font-semibold">Progress Over Time (Last 30 Days)</h3>
              
              <div v-if="completionData.length > 0" class="relative h-64 border border-border rounded-lg p-4">
                <div class="absolute left-0 top-0 bottom-0 w-8 flex flex-col justify-between text-xs text-muted-foreground">
                  <span>100%</span>
                  <span>75%</span>
                  <span>50%</span>
                  <span>25%</span>
                  <span>0%</span>
                </div>
                
                <div class="ml-10 mr-2 h-full relative">
                  <div class="absolute inset-0 flex flex-col justify-between">
                    <div class="border-t border-border/50"></div>
                    <div class="border-t border-border/50"></div>
                    <div class="border-t border-border/50"></div>
                    <div class="border-t border-border/50"></div>
                    <div class="border-t border-border/50"></div>
                  </div>
                  
                  <svg class="w-full h-full absolute inset-0" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <polyline
                      :points="completionData.map((entry, i) => {
                        const x = (i / (completionData.length - 1)) * 100
                        const y = 100 - entry.rate
                        return `${x},${y}`
                      }).join(' ')"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      class="text-primary"
                      vector-effect="non-scaling-stroke"
                    />
                    <circle
                      v-for="(entry, i) in completionData"
                      :key="i"
                      :cx="(i / (completionData.length - 1)) * 100"
                      :cy="100 - entry.rate"
                      r="1.5"
                      :class="entry.rate >= 100 ? 'fill-green-500' : entry.rate > 0 ? 'fill-orange-500' : 'fill-gray-400'"
                      vector-effect="non-scaling-stroke"
                    />
                  </svg>
                </div>
                
                <div class="ml-10 mr-2 mt-2 flex justify-between text-xs text-muted-foreground">
                  <span>{{ completionData[0]?.date.slice(5) }}</span>
                  <span v-if="completionData.length > 1">{{ completionData[Math.floor(completionData.length / 2)]?.date.slice(5) }}</span>
                  <span>{{ completionData[completionData.length - 1]?.date.slice(5) }}</span>
                </div>
              </div>
              
              <div v-else class="text-center text-muted-foreground py-8">
                <p>No data yet. Start logging to see your progress!</p>
              </div>
            </div>
          </TabsContent>
          
          <!-- Consistency View Tab -->
          <TabsContent value="consistency" class="mt-6">
            <div class="space-y-4">
              <h3 class="text-lg font-semibold">Daily Consistency (Last 30 Days)</h3>
              
              <div v-if="completionData.length > 0" class="space-y-4">
                <div class="bg-accent/50 rounded-lg p-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-sm text-muted-foreground">Average Completion</p>
                      <p class="text-3xl font-bold">
                        {{ Math.round(completionData.reduce((sum, e) => sum + e.rate, 0) / completionData.length) }}%
                      </p>
                    </div>
                    <div class="text-right">
                      <p class="text-sm text-muted-foreground">Days Logged</p>
                      <p class="text-3xl font-bold">
                        {{ completionData.filter(e => e.rate > 0).length }}
                      </p>
                    </div>
                  </div>
                </div>
                
                <div class="space-y-2">
                  <div v-for="entry in completionData" :key="entry.date"
                    class="flex items-center gap-3">
                    <span class="text-xs text-muted-foreground w-24">{{ entry.date }}</span>
                    <div class="flex-1 bg-secondary rounded-full h-6 relative overflow-hidden">
                      <div class="h-6 rounded-full transition-all flex items-center justify-end pr-2"
                        :class="entry.rate >= 100 ? 'bg-green-500' : entry.rate > 0 ? 'bg-orange-500' : 'bg-gray-300'"
                        :style="{ width: entry.rate + '%' }">
                        <span v-if="entry.rate > 0" class="text-xs font-semibold text-white">
                          {{ getConsistencyLabel(entry) }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="text-center text-muted-foreground py-8">
                <p>No data yet. Start logging to see your progress!</p>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </CardContent>
  </Card>
</template>
