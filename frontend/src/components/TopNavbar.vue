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
        <h1 class="app-name">🏠 Personal Manager</h1>
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
  background: rgba(var(--bg-primary), 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-light);
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: var(--shadow-md);
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
  gap: var(--spacing-sm);
  text-decoration: none;
  cursor: pointer;
  transition: transform var(--transition-base);
}

.navbar-brand:hover {
  transform: translateY(-2px);
}

.navbar-brand:active {
  transform: translateY(0);
}

.app-name {
  margin: 0;
  font-size: var(--text-2xl);
  font-weight: var(--fw-bold);
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.app-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--fw-medium);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.theme-toggle-wrapper {
  position: relative;
}

.theme-toggle {
  padding: var(--spacing-sm);
  background: var(--bg-secondary);
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  font-size: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.theme-toggle:hover {
  background: var(--bg-tertiary);
  transform: translateY(-2px);
}

.theme-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: var(--spacing-sm);
  z-index: 100;
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
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
  padding: var(--spacing-sm) var(--spacing-lg);
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: var(--fw-semibold);
  transition: all var(--transition-slow);
}

.btn-login:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.4);
}

.user-menu-wrapper {
  position: relative;
}

.user-profile-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--bg-secondary);
  border: 1px solid var(--border-medium);
  border-radius: 20px;
  cursor: pointer;
  font-weight: var(--fw-semibold);
  transition: all var(--transition-base);
}

.user-profile-btn:hover {
  background: var(--bg-tertiary);
  border-color: var(--border-medium);
}

.profile-icon {
  font-size: 1.2rem;
}

.username {
  font-size: var(--text-sm);
  color: var(--text-primary);
}

.dropdown-icon {
  font-size: 0.7rem;
  transition: transform var(--transition-base);
}

.dropdown-icon.open {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: var(--spacing-sm);
  background: var(--bg-primary);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-lg);
  min-width: 160px;
  box-shadow: var(--shadow-lg);
  z-index: 100;
  overflow: hidden;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: var(--spacing-md);
  text-align: left;
  background: transparent;
  border: none;
  color: var(--text-primary);
  cursor: pointer;
  font-size: var(--text-sm);
  transition: background var(--transition-base);
  text-decoration: none;
}

.dropdown-item:hover {
  background: rgba(var(--primary-rgb), 0.1);
  color: var(--primary);
}

.dropdown-divider {
  margin: var(--spacing-xs) 0;
  border: none;
  border-top: 1px solid var(--border-light);
}

.logout-btn {
  color: var(--error);
}

.logout-btn:hover {
  background: rgba(211, 47, 47, 0.1) !important;
  color: #b71c1c !important;
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
