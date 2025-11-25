import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TodoView from '@/views/TodoView.vue'
import AccountingView from '@/views/AccoutingView.vue'

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
    },
    {
      path: '/accounting',
      name: 'accounting',
      component: AccountingView,
    },
    {
      path: '/diet',
      name: 'diet',
      component: () => import('../views/DietView.vue'),
    },
    {
      path: '/energy',
      name: 'energy',
      component: () => import('../views/EnergyView.vue'),
    },
    {
      path: '/calendar',
      name: 'calendar',
      component: () => import('../views/CalendarView.vue'),
    },
    {
      path: '/sleep',
      name: 'sleep',
      component: () => import('../views/SleepView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
