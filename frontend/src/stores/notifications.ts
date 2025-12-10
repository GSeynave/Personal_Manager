import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { GamificationNotification } from '@/services/WebSocketService'

export const useNotificationStore = defineStore('notifications', () => {
  const notifications = ref<GamificationNotification[]>([])
  const currentToast = ref<GamificationNotification | null>(null)

  const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length)

  const hasUnread = computed(() => unreadCount.value > 0)

  function addNotification(notification: GamificationNotification) {
    notifications.value.unshift(notification)
    // Keep only last 50 notifications
    if (notifications.value.length > 50) {
      notifications.value = notifications.value.slice(0, 50)
    }
    // Show toast for new notification
    showToast(notification)
  }

  function showToast(notification: GamificationNotification) {
    currentToast.value = notification
    // Auto-hide toast after 5 seconds
    setTimeout(() => {
      if (currentToast.value?.id === notification.id) {
        currentToast.value = null
      }
    }, 5000)
  }

  function hideToast() {
    currentToast.value = null
  }

  function markAsRead(notificationId: string) {
    const notification = notifications.value.find((n) => n.id === notificationId)
    if (notification) {
      notification.read = true
    }
  }

  function markAllAsRead() {
    notifications.value.forEach((n) => {
      n.read = true
    })
  }

  function clearAll() {
    notifications.value = []
  }

  return {
    notifications,
    currentToast,
    unreadCount,
    hasUnread,
    addNotification,
    showToast,
    hideToast,
    markAsRead,
    markAllAsRead,
    clearAll,
  }
})
