import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

let isInterceptorSetup = false

export function setupAxiosInterceptor() {
  // Only setup once
  if (isInterceptorSetup) {
    return
  }

  // Configure axios defaults (optional, only if backend requires credentials)
  axios.defaults.withCredentials = false // Set to true if backend uses cookies

  // Request interceptor to add auth token
  axios.interceptors.request.use(
    async (config) => {
      const authStore = useAuthStore()
      
      // Only add token for API requests (not for external URLs)
      if (config.url?.startsWith('/api')) {
        try {
          console.log('Attempting to get token for request:', config.url)
          const token = await authStore.jwtToken()
          
          if (token) {
            config.headers.Authorization = `Bearer ${token}`
            console.log('✓ Auth token added to request:', config.url, 'Token:', token.substring(0, 20) + '...')
          } else {
            console.warn('⚠ No auth token available for request:', config.url)
            console.log('Auth state:', {
              isAuthenticated: authStore.isAuthenticated,
              currentUser: authStore.currentUser,
              userId: authStore.userId
            })
          }
        } catch (error) {
          console.error('❌ Error getting auth token:', error)
        }
      }
      
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // Response interceptor to handle auth errors
  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      const authStore = useAuthStore()
      
      console.error('Request failed:', {
        url: error.config?.url,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      })
      
      // Handle 401 Unauthorized errors
      if (error.response?.status === 401) {
        console.error('401 Unauthorized - logging out')
        await authStore.logout()
        
        // Optionally redirect to login
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
      
      // Handle 403 Forbidden errors
      if (error.response?.status === 403) {
        console.error('403 Forbidden - Access denied')
        console.log('Token might be invalid or user lacks permissions')
      }
      
      return Promise.reject(error)
    }
  )

  isInterceptorSetup = true
  console.log('Axios interceptor setup complete')
}
