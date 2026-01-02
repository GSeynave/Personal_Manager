import { computed, type Ref } from 'vue'
import type HabitData from '@/model/habits/HabitData'
import type HabitLog from '@/model/habits/HabitLog'

export function useHabitHelpers(
  habitLogs: Ref<Map<number, HabitLog[]>>,
  habits: Ref<HabitData[]>
) {
  // Get log count for a habit
  function getLogCount(habitId: number): number {
    return habitLogs.value.get(habitId)?.length || 0
  }

  // Check if habit was logged today (any log exists)
  function isLoggedToday(habitId: number): boolean {
    const logs = habitLogs.value.get(habitId)
    const today = new Date().toISOString().split('T')[0]!
    
    if (!logs || logs.length === 0) {
      return false
    }

    const hasLogToday = logs.some(log => {
      const logDate = log.date || log.createdAt?.split('T')[0]
      return logDate === today
    })
    
    return hasLogToday
  }

  // Check if habit goal is completed today
  function isCompletedToday(habitId: number): boolean {
    const habit = habits.value.find(h => h.id === habitId)
    if (!habit) return false
    
    const logs = habitLogs.value.get(habitId)
    const today = new Date().toISOString().split('T')[0]!
    
    if (!logs || logs.length === 0) {
      return false
    }

    const todayLog = logs.find(log => {
      const logDate = log.date || log.createdAt?.split('T')[0]
      return logDate === today
    })
    
    if (!todayLog) return false
    
    switch (habit.habitType) {
      case 'YES_NO':
        return todayLog.completed === true
      case 'NUMERIC':
        return habit.numberOfTimes !== undefined && 
               todayLog.numberOfTimes !== undefined && 
               todayLog.numberOfTimes >= habit.numberOfTimes
      case 'DURATION':
        return habit.duration !== undefined && 
               todayLog.duration !== undefined && 
               todayLog.duration >= habit.duration
      default:
        return false
    }
  }

  // Get today's log value for a habit
  function getTodayLogValue(habitId: number, field: 'numberOfTimes' | 'duration'): number | undefined {
    const logs = habitLogs.value.get(habitId)
    const today = new Date().toISOString().split('T')[0]!
    
    if (!logs || logs.length === 0) {
      return undefined
    }

    const todayLog = logs.find(log => {
      const logDate = log.date || log.createdAt?.split('T')[0]
      return logDate === today
    })
    
    return todayLog?.[field]
  }

  // Helper function to format date consistently without timezone conversion
  function formatDateToString(date: Date): string {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  // Get status for a specific date
  function getDateStatus(habitId: number, date: Date): 'complete' | 'partial' | 'unlogged' {
    const habit = habits.value.find(h => h.id === habitId)
    if (!habit) return 'unlogged'
    
    const logs = habitLogs.value.get(habitId)
    const dateStr = formatDateToString(date)
    
    if (!logs || logs.length === 0) return 'unlogged'
    
    const log = logs.find(l => {
      const logDate = l.date || l.createdAt?.split('T')[0]
      return logDate === dateStr
    })
    
    if (!log) return 'unlogged'
    
    switch (habit.habitType) {
      case 'YES_NO':
        return log.completed ? 'complete' : 'unlogged'
      case 'NUMERIC':
        if (habit.numberOfTimes && log.numberOfTimes !== undefined) {
          if (log.numberOfTimes >= habit.numberOfTimes) return 'complete'
          if (log.numberOfTimes > 0) return 'partial'
        }
        return 'unlogged'
      case 'DURATION':
        if (habit.duration && log.duration !== undefined) {
          if (log.duration >= habit.duration) return 'complete'
          if (log.duration > 0) return 'partial'
        }
        return 'unlogged'
      default:
        return 'unlogged'
    }
  }

  function getStatusColor(status: 'complete' | 'partial' | 'unlogged'): string {
    switch (status) {
      case 'complete': return 'bg-green-500 hover:bg-green-600'
      case 'partial': return 'bg-orange-500 hover:bg-orange-600'
      case 'unlogged': return 'bg-gray-300 dark:bg-gray-700 hover:bg-gray-400 dark:hover:bg-gray-600'
    }
  }

  // Format date for display
  function formatDate(dateStr?: string): string {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
  }

  return {
    getLogCount,
    isLoggedToday,
    isCompletedToday,
    getTodayLogValue,
    getDateStatus,
    getStatusColor,
    formatDate
  }
}
