<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const isSignupMode = ref(false)
const errorMessage = ref('')

async function handleSubmit() {
  errorMessage.value = ''
  
  if (!email.value || !password.value) {
    errorMessage.value = 'Please fill in all fields'
    return
  }
  
  const result = isSignupMode.value
    ? await authStore.signup(email.value, password.value)
    : await authStore.login(email.value, password.value)
  
  if (result.success) {
    // Redirect to originally requested page or home
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || '/')
  } else {
    errorMessage.value = result.error || 'Authentication failed'
  }
}

function toggleMode() {
  isSignupMode.value = !isSignupMode.value
  errorMessage.value = ''
}
</script>

<template>
  <main class="login-container">
    <div class="login-card">
      <h1>{{ isSignupMode ? 'Sign Up' : 'Login' }}</h1>
      
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="Enter your email"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            required
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <button type="submit" class="btn-primary" :disabled="authStore.isLoading">
          {{ authStore.isLoading ? 'Loading...' : (isSignupMode ? 'Sign Up' : 'Login') }}
        </button>
      </form>
      
      <div class="toggle-mode">
        <p>
          {{ isSignupMode ? 'Already have an account?' : "Don't have an account?" }}
          <a @click="toggleMode">
            {{ isSignupMode ? 'Login' : 'Sign Up' }}
          </a>
        </p>
      </div>
    </div>
  </main>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
}

.login-card {
  background: var(--color-background-soft);
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--color-heading);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.error-message {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  font-size: 0.875rem;
}

.btn-primary {
  width: 100%;
  padding: 0.75rem;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.toggle-mode {
  margin-top: 1.5rem;
  text-align: center;
}

.toggle-mode p {
  color: var(--color-text);
  font-size: 0.875rem;
}

.toggle-mode a {
  color: var(--color-primary);
  cursor: pointer;
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.25rem;
}

.toggle-mode a:hover {
  text-decoration: underline;
}
</style>
