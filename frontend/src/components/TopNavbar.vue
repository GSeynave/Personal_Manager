<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import ThemeSwitcher from './ThemeSwitcher.vue'

const router = useRouter()
const authStore = useAuthStore()
const isUserMenuOpen = ref(false)
const showThemeSwitcher = ref(false)
const themeToggleRef = ref<HTMLElement | null>(null)

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const toggleThemeSwitcher = () => {
  showThemeSwitcher.value = !showThemeSwitcher.value
}

const handleClickOutside = (event: MouseEvent) => {
  if (themeToggleRef.value && !themeToggleRef.value.contains(event.target as Node)) {
    showThemeSwitcher.value = false
  }
  if (isUserMenuOpen.value && !(event.target as HTMLElement).closest('.user-menu-wrapper')) {
    isUserMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const handleLogout = async () => {
  await authStore.logout()
  isUserMenuOpen.value = false
  router.push('/')
}

const handleLogin = () => {
  router.push('/login')
}

const goToProfile = () => {
  router.push('/profile')
  isUserMenuOpen.value = false
}

// Display userTag if available, otherwise email
const displayName = computed(() => {
  return authStore.userIdentity?.userTag || authStore.userEmail || 'User'
})

// theme
const themeStore = useThemeStore()

const themeIcons = {
  natural: '🌿',
  cosmic: '🌌',
  zen: '☯️',
  dark: '🌙'
}
</script>

<template>
  <header class="top-navbar">
    <div class="navbar-content">
      <!-- App Logo/Name (clickable to go home) -->
      <RouterLink to="/" class="navbar-brand">
        <h1 class="app-name">Personal Manager</h1>
        <span class="app-subtitle">Dashboard</span>
      </RouterLink>

      <!-- Right Section: User Menu -->
      <div class="navbar-right">
        <div ref="themeToggleRef" class="theme-toggle-wrapper">
          <button
            class="theme-toggle btn-ghost"
            @click="toggleThemeSwitcher"
            :title="'Change theme'"
          >
            <span>{{ themeIcons[themeStore.theme] }}</span>
          </button>
          <div v-if="showThemeSwitcher" class="theme-dropdown">
            <ThemeSwitcher />
          </div>
        </div>
        <div class="user-section">
          <button v-if="!authStore.isAuthenticated" class="btn-login" @click="handleLogin">Login</button>
          <div v-else class="user-menu-wrapper">
            <button class="user-profile-btn" @click="toggleUserMenu">
              <span class="profile-icon">👤</span>
              <span class="username">{{ displayName }}</span>
              <span class="dropdown-icon" :class="{ open: isUserMenuOpen }">▼</span>
            </button>
            <div v-if="isUserMenuOpen" class="user-dropdown">
              <button class="dropdown-item" @click="goToProfile">Profile</button>
              <hr class="dropdown-divider" />
              <button class="dropdown-item logout-btn" @click="handleLogout">Logout</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.top-navbar {
  background: hsl(0, 0%, 100%);
  border-bottom: 1px solid hsl(214.3, 31.8%, 91.4%);
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}

.navbar-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 100%;
}

.navbar-brand {
  display: flex;
  align-items: baseline;
  gap: 12px;
  text-decoration: none;
  cursor: pointer;
  transition: opacity 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.navbar-brand:hover {
  opacity: 0.8;
}

.navbar-brand:active {
  opacity: 1;
}

.app-name {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: hsl(222.2, 47.4%, 11.2%);
  letter-spacing: -0.025em;
}

.app-subtitle {
  font-size: 0.875rem;
  color: hsl(215.4, 16.3%, 46.9%);
  font-weight: 500;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.theme-toggle-wrapper {
  position: relative;
}

.theme-toggle {
  padding: 8px;
  background: hsl(0, 0%, 100%);
  border: 1px solid hsl(214.3, 31.8%, 91.4%);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1.125rem;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  width: 36px;
}

.theme-toggle:hover {
  background: hsl(210, 40%, 96.1%);
  border-color: hsl(214.3, 31.8%, 85%);
}

.theme-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  z-index: 100;
  animation: slideDown 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-section {
  position: relative;
  display: flex;
  align-items: center;
}

.btn-login {
  padding: 8px 16px;
  background: hsl(222.2, 47.4%, 11.2%);
  color: hsl(210, 40%, 98%);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
  height: 36px;
}

.btn-login:hover {
  background: hsl(222.2, 47.4%, 11.2%, 0.9);
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.user-menu-wrapper {
  position: relative;
}

.user-profile-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: hsl(0, 0%, 100%);
  border: 1px solid hsl(214.3, 31.8%, 91.4%);
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
  height: 36px;
}

.user-profile-btn:hover {
  background: hsl(210, 40%, 96.1%);
  border-color: hsl(214.3, 31.8%, 85%);
}

.profile-icon {
  font-size: 1rem;
}

.username {
  font-size: 0.875rem;
  color: hsl(222.2, 47.4%, 11.2%);
}

.dropdown-icon {
  font-size: 0.625rem;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-icon.open {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: hsl(0, 0%, 100%);
  border: 1px solid hsl(214.3, 31.8%, 91.4%);
  border-radius: 8px;
  min-width: 180px;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  z-index: 100;
  overflow: hidden;
  padding: 4px;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 8px 12px;
  text-align: left;
  background: transparent;
  border: none;
  color: hsl(222.2, 47.4%, 11.2%);
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  border-radius: 4px;
}

.dropdown-item:hover {
  background: hsl(210, 40%, 96.1%);
  color: hsl(222.2, 47.4%, 11.2%);
}

.dropdown-divider {
  margin: 4px 0;
  border: none;
  border-top: 1px solid hsl(214.3, 31.8%, 91.4%);
}

.logout-btn {
  color: hsl(0, 84.2%, 60.2%);
}

.logout-btn:hover {
  background: hsl(0, 84.2%, 60.2%, 0.1) !important;
  color: hsl(0, 84.2%, 60.2%) !important;
}

@media (max-width: 768px) {
  .top-navbar {
    height: 60px;
    padding: 0 16px;
  }

  .app-name {
    font-size: 1.2rem;
  }

  .app-subtitle {
    display: none;
  }

  .username {
    display: none;
  }
}
</style>
