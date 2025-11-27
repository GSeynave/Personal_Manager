<script setup lang="ts">
import HomeDashboard from '../components/Dashboard/HomeDashboard.vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const features = [
  {
    icon: '✅',
    title: 'Todo Management',
    description: 'Organize your tasks efficiently with our intuitive todo system'
  },
  {
    icon: '💰',
    title: 'Accounting',
    description: 'Track your finances and manage your budget with ease'
  },
  {
    icon: '🍎',
    title: 'Diet Tracking',
    description: 'Monitor your nutrition and maintain healthy eating habits'
  },
  {
    icon: '⚡',
    title: 'Energy Monitoring',
    description: 'Track your energy levels throughout the day'
  },
  {
    icon: '📅',
    title: 'Calendar',
    description: 'Stay organized with an integrated calendar system'
  },
  {
    icon: '😴',
    title: 'Sleep Tracking',
    description: 'Monitor your sleep patterns for better rest'
  }
]

function handleGetStarted() {
  router.push('/login')
}
</script>

<template>
  <main>
    <!-- Authenticated View -->
    <div v-if="authStore.isAuthenticated">
      <HomeDashboard />
    </div>

    <!-- Non-Authenticated View -->
    <div v-else class="landing-page">
      <section class="hero">
        <h1 class="hero-title">Welcome to Personal Manager</h1>
        <p class="hero-subtitle">Your all-in-one solution to manage your life efficiently</p>
        <button class="btn-get-started" @click="handleGetStarted">
          Get Started
        </button>
      </section>

      <section class="features">
        <h2 class="section-title">What You Can Do</h2>
        <div class="features-grid">
          <div v-for="feature in features" :key="feature.title" class="feature-card">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </section>

      <section class="cta">
        <h2>Ready to get organized?</h2>
        <p>Sign up now and start managing your life better</p>
        <button class="btn-cta" @click="handleGetStarted">
          Sign Up Now
        </button>
      </section>
    </div>
  </main>
</template>

<style scoped>
main {
  min-height: calc(100vh - 70px);
}

.landing-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

/* Hero Section */
.hero {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, rgba(var(--color-primary-rgb, 66, 153, 225), 0.1) 0%, rgba(var(--color-primary-dark-rgb, 49, 130, 206), 0.1) 100%);
  border-radius: 16px;
  margin-bottom: 4rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 1rem;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-get-started {
  padding: 1rem 3rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(var(--color-primary-rgb, 66, 153, 225), 0.3);
}

.btn-get-started:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(var(--color-primary-rgb, 66, 153, 225), 0.4);
}

/* Features Section */
.features {
  margin-bottom: 4rem;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 3rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  padding: 2rem;
  background: var(--color-background-soft);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  text-align: center;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--color-primary);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.feature-description {
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* CTA Section */
.cta {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border-radius: 16px;
}

.cta h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.cta p {
  font-size: 1.125rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.btn-cta {
  padding: 1rem 3rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-primary);
  background: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .cta h2 {
    font-size: 1.5rem;
  }
}
</style>
