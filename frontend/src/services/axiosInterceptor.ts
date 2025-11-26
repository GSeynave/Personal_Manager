import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

let isInterceptorSetup = false

export function setupAxiosInterceptor() {
  // Only setup once
  if (isInterceptorSetup) {
    return
  }

  // Request interceptor to add auth token
  axios.interceptors.request.use(
    async (config) => {
      const authStore = useAuthStore()
      
      // Only add token for API requests (not for external URLs)
      if (config.url?.startsWith('/api')) {
        try {
          const token = await authStore.jwtToken()
          
          if (token) {
            config.headers.Authorization = `Bearer ${token}`
            console.log('Added auth token to request:', config.url)
          } else {
            console.log('No auth token available for request:', config.url)
          }
        } catch (error) {
          console.error('Error getting auth token:', error)
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
      
      // Handle 401 Unauthorized errors
      if (error.response?.status === 401) {
        console.error('Unauthorized request - logging out')
        await authStore.logout()
        
        // Optionally redirect to login
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
      
      return Promise.reject(error)
    }
  )

  isInterceptorSetup = true
  console.log('Axios interceptor setup complete')
}
