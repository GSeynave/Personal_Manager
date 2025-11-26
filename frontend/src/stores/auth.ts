import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { User } from 'firebase/auth'
import FirebaseService from '@/services/FirebaseService'

export const useAuthStore = defineStore('auth', () => {
  // State
  const currentUser = ref<User | null>(null)
  const isLoading = ref(true)
  const error = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => currentUser.value !== null)
  const userEmail = computed(() => currentUser.value?.email || null)
  const userId = computed(() => currentUser.value?.uid || null)

  // Actions
  async function initialize() {
    isLoading.value = true
    
    // Initialize Firebase
    FirebaseService.initFirebase()
    
    // Set up auth state listener
    FirebaseService.onAuthStateChange((user) => {
      currentUser.value = user
      isLoading.value = false
      
      if (user) {
        console.log('User authenticated:', user.email)
      } else {
        console.log('User not authenticated')
      }
    })
  }

  async function login(email: string, password: string) {
    error.value = null
    isLoading.value = true
    
    try {
      const result = await FirebaseService.signIn(email, password)
      
      if (result.success) {
        currentUser.value = result.user || null
        return { success: true }
      } else {
        error.value = result.error || 'Login failed'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || 'An error occurred during login'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  async function signup(email: string, password: string) {
    error.value = null
    isLoading.value = true
    
    try {
      const result = await FirebaseService.signUp(email, password)
      
      if (result.success) {
        currentUser.value = result.user || null
        return { success: true }
      } else {
        error.value = result.error || 'Signup failed'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || 'An error occurred during signup'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  async function logout() {
    error.value = null
    isLoading.value = true
    
    try {
      const result = await FirebaseService.signOutUser()
      
      if (result.success) {
        currentUser.value = null
        return { success: true }
      } else {
        error.value = result.error || 'Logout failed'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || 'An error occurred during logout'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  async function jwtToken(): Promise<string | null> {
    if (!currentUser.value) {
      console.log("No user logged in, cannot fetch JWT token")
      return null
    }
    
    try {
      const token = await currentUser.value.getIdToken()
      console.log("JWT token fetched successfully for user:", currentUser.value.email)
      return token
    } catch (error) {
      console.error("Error fetching JWT token:", error)
      return null
    }
  }

  return {
    // State
    currentUser,
    isLoading,
    error,
    
    // Getters
    isAuthenticated,
    userEmail,
    userId,
    
    // Actions
    initialize,
    login,
    signup,
    logout,
    clearError,
    jwtToken,
  }
})
