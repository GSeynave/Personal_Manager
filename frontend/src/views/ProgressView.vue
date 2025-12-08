<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const loading = ref(true)
const error = ref<string | null>(null)

interface GameProfile {
  id: number
  userId: number
  totalEssence: number
  currentLevel: number
  currentTitle: string
  lastEssenceEarned: string | null
  essenceToNextLevel: number
  progressToNextLevel: number
}

interface Achievement {
  id: string
  name: string
  description: string
  type: string
  essenceReward: number
  rewardIds: string[]
  unlocked: boolean
}

interface Reward {
  id: string
  name: string
  description: string
  type: string
  value: string
  owned: boolean
  equipped: boolean
}

interface Transaction {
  id: number
  amount: number
  source: string
  sourceId: number
  timestamp: string
}

const profile = ref<GameProfile | null>(null)
const achievements = ref<Achievement[]>([])
const rewards = ref<Reward[]>([])
const transactions = ref<Transaction[]>([])

const loadGameProfile = async () => {
  try {
    const [profileRes, achievementsRes, rewardsRes, transactionsRes] = await Promise.all([
      axios.get('/api/gamification/profile'),
      axios.get('/api/gamification/achievements'),
      axios.get('/api/gamification/rewards'),
      axios.get('/api/gamification/transactions')
    ])
    
    profile.value = profileRes.data
    achievements.value = achievementsRes.data
    rewards.value = rewardsRes.data
    transactions.value = transactionsRes.data
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to load gamification data'
    console.error('Error loading gamification data:', err)
  } finally {
    loading.value = false
  }
}

const equipReward = async (rewardId: string) => {
  try {
    await axios.post(`/api/gamification/rewards/${rewardId}/equip`)
    // Reload rewards to update equipped status
    const res = await axios.get('/api/gamification/rewards')
    rewards.value = res.data
  } catch (err: any) {
    console.error('Error equipping reward:', err)
  }
}

const unlockedAchievements = computed(() => achievements.value.filter(a => a.unlocked))
const lockedAchievements = computed(() => achievements.value.filter(a => !a.unlocked))
const ownedRewards = computed(() => rewards.value.filter(r => r.owned))
const equippedRewards = computed(() => rewards.value.filter(r => r.equipped))

onMounted(() => {
  loadGameProfile()
})

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getSourceLabel = (source: string) => {
  const labels: Record<string, string> = {
    'task_completed': 'Task Completed',
    'habit_completed': 'Habit Completed',
    'streak_7': '7-Day Streak',
    'streak_30': '30-Day Streak',
    'achievement': 'Achievement Bonus'
  }
  return labels[source] || source
}
</script>

<template>
  <div class="progress-view">
    <div class="container">
      <h1 class="page-title">Your Progress</h1>
      
      <div v-if="loading" class="loading-state">
        <p>Loading your progress...</p>
      </div>
      
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
      </div>
      
      <div v-else class="progress-content">
        <!-- Profile Card -->
        <div v-if="profile" class="profile-card card">
          <div class="profile-header">
            <div class="profile-icon">⭐</div>
            <div class="profile-info">
              <h2>{{ profile.currentTitle }}</h2>
              <p class="user-name">{{ authStore.userIdentity?.userTag || 'User' }}</p>
            </div>
            <div class="level-badge">
              <span class="level-number">{{ profile.currentLevel }}</span>
              <span class="level-label">Level</span>
            </div>
          </div>
          
          <div class="essence-section">
            <div class="essence-info">
              <span class="essence-label">Total Essence</span>
              <span class="essence-value">{{ profile.totalEssence }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${profile.progressToNextLevel}%` }"></div>
            </div>
            <div class="next-level-info">
              <span>{{ profile.essenceToNextLevel }} to next level</span>
            </div>
          </div>
        </div>

        <!-- Achievements Section -->
        <div class="section">
          <h2 class="section-title">🏆 Achievements</h2>
          
          <div v-if="unlockedAchievements.length > 0" class="achievements-group">
            <h3 class="subsection-title">Unlocked ({{ unlockedAchievements.length }})</h3>
            <div class="achievements-grid">
              <div v-for="achievement in unlockedAchievements" :key="achievement.id" class="achievement-card unlocked">
                <div class="achievement-icon">✓</div>
                <div class="achievement-info">
                  <h4>{{ achievement.name }}</h4>
                  <p>{{ achievement.description }}</p>
                  <span class="essence-reward">+{{ achievement.essenceReward }} essence</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="lockedAchievements.length > 0" class="achievements-group">
            <h3 class="subsection-title">Locked ({{ lockedAchievements.length }})</h3>
            <div class="achievements-grid">
              <div v-for="achievement in lockedAchievements" :key="achievement.id" class="achievement-card locked">
                <div class="achievement-icon">🔒</div>
                <div class="achievement-info">
                  <h4>{{ achievement.name }}</h4>
                  <p>{{ achievement.description }}</p>
                  <span class="essence-reward">+{{ achievement.essenceReward }} essence</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Rewards Section -->
        <div v-if="ownedRewards.length > 0" class="section">
          <h2 class="section-title">🎁 Rewards</h2>
          <div class="rewards-grid">
            <div v-for="reward in ownedRewards" :key="reward.id" 
                 class="reward-card" 
                 :class="{ equipped: reward.equipped }">
              <div class="reward-header">
                <span class="reward-icon">{{ reward.type === 'EMOJI' ? reward.value : '🎨' }}</span>
                <span v-if="reward.equipped" class="equipped-badge">Equipped</span>
              </div>
              <h4>{{ reward.name }}</h4>
              <p>{{ reward.description }}</p>
              <button v-if="!reward.equipped" @click="equipReward(reward.id)" class="btn-equip">
                Equip
              </button>
            </div>
          </div>
        </div>

        <!-- Recent Activity -->
        <div v-if="transactions.length > 0" class="section">
          <h2 class="section-title">📊 Recent Activity</h2>
          <div class="transactions-list">
            <div v-for="transaction in transactions.slice(0, 10)" :key="transaction.id" class="transaction-item">
              <div class="transaction-info">
                <span class="transaction-source">{{ getSourceLabel(transaction.source) }}</span>
                <span class="transaction-date">{{ formatDate(transaction.timestamp) }}</span>
              </div>
              <span class="transaction-amount">+{{ transaction.amount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.progress-view {
  padding: 2rem;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 2rem;
  color: var(--color-heading);
}

.loading-state,
.error-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-text);
}

.progress-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.card {
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.profile-card {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  border: none;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.profile-icon {
  font-size: 3rem;
}

.profile-info {
  flex: 1;
}

.profile-info h2 {
  font-size: 1.5rem;
  margin: 0;
}

.user-name {
  opacity: 0.9;
  margin: 0.25rem 0 0;
}

.level-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255, 255, 255, 0.2);
  padding: 0.75rem 1.25rem;
  border-radius: 8px;
}

.level-number {
  font-size: 2rem;
  font-weight: 700;
}

.level-label {
  font-size: 0.875rem;
  opacity: 0.9;
}

.essence-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.essence-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.essence-label {
  font-size: 0.875rem;
  opacity: 0.9;
}

.essence-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.progress-bar {
  height: 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 6px;
  transition: width 0.3s ease;
}

.next-level-info {
  text-align: center;
  font-size: 0.875rem;
  opacity: 0.9;
}

.section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-heading);
}

.subsection-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-muted);
  margin-top: 1rem;
}

.achievements-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.achievement-card {
  background: var(--color-background);
  border: 2px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  gap: 1rem;
  transition: all 0.2s ease;
}

.achievement-card.unlocked {
  border-color: var(--color-success);
  background: var(--color-success-background);
}

.achievement-card.locked {
  opacity: 0.6;
}

.achievement-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.achievement-info h4 {
  margin: 0 0 0.25rem;
  font-size: 1rem;
  color: var(--color-heading);
}

.achievement-info p {
  margin: 0 0 0.5rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.essence-reward {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  background: var(--color-accent);
  color: white;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.rewards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.reward-card {
  background: var(--color-background);
  border: 2px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  transition: all 0.2s ease;
}

.reward-card.equipped {
  border-color: var(--color-primary);
  background: var(--color-primary-background);
}

.reward-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reward-icon {
  font-size: 2rem;
}

.equipped-badge {
  padding: 0.25rem 0.5rem;
  background: var(--color-primary);
  color: white;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.reward-card h4 {
  margin: 0;
  font-size: 1rem;
  color: var(--color-heading);
}

.reward-card p {
  margin: 0;
  font-size: 0.875rem;
  color: var(--color-text-muted);
  flex: 1;
}

.btn-equip {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-equip:hover {
  background: var(--color-primary-dark);
}

.transactions-list {
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.transaction-source {
  font-weight: 600;
  color: var(--color-heading);
}

.transaction-date {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.transaction-amount {
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--color-success);
}
</style>
