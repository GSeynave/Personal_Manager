import HabitData from '@/model/HabitData'
import HabitLog from '@/model/HabitLog'
import HabitsService from '@/services/HabitsService'
import { defineStore } from 'pinia'
import { ref } from 'vue'

const habitService = new HabitsService()

export type HabitFilter = 'all' | 'active' | 'completed'

export const useHabitStore = defineStore('habit', () => {
  // State
  const habits = ref<HabitData[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)


  const mockedHabitsLogs = [
    new HabitLog(1, new Date('2024-10-01')),
    new HabitLog(2, new Date('2024-10-02')),
  ]
    const mockedHabits: HabitData[] = [
    new HabitData(1,'Morning Run', 'Run 5km every morning', 'Health', mockedHabitsLogs,'boolean', undefined ),
    new HabitData(2,'Read Book', 'Read 20 pages of a book', 'Personal Development', [], '', undefined ),
    new HabitData(3, 'Meditation', 'Meditate for 15 minutes', 'Wellness', [], '', undefined ),
  ]

  const useMockData = true  // Set to true to use mocked data

  if (useMockData) {
    habits.value = mockedHabits
  }

  // Actions
  async function fetchHabits() {
    isLoading.value = true
    error.value = null
    
    try {
      console.log('Fetching habits...')
      const data: HabitData[] = await habitService.getHabits()
      console.log('Habits retrieved:', data)
      habits.value = data || []
    if (useMockData) {
      habits.value = mockedHabits
    }
    } catch (err: any) {
      console.error('Error fetching habits:', err)
      error.value = err.message || 'Failed to fetch habits'
    } finally {
      isLoading.value = false
    }
  }

  async function addHabit(title: string, description: string, category: string, type: string, unit?: string) {
    error.value = null
    
    try {
      const newHabit = await habitService.createHabit(title, description, category, type, unit)
      console.log('Habit created:', newHabit)
      // Refresh the list after adding
      await fetchHabits()
    } catch (err: any) {
      console.error('Error creating habit:', err)
      error.value = err.message || 'Failed to create habit'
      throw err
    }
  }

  async function updateHabit(habit: HabitData) {
    error.value = null
    
    
    try {
      await habitService.updateHabit(habit)
      console.log('Habit updated:', habit)
    } catch (err: any) {
      console.error('Error updating habit:', err)
      error.value = err.message || 'Failed to update habit'
      throw err
    }
  }

  async function deleteHabit(id: number ) {
    error.value = null
    
    // Find and remove from ungrouped or grouped
    try {
      await habitService.deleteHabit(id)
      console.log('Habit deleted:', id)
    } catch (err: any) {
      console.error('Error deleting habit:', err)
      error.value = err.message || 'Failed to delete habit'
      throw err
    }
  }

  async function addHabitLog(habitId: number, date: string) {
    error.value = null
    
    try {
      await habitService.addHabitLog(habitId, new Date(date))
      console.log('Habit log added for habit:', habitId, 'on date:', date)
    } catch (err: any) {
      console.error('Error adding habit log:', err)
      error.value = err.message || 'Failed to add habit log'
      throw err
    }
  }

  return {
    // State
    isLoading,
    error,
    
    // Getters
    habits,

    
    // Actions
    fetchHabits,
    addHabit,
    updateHabit,
    deleteHabit,
    addHabitLog

  }
})
