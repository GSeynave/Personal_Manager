<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <main class="profile-view">
    <div class="profile-container">
      <div class="profile-card">
        <div class="profile-header">
          <div class="profile-icon">👤</div>
          <h1>My Profile</h1>
        </div>

        <div class="profile-content">
          <div class="profile-section">
            <h2>Account Information</h2>
            
            <div class="info-row">
              <span class="label">Email:</span>
              <span class="value">{{ authStore.userEmail || 'Not available' }}</span>
            </div>

            <div class="info-row">
              <span class="label">User Tag:</span>
              <span class="value tag">{{ authStore.userIdentity?.userTag || 'Not set' }}</span>
            </div>

            <div class="info-row">
              <span class="label">User ID:</span>
              <span class="value code">{{ authStore.userId || 'Not available' }}</span>
            </div>
          </div>

          <div class="profile-actions">
            <button class="btn-logout" @click="handleLogout">
              Logout
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.profile-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  padding: 2rem;
}

.profile-container {
  max-width: 600px;
  width: 100%;
}

.profile-card {
  background: var(--color-background-soft);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.profile-header {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  padding: 2rem;
  text-align: center;
}

.profile-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.profile-header h1 {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 600;
}

.profile-content {
  padding: 2rem;
}

.profile-section {
  margin-bottom: 2rem;
}

.profile-section h2 {
  font-size: 1.25rem;
  color: var(--color-heading);
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--color-border);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  margin-bottom: 0.5rem;
  background: var(--color-background);
  border-radius: 8px;
  border: 1px solid var(--color-border);
}

.label {
  font-weight: 600;
  color: var(--color-text);
}

.value {
  color: var(--color-text-secondary);
  text-align: right;
  max-width: 60%;
  word-break: break-all;
}

.value.tag {
  background: var(--color-primary);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.875rem;
  font-weight: 500;
}

.value.code {
  font-family: monospace;
  font-size: 0.875rem;
}

.profile-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);
}

.btn-logout {
  padding: 0.75rem 2rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-logout:hover {
  background: #dc2626;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.3);
}

@media (max-width: 768px) {
  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .value {
    max-width: 100%;
    text-align: left;
  }
}
</style>
