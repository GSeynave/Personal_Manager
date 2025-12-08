<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
  SidebarSeparator,
} from '@/components/ui/sidebar'
import { LogOut, User, Sparkles, Moon, Sun, Lock } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'

const authStore = useAuthStore()
const themeStore = useThemeStore()
const router = useRouter()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}


interface ModuleItem {
  id: string
  label: string
  to: string
  locked?: boolean
}

interface ModuleCategory {
  id: string
  label: string
  color: string
  modules: ModuleItem[]
}

const categories = ref<ModuleCategory[]>([
  {
    id: 'wellness',
    label: 'Wellness & Health',
    color: 'health',
    modules: [
      { id: 'habits', label: 'Habits', to: '/habits' },
    ],
  },
  {
    id: 'productivity',
    label: 'Productivity & Focus',
    color: 'productivity',
    modules: [
      { id: 'todo', label: 'Todo', to: '/todo' },
    ],
  },
  {
    id: 'finance',
    label: 'Finance & Resources',
    color: 'accounting',
    modules: [
      { id: 'accounting', label: 'Accounting', to: '/accounting' },
    ],
  },
])


</script>

<template>
  <Sidebar>
    <SidebarHeader class="py-2">
      <div class="flex items-center gap-2 px-2 py-2">
        <Sparkles class="w-5 h-5 text-primary" />
        <h1 class="text-lg font-bold text-primary">Elevate</h1>
      </div>
    </SidebarHeader>
    
    <SidebarContent class="gap-0">
      <template v-for="(category, index) in categories" :key="category.id">
        <SidebarGroup class="py-1">
          <SidebarGroupLabel class="text-xs" :class="`text-${category.color}`">{{ category.label }}</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu class="gap-0">
              <SidebarMenuItem v-for="module in category.modules" :key="module.id">
                <SidebarMenuButton 
                  :as-child="!module.locked"
                  :disabled="module.locked"
                  class="h-8 text-sm"
                  :class="module.locked ? 'opacity-50 cursor-not-allowed' : ''"
                >
                  <RouterLink v-if="!module.locked" :to="module.to">
                    <span>{{ module.label }}</span>
                  </RouterLink>
                  <div v-else class="flex items-center justify-between w-full">
                    <span>{{ module.label }}</span>
                    <Lock class="w-3 h-3 ml-auto opacity-50" />
                  </div>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
        <SidebarSeparator v-if="index < categories.length - 1" class="my-1" />
      </template>
    </SidebarContent>
    
    <SidebarFooter class="mt-auto py-2">
      <SidebarMenu class="gap-0">
        <SidebarMenuItem>
          <SidebarMenuButton class="h-8 text-sm" @click="themeStore.toggle()">
            <Moon v-if="themeStore.theme !== 'dark'" class="w-3 h-3" />
            <Sun v-else class="w-3 h-3" />
            <span>Theme</span>
          </SidebarMenuButton>
        </SidebarMenuItem>
        <SidebarMenuItem>
          <SidebarMenuButton class="h-8 text-sm" as-child>
            <RouterLink to="/profile">
              <User class="w-3 h-3" />
              <span>Profile</span>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
        <SidebarMenuItem>
          <SidebarMenuButton class="h-8 text-sm" @click="handleLogout">
            <LogOut class="w-3 h-3" />
            <span>Logout</span>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
    
    <SidebarRail />
  </Sidebar>
</template>
