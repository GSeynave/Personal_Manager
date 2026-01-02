import HabitData, { type HabitType, type HabitFrequency, type DayOfWeek } from '@/model/habits/HabitData'
import HabitLog from '@/model/habits/HabitLog'
import HabitsService from '@/services/habits/HabitsService'
import { defineStore } from 'pinia'
import { ref } from 'vue'

const habitService = new HabitsService()

export type HabitFilter = 'all' | 'active' | 'completed'

export const useHabitStore = defineStore('habit', () => {
  // State
  const habits = ref<HabitData[]>([])
  const habitLogs = ref<Map<number, HabitLog[]>>(new Map())
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Actions
  async function fetchHabits() {
    isLoading.value = true
    error.value = null
    
    try {
      console.log('Fetching habits...')
      const data: HabitData[] = await habitService.getHabits()
      console.log('Habits retrieved:', data)
      habits.value = data || []
    } catch (err: any) {
      console.error('Error fetching habits:', err)
      error.value = err.message || 'Failed to fetch habits'
    } finally {
      isLoading.value = false
    }
  }

  async function fetchHabitLogs(habitId: number) {
    error.value = null
    
    try {
      console.log('Fetching logs for habit:', habitId)
      const logs = await habitService.getHabitLogs(habitId)
      console.log('Logs retrieved:', logs)
      // Create a new Map to trigger reactivity
      const newMap = new Map(habitLogs.value)
      newMap.set(habitId, logs || [])
      habitLogs.value = newMap
    } catch (err: any) {
      console.error('Error fetching habit logs:', err)
      error.value = err.message || 'Failed to fetch habit logs'
    }
  }

  async function addHabit(
    title: string, 
    description: string, 
    category: string, 
    habitType: HabitType,
    frequency: HabitFrequency,
    scheduledDays: DayOfWeek[],
    numberOfTimes?: number,
    duration?: number
  ) {
    error.value = null
    
    try {
      const newHabit = await habitService.createHabit(
        title, 
        description, 
        category, 
        habitType,
        frequency,
        scheduledDays,
        numberOfTimes,
        duration
      )
      console.log('Habit created:', newHabit)
      // Refresh the list after adding
      await fetchHabits()
    } catch (err: any) {
      console.error('Error creating habit:', err)
      error.value = err.message || 'Failed to create habit'
      throw err
    }
  }

  async function updateHabit(
    id: number,
    title: string,
    description?: string,
    category?: string,
    habitType?: HabitType,
    frequency?: HabitFrequency,
    scheduledDays?: DayOfWeek[],
    numberOfTimes?: number,
    duration?: number
  ) {
    error.value = null
    
    try {
      await habitService.updateHabit(id, title, description, category, habitType, frequency, scheduledDays, numberOfTimes, duration)
      console.log('Habit updated:', id)
      // Refresh the list after updating
      await fetchHabits()
    } catch (err: any) {
      console.error('Error updating habit:', err)
      error.value = err.message || 'Failed to update habit'
      throw err
    }
  }

  async function deleteHabit(id: number) {
    error.value = null
    
    try {
      await habitService.deleteHabit(id)
      console.log('Habit deleted:', id)
      // Remove from local state
      habits.value = habits.value.filter(h => h.id !== id)
      habitLogs.value.delete(id)
    } catch (err: any) {
      console.error('Error deleting habit:', err)
      error.value = err.message || 'Failed to delete habit'
      throw err
    }
  }

  async function addHabitLog(
    habitId: number,
    date: string, // ISO-8601 format (YYYY-MM-DD)
    completed?: boolean,
    numberOfTimes?: number,
    duration?: number
  ) {
    error.value = null
    
    try {
      await habitService.createHabitLog(habitId, date, completed, numberOfTimes, duration)
      console.log('Habit log added for habit:', habitId, 'on date:', date)
      // Refresh logs for this habit
      await fetchHabitLogs(habitId)
    } catch (err: any) {
      console.error('Error adding habit log:', err)
      error.value = err.message || 'Failed to add habit log'
      throw err
    }
  }

  async function updateHabitLog(
    habitId: number,
    logId: number,
    date: string,
    completed?: boolean,
    numberOfTimes?: number,
    duration?: number
  ) {
    error.value = null
    
    try {
      await habitService.updateHabitLog(habitId, logId, date, completed, numberOfTimes, duration)
      console.log('Habit log updated:', logId)
      // Refresh logs for this habit
      await fetchHabitLogs(habitId)
    } catch (err: any) {
      console.error('Error updating habit log:', err)
      error.value = err.message || 'Failed to update habit log'
      throw err
    }
  }

  async function deleteHabitLog(habitId: number, logId: number) {
    error.value = null
    
    try {
      await habitService.deleteHabitLog(habitId, logId)
      console.log('Habit log deleted:', logId)
      // Refresh logs for this habit
      await fetchHabitLogs(habitId)
    } catch (err: any) {
      console.error('Error deleting habit log:', err)
      error.value = err.message || 'Failed to delete habit log'
      throw err
    }
  }

  return {
    // State
    isLoading,
    error,
    
    // Getters
    habits,
    habitLogs,
    
    // Actions
    fetchHabits,
    fetchHabitLogs,
    addHabit,
    updateHabit,
    deleteHabit,
    addHabitLog,
    updateHabitLog,
    deleteHabitLog
  }
})
