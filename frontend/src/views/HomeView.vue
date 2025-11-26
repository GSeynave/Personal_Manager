<script setup lang="ts">
import HomeDashboard from '../components/Dashboard/HomeDashboard.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

console.log("Is user authenticated?", authStore.isAuthenticated)
console.log("Current user:", authStore.jwtToken())
</script>

<template>
  <main>

    <!-- <HomeDashboard /> -->
  </main>
</template>
