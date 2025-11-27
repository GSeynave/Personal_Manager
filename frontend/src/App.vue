<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import TopNavbar from './components/TopNavbar.vue'
import ModuleBar from './components/ModuleBar.vue'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const authStore = useAuthStore()

// Hide ModuleBar on certain routes or when not authenticated
const showModuleBar = computed(() => {
  if (!authStore.isAuthenticated) {
    return false
  }
  const hiddenRoutes = ['login', 'first-connection']
  return !hiddenRoutes.includes(route.name as string)
})
</script>

<template>
  <div class="app-layout">
    <!-- Top Navbar -->
    <TopNavbar />

    <!-- Main Content Area -->
    <div class="main-content">
      <!-- Left Module Bar (hidden on login/first-connection) -->
      <ModuleBar v-if="showModuleBar" />

      <!-- Page Content -->
      <div class="page-container" :class="{ 'full-width': !showModuleBar }">
        <RouterView />
      </div>
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

.main-content {
  flex: 1;
  display: flex;
  gap: 0;
  overflow: hidden;
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
