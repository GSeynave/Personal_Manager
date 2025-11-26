<script setup lang="ts">
import { RouterView } from 'vue-router'
import TopNavbar from './components/TopNavbar.vue'
import ModuleBar from './components/ModuleBar.vue'
</script>

<template>
  <div class="app-layout">
    <!-- Top Navbar -->
    <TopNavbar />

    <!-- Main Content Area -->
    <div class="main-content">
      <!-- Left Module Bar -->
      <ModuleBar />

      <!-- Page Content -->
      <div class="page-container">
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
