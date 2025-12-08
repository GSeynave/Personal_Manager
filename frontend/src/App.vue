<script setup lang="ts">
import { RouterView, useRoute, RouterLink } from 'vue-router'
import { computed } from 'vue'
import TopNavbar from './components/TopNavbar.vue'
import AppSidebar from './components/AppSidebar.vue'
import { useAuthStore } from './stores/auth'
import { SidebarProvider, SidebarInset, SidebarTrigger } from './components/ui/sidebar'

const route = useRoute()
const authStore = useAuthStore()

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
  <div class="app-layout">
    <SidebarProvider v-if="showSidebar">
      <AppSidebar />
      <SidebarInset>
        <header class="flex h-16 shrink-0 items-center justify-between gap-4 border-b px-4">
          <SidebarTrigger class="-ml-1" />
          <RouterLink to="/profile" class="flex items-center gap-3 hover:opacity-80 transition-opacity">
            <div class="flex flex-col items-end gap-0.5 leading-none">
              <span class="text-sm font-semibold">{{ authStore.userIdentity?.userTag || authStore.userEmail }}</span>
              <span class="text-xs font-medium" :style="{ color: authStore.userIdentity?.borderColor || '#4f46e5' }">
                {{ authStore.userIdentity?.title || 'Beginner' }}
              </span>
            </div>
            <div 
              class="flex aspect-square size-10 items-center justify-center rounded-full overflow-hidden"
              :style="{ border: `3px solid ${authStore.userIdentity?.borderColor || '#4f46e5'}` }"
            >
              <img 
                v-if="getUserAvatar()" 
                :src="getUserAvatar()" 
                :alt="authStore.userIdentity?.userTag || 'User'"
                class="w-full h-full object-cover"
              />
            </div>
          </RouterLink>
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
