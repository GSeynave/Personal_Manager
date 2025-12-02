import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TodoView from '@/views/TodoView.vue'
import AccountingView from '@/views/AccoutingView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/todo',
      name: 'todo',
      component: TodoView,
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/accounting',
      name: 'accounting',
      component: AccountingView,
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/diet',
      name: 'diet',
      component: () => import('../views/DietView.vue'),
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/energy',
      name: 'energy',
      component: () => import('../views/EnergyView.vue'),
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/calendar',
      name: 'calendar',
      component: () => import('../views/CalendarView.vue'),
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/sleep',
      name: 'sleep',
      component: () => import('../views/SleepView.vue'),
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/habits',
      name: 'habits',
      component: () => import('../views/HabitsView.vue'),
      meta: { requiresAuth: true, requiresIdentity: true }, // Protected route
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresGuest: true }, // Only for non-authenticated users
    },
    {
      path: '/first-connection',
      name: 'first-connection',
      component: () => import('../views/FirstConnectionView.vue'),
      meta: { requiresAuth: true, skipIdentityCheck: true }, // Requires auth but skips identity check
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }, // Requires auth but not identity
    },
  ],
})

// Navigation guard for protected routes
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Wait for auth to initialize
  if (authStore.isLoading) {
    // Watch for loading to complete
    const unwatch = authStore.$subscribe(() => {
      if (!authStore.isLoading) {
        unwatch()
        checkRoute()
      }
    })
  } else {
    checkRoute()
  }
  
  function checkRoute() {
    // Check authentication first
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      // Redirect to login if not authenticated
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }
    
    // If already authenticated and trying to access guest-only routes
    if (to.meta.requiresGuest && authStore.isAuthenticated) {
      next({ name: 'home' })
      return
    }
    
    // Check user identity for protected routes (skip for first-connection and login)
    if (to.meta.requiresIdentity && authStore.isAuthenticated) {
      // Wait for identity to load if it's currently loading
      if (authStore.isIdentityLoading) {
        const unwatch = authStore.$subscribe(() => {
          if (!authStore.isIdentityLoading) {
            unwatch()
            checkIdentity()
          }
        })
        return
      }
      
      checkIdentity()
      return
    }
    
    next()
  }
  
  function checkIdentity() {
    if (!authStore.hasIdentity) {
      // Redirect to first connection if identity is not set
      console.log('User identity not set, redirecting to first-connection')
      next({ name: 'first-connection' })
    } else {
      console.log('User identity set, proceeding to route', authStore.userIdentity)
      next()
    }
  }
})

export default router
