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
      meta: { requiresAuth: true }, // Protected route
    },
    {
      path: '/accounting',
      name: 'accounting',
      component: AccountingView,
      meta: { requiresAuth: true }, // Protected route
    },
    {
      path: '/diet',
      name: 'diet',
      component: () => import('../views/DietView.vue'),
      meta: { requiresAuth: true }, // Protected route
    },
    {
      path: '/energy',
      name: 'energy',
      component: () => import('../views/EnergyView.vue'),
      meta: { requiresAuth: true }, // Protected route
    },
    {
      path: '/calendar',
      name: 'calendar',
      component: () => import('../views/CalendarView.vue'),
      meta: { requiresAuth: true }, // Protected route
    },
    {
      path: '/sleep',
      name: 'sleep',
      component: () => import('../views/SleepView.vue'),
      meta: { requiresAuth: true }, // Protected route
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
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      // Redirect to login if not authenticated
      next({ name: 'login', query: { redirect: to.fullPath } })
    } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
      // Redirect to home if already authenticated
      next({ name: 'home' })
    } else {
      next()
    }
  }
})

export default router
