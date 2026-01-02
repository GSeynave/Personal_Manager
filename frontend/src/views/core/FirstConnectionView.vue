<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import InitUserTag from '@/components/user/InitUserTag.vue'

const router = useRouter()
const authStore = useAuthStore()

// After setting user tag, refresh identity and redirect
async function handleIdentitySet() {
  await authStore.fetchUserIdentity()
  router.push('/')
}
</script>

<template>
  <main class="first-connection-view">
    <div class="container">
      <h1>Welcome! Let's set up your profile</h1>
      <p class="subtitle">Please complete your user identity to continue</p>
      <InitUserTag @identity-set="handleIdentitySet" />
    </div>
  </main>
</template>

<style scoped>
.first-connection-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
}

.container {
  max-width: 600px;
  width: 100%;
}

h1 {
  text-align: center;
  margin-bottom: 1rem;
  color: var(--color-heading);
}

.subtitle {
  text-align: center;
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
}
</style>
