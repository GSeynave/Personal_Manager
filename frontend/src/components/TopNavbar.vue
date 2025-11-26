<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const isUserMenuOpen = ref(false)

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const handleLogout = async () => {
  await authStore.logout()
  isUserMenuOpen.value = false
  router.push('/')
}

const handleLogin = () => {
  router.push('/login')
}

// theme
const themeStore = useThemeStore()
const toggleTheme = () => themeStore.toggle()
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
        <button
          class="theme-toggle btn-ghost"
          @click="toggleTheme"
          :title="themeStore.theme === 'light' ? 'Switch to dark' : 'Switch to light'"
        >
          <span v-if="themeStore.theme === 'light'">🌙</span>
          <span v-else>☀️</span>
        </button>
        <div class="user-section">
          <button v-if="!authStore.isAuthenticated" class="btn-login" @click="handleLogin">Login</button>
          <div v-else class="user-menu-wrapper">
            <button class="user-profile-btn" @click="toggleUserMenu">
              <span class="profile-icon">👤</span>
              <span class="username">{{ authStore.userEmail }}</span>
              <span class="dropdown-icon" :class="{ open: isUserMenuOpen }">▼</span>
            </button>
            <div v-if="isUserMenuOpen" class="user-dropdown">
              <a href="#" class="dropdown-item">Profile</a>
              <a href="#" class="dropdown-item">Settings</a>
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
