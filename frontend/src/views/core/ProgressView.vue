<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { Trophy, Gift, TrendingUp, Lock, Check, Sparkles } from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
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
    // Reload user identity to update emoji in navbar
    await authStore.fetchUserIdentity()
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
  <main class="p-6 min-h-screen space-y-8">
    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <TrendingUp class="w-8 h-8 text-[#A855F7]" />
        <h1 class="text-3xl font-bold text-foreground">Your Progress</h1>
      </div>
      <p class="text-sm text-[#A855F7]">Track your achievements, rewards, and level progression</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="text-muted-foreground">Loading your progress...</div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="flex items-center justify-center py-12">
      <Card class="max-w-md">
        <CardContent class="pt-6">
          <p class="text-destructive">{{ error }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Main Content -->
    <div v-else class="space-y-6">
      <!-- Profile Card -->
      <Card v-if="profile" class="bg-gradient-to-br from-card to-muted/30">
        <CardHeader>
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="flex items-center justify-center w-16 h-16 rounded-full bg-[#A855F7]/10 text-4xl">
                ⭐
              </div>
              <div>
                <CardTitle class="text-2xl flex items-center gap-2">
                  <span v-if="equippedRewards.length > 0 && equippedRewards[0]?.type === 'EMOJI'" class="text-3xl">
                    {{ equippedRewards[0]?.value }}
                  </span>
                  {{ profile.currentTitle }}
                </CardTitle>
                <CardDescription class="text-base mt-1">
                  {{ authStore.userIdentity?.userTag || 'User' }}
                </CardDescription>
              </div>
            </div>
            <div class="flex flex-col items-center px-6 py-3 bg-card rounded-lg border-2 border-[#A855F7]">
              <span class="text-3xl font-bold text-[#A855F7]">{{ profile.currentLevel }}</span>
              <span class="text-xs text-muted-foreground uppercase tracking-wide">Level</span>
            </div>
          </div>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex items-center justify-between">
            <span class="text-sm text-muted-foreground">Total Essence</span>
            <span class="text-2xl font-bold text-foreground">{{ profile.totalEssence }}</span>
          </div>
          
          <div class="space-y-2">
            <div class="h-3 bg-muted rounded-full overflow-hidden">
              <div 
                class="h-full bg-gradient-to-r from-[#A855F7] to-[#EC4899] rounded-full transition-all duration-500"
                :style="{ width: `${profile.progressToNextLevel}%` }"
              />
            </div>
            <div class="flex items-center justify-center">
              <span class="text-xs text-muted-foreground">
                {{ profile.essenceToNextLevel }} essence to level {{ profile.currentLevel + 1 }}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Achievements Section -->
      <div class="space-y-4">
        <div class="flex items-center gap-3">
          <Trophy class="w-6 h-6 text-[#F59E0B]" />
          <h2 class="text-2xl font-semibold text-foreground">Achievements</h2>
        </div>

        <!-- Unlocked Achievements -->
        <div v-if="unlockedAchievements.length > 0" class="space-y-3">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-medium text-foreground">Unlocked</h3>
            <Badge variant="secondary">{{ unlockedAchievements.length }}</Badge>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card
              v-for="achievement in unlockedAchievements"
              :key="achievement.id"
              class="hover:shadow-md transition-all"
            >
              <CardHeader class="pb-3">
                <div class="flex items-start gap-3">
                  <div class="flex items-center justify-center w-10 h-10 rounded-full bg-green-500/10">
                    <Check class="w-5 h-5 text-green-600" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <CardTitle class="text-base">{{ achievement.name }}</CardTitle>
                    <CardDescription class="mt-1">{{ achievement.description }}</CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <Badge class="bg-[#A855F7] hover:bg-[#A855F7]/80">
                  <Sparkles class="w-3 h-3 mr-1" />
                  +{{ achievement.essenceReward }} essence
                </Badge>
              </CardContent>
            </Card>
          </div>
        </div>

        <!-- Locked Achievements -->
        <div v-if="lockedAchievements.length > 0" class="space-y-3">
          <Separator />
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-medium text-muted-foreground">Locked</h3>
            <Badge variant="outline">{{ lockedAchievements.length }}</Badge>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card
              v-for="achievement in lockedAchievements"
              :key="achievement.id"
              class="opacity-60 hover:opacity-80 transition-opacity"
            >
              <CardHeader class="pb-3">
                <div class="flex items-start gap-3">
                  <div class="flex items-center justify-center w-10 h-10 rounded-full bg-muted">
                    <Lock class="w-5 h-5 text-muted-foreground" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <CardTitle class="text-base">{{ achievement.name }}</CardTitle>
                    <CardDescription class="mt-1">{{ achievement.description }}</CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <Badge variant="outline">
                  <Sparkles class="w-3 h-3 mr-1" />
                  +{{ achievement.essenceReward }} essence
                </Badge>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      <!-- Rewards Section -->
      <div v-if="ownedRewards.length > 0" class="space-y-4">
        <div class="flex items-center gap-3">
          <Gift class="w-6 h-6 text-[#10B981]" />
          <h2 class="text-2xl font-semibold text-foreground">Rewards</h2>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <Card
            v-for="reward in ownedRewards"
            :key="reward.id"
            class="hover:shadow-md transition-all"
            :class="{ 'ring-2 ring-[#A855F7]': reward.equipped }"
          >
            <CardHeader>
              <div class="flex items-center justify-between mb-2">
                <span class="text-4xl">{{ reward.type === 'EMOJI' ? reward.value : '🎨' }}</span>
                <Badge v-if="reward.equipped" class="bg-[#A855F7] hover:bg-[#A855F7]/80">
                  <Check class="w-3 h-3 mr-1" />
                  Equipped
                </Badge>
              </div>
              <CardTitle class="text-base">{{ reward.name }}</CardTitle>
              <CardDescription>{{ reward.description }}</CardDescription>
            </CardHeader>
            <CardContent v-if="!reward.equipped">
              <Button 
                @click="equipReward(reward.id)" 
                variant="outline" 
                size="sm"
                class="w-full"
              >
                Equip
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      <!-- Recent Activity -->
      <div v-if="transactions.length > 0" class="space-y-4">
        <div class="flex items-center gap-3">
          <TrendingUp class="w-6 h-6 text-[#3B82F6]" />
          <h2 class="text-2xl font-semibold text-foreground">Recent Activity</h2>
        </div>

        <Card>
          <CardContent class="p-0">
            <div
              v-for="(transaction, index) in transactions.slice(0, 10)"
              :key="transaction.id"
              class="flex items-center justify-between p-4 hover:bg-accent/50 transition-colors"
              :class="{ 'border-b border-border': index < Math.min(transactions.length, 10) - 1 }"
            >
              <div class="flex flex-col gap-1">
                <span class="font-medium text-foreground">{{ getSourceLabel(transaction.source) }}</span>
                <span class="text-sm text-muted-foreground">{{ formatDate(transaction.timestamp) }}</span>
              </div>
              <Badge class="bg-green-500/10 text-green-600 hover:bg-green-500/20 border-green-500/20">
                +{{ transaction.amount }}
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
