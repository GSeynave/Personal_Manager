<script setup lang="ts">
import { RouterView, useRoute, RouterLink } from 'vue-router'
import { computed, onMounted, onUnmounted, watch, ref } from 'vue'
import { Bell } from 'lucide-vue-next'
import TopNavbar from './components/TopNavbar.vue'
import AppSidebar from './components/AppSidebar.vue'
import ToastNotification from './components/ToastNotification.vue'
import CircularProgress from './components/CircularProgress.vue'
import { useAuthStore } from './stores/auth'
import { useNotificationStore } from './stores/notifications'
import { webSocketService } from './services/WebSocketService'
import { SidebarProvider, SidebarInset, SidebarTrigger } from './components/ui/sidebar'
import axios from 'axios'

const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

// Game profile for progress bar
interface GameProfile {
  totalEssence: number
  currentLevel: number
  essenceToNextLevel: number
  progressToNextLevel: number
}

const gameProfile = ref<GameProfile | null>(null)
const isLevelingUp = ref(false)

// Fetch game profile
const fetchGameProfile = async () => {
  if (!authStore.isAuthenticated) return
  try {
    const response = await axios.get('/api/gamification/profile')
    const oldLevel = gameProfile.value?.currentLevel
    gameProfile.value = response.data
    
    // Detect level up
    if (oldLevel && gameProfile.value && gameProfile.value.currentLevel > oldLevel) {
      isLevelingUp.value = true
      setTimeout(() => isLevelingUp.value = false, 2000)
    }
  } catch (error) {
    console.error('Failed to fetch game profile:', error)
  }
}

// Fetch profile on mount and when authenticated
watch(() => authStore.isAuthenticated, (isAuth) => {
  if (isAuth) {
    fetchGameProfile()
  } else {
    gameProfile.value = null
  }
}, { immediate: true })

// WebSocket connection management
let isWebSocketInitialized = false

// Log initial state
console.log('🚀 App.vue mounted. Initial state:', {
  isAuthenticated: authStore.isAuthenticated,
  hasUser: !!authStore.currentUser,
  userIdentityId: authStore.userIdentity?.id,
  isWebSocketInitialized
})

watch(
  () => [authStore.isAuthenticated, authStore.userIdentity?.id],
  ([isAuthenticated, userId]) => {
    console.log('🔍 WebSocket watch triggered:', {
      isAuthenticated,
      userId,
      hasUser: !!authStore.currentUser,
      firebaseUid: authStore.currentUser?.uid,
      isWebSocketInitialized
    })
    
    if (isAuthenticated && authStore.currentUser && userId && !isWebSocketInitialized) {
      // Connect WebSocket when user is authenticated and we have the database user ID
      console.log('✅ Initializing WebSocket - Firebase UID:', authStore.currentUser.uid, 'Database ID:', userId)
      isWebSocketInitialized = true
      
      try {
        webSocketService.connect(authStore.currentUser.uid, userId as number)
        webSocketService.onNotification((notification) => {
          console.log('📬 Notification received in App.vue:', notification)
          notificationStore.addNotification(notification)
          
          // Refresh game profile when essence/level changes
          if (['ESSENCE_GAINED', 'LEVEL_UP'].includes(notification.type)) {
            fetchGameProfile()
          }
        })
      } catch (error) {
        console.error('❌ Error connecting WebSocket:', error)
        isWebSocketInitialized = false
      }
    } else if (!isAuthenticated && isWebSocketInitialized) {
      // Disconnect WebSocket when user logs out
      console.log('❌ Disconnecting WebSocket')
      isWebSocketInitialized = false
      webSocketService.disconnect()
    } else {
      console.log('⏸️ WebSocket not initialized. Waiting for conditions:', {
        needsAuthentication: !isAuthenticated,
        needsUser: !authStore.currentUser,
        needsUserId: !userId,
        alreadyInitialized: isWebSocketInitialized
      })
    }
  },
  { immediate: true }
)

onUnmounted(() => {
  if (isWebSocketInitialized) {
    webSocketService.disconnect()
    isWebSocketInitialized = false
  }
})

// Hide sidebar on certain routes or when not authenticated
const showSidebar = computed(() => {
  if (!authStore.isAuthenticated) {
    return false
  }
  const hiddenRoutes = ['login', 'first-connection']
  return !hiddenRoutes.includes(route.name as string)
})

const images = import.meta.glob('./assets/images/*.{jpg,png,svg}', {
  eager: true,
  as: 'url',
}) as Record<string, string>
const DEFAULT_IMAGE_PATH = './assets/images/default-avatar.svg'

function getUserAvatar() {
  if (!authStore.userIdentity?.userTag) return images[DEFAULT_IMAGE_PATH]

  const base = authStore.userIdentity.userTag.toLowerCase()
  const candidates = [
    `./assets/images/${base}.jpg`,
    `./assets/images/${base}.png`,
    `./assets/images/${base}.svg`,
  ]

  for (const path of candidates) {
    if (path in images) return images[path]
  }

  return images[DEFAULT_IMAGE_PATH]
}
</script>

<template>
  <ToastNotification />
  <div class="app-layout">
    <SidebarProvider v-if="showSidebar">
      <AppSidebar />
      <SidebarInset>
        <header class="flex h-16 shrink-0 items-center justify-between gap-4 border-b px-4">
          <SidebarTrigger class="-ml-1" />
          <div class="flex items-center gap-3">
            <button
              @click="$router.push('/progress')"
              class="relative p-2 hover:bg-muted rounded-md transition-colors"
              :title="notificationStore.hasUnread ? `${notificationStore.unreadCount} new notifications` : 'Notifications'"
            >
              <Bell class="w-5 h-5 text-[#6366f1]" />
              <span
                v-if="notificationStore.hasUnread"
                class="absolute top-1 right-1 bg-red-500 text-white text-xs font-bold rounded-full h-4 w-4 flex items-center justify-center"
              >
                {{ notificationStore.unreadCount > 9 ? '9+' : notificationStore.unreadCount }}
              </span>
            </button>
            <RouterLink to="/progress" class="flex items-center gap-3 hover:opacity-80 transition-opacity">
            <div class="flex flex-col items-end gap-0.5 leading-none">
              <span class="text-sm font-semibold">{{ authStore.userIdentity?.userTag || authStore.userEmail }}</span>
              <span class="text-xs font-medium flex items-center gap-1" :style="{ color: authStore.userIdentity?.borderColor || '#4f46e5' }">
                <span v-if="authStore.userIdentity?.equippedEmoji" class="text-sm">{{ authStore.userIdentity.equippedEmoji }}</span>
                {{ authStore.userIdentity?.title || 'Beginner' }}
              </span>
            </div>
            <CircularProgress
              :progress="gameProfile?.progressToNextLevel || 0"
              :size="48"
              :stroke-width="3"
              :progress-color="authStore.userIdentity?.borderColor || '#6366f1'"
              :is-leveling-up="isLevelingUp"
            >
              <div 
                class="flex aspect-square w-10 h-10 items-center justify-center rounded-full overflow-hidden bg-muted"
              >
                <img 
                  v-if="getUserAvatar()" 
                  :src="getUserAvatar()" 
                  :alt="authStore.userIdentity?.userTag || 'User'"
                  class="w-full h-full object-cover rounded-full"
                />
              </div>
            </CircularProgress>
          </RouterLink>
          </div>
        </header>
        <div class="flex flex-1 flex-col gap-4 p-4">
          <RouterView />
        </div>
      </SidebarInset>
    </SidebarProvider>
    
    <!-- If not showing sidebar, just show content -->
    <div v-else class="page-container full-width">
      <RouterView />
    </div>
  </div>

</template>

<style scoped>
.app-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: transparent;
}

.page-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.page-container.full-width {
  width: 100%;
  max-width: 100%;
}

/* Scrollbar styling */
.page-container::-webkit-scrollbar {
  width: 8px;
}

.page-container::-webkit-scrollbar-track {
  background: transparent;
}

.page-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.page-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}
</style>
