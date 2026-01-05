<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

import WalletSelector from '@/components/accounting/WalletSelector.vue'
import WalletSummary from '@/components/accounting/WalletSummary.vue'
import TransactionChart from '@/components/accounting/TransactionChart.vue'

import AccountingService from '@/services/accounting/AccountingService'
import WalletService from '@/services/accounting/WalletService'

import AccountingSummary from '@/model/accounting/AccountingSummary'
import type { Wallet } from '@/model/accounting/Wallet'

import { LayoutDashboard } from 'lucide-vue-next'

const accountingService = new AccountingService()
const walletService = new WalletService()

interface DateParam {
  minDate: string
  maxDate: string
}

function getDefaultDateParam(): DateParam {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const lastDay = new Date(year, parseInt(month), 0).getDate()

  return {
    minDate: `${year}-${month}-01`,
    maxDate: `${year}-${month}-${lastDay}`,
  }
}

const dateParam = ref<DateParam>(getDefaultDateParam())
const selectedWalletId = ref<number | null>(null)
const selectedWallet = ref<Wallet | null>(null)
const selectedPeriod = ref('this-month')
const wallets = ref<Wallet[]>([])
const accountingSummary = ref<AccountingSummary>()
const recentTransactions = ref<any[]>([])

function onWalletSelected(walletId: number) {
  selectedWalletId.value = walletId
  fetchDashboardData()
}

function onWalletChanged(wallet: Wallet) {
  selectedWallet.value = wallet
}

function onPeriodChange(period: string) {
  selectedPeriod.value = period
  
  // Update date params based on period
  if (period === 'this-month') {
    dateParam.value = getDefaultDateParam()
  } else if (period === 'last-month') {
    const today = new Date()
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1)
    const year = lastMonth.getFullYear()
    const month = String(lastMonth.getMonth() + 1).padStart(2, '0')
    const lastDay = new Date(year, parseInt(month), 0).getDate()
    
    dateParam.value = {
      minDate: `${year}-${month}-01`,
      maxDate: `${year}-${month}-${lastDay}`,
    }
  } else if (period === 'this-year') {
    const today = new Date()
    const year = today.getFullYear()
    
    dateParam.value = {
      minDate: `${year}-01-01`,
      maxDate: `${year}-12-31`,
    }
  }
  
  fetchDashboardData()
}

const fetchDashboardData = async () => {
  if (!selectedWalletId.value) return
  
  try {
    await fetchSummary()
    await fetchRecentTransactions()
  } catch (err) {
    console.error('Error fetching dashboard data:', err)
  }
}

async function fetchSummary() {
  const data = await accountingService.getAccountingSummary(
    getMinMaxDateParam(), 
    selectedWalletId.value || undefined
  )
  accountingSummary.value = data
}

async function fetchRecentTransactions() {
  const data = await accountingService.getTransactionsSummary(
    getMinMaxDateParam(),
    selectedWalletId.value || undefined
  )
  
  // Get only the 5 most recent transactions
  if (data && Array.isArray(data)) {
    recentTransactions.value = data
      .flatMap(summary => summary.transactions || [])
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .slice(0, 5)
  }
}

function getMinMaxDateParam() {
  const params = new URLSearchParams()
  params.append('minDate', dateParam.value!.minDate)
  params.append('maxDate', dateParam.value!.maxDate)
  return params
}

async function loadWallets() {
  try {
    wallets.value = await walletService.getUserWallets()
  } catch (err) {
    console.error('Error loading wallets:', err)
  }
}

onMounted(async () => {
  await loadWallets()
})
</script>

<template>
  <main :style="{
    '--module-color': moduleColor('accounting'),
    '--module-color-rgb': hexToRgbStr(moduleColor('accounting')),
    '--module-tint-alpha': moduleTintAlpha(moduleColor('accounting')),
  }" class="p-6 min-h-screen space-y-6">
    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <LayoutDashboard class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Dashboard</h1>
      </div>
      <p class="text-sm text-accounting">
        Overview of your wallets and recent transactions
      </p>
    </div>

    <!-- Two-column layout: Wallet Selector + Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- Left Sidebar: Wallet Selector -->
      <div class="lg:col-span-1">
        <div class="sticky top-6">
          <WalletSelector 
            @wallet-selected="onWalletSelected" 
            @wallet-changed="onWalletChanged"
          />
        </div>
      </div>

      <!-- Right Content Area -->
      <div class="lg:col-span-3 space-y-4">
        <!-- Wallet Summary Section -->
        <WalletSummary
          :accounting-summary="accountingSummary"
          :wallet-name="selectedWallet?.name"
          :period="selectedPeriod"
          @period-change="onPeriodChange"
        />

        <!-- Transaction Chart -->
        <TransactionChart :transactions="recentTransactions" />

        <!-- Recent Transactions -->
        <div class="card p-4">
          <h2 class="text-xl font-semibold mb-4">Recent Transactions</h2>
          <div v-if="recentTransactions.length === 0" class="text-center py-8 text-muted-foreground">
            No transactions found for this period
          </div>
          <div v-else class="space-y-2">
            <div 
              v-for="transaction in recentTransactions" 
              :key="transaction.id"
              class="flex items-center justify-between p-3 rounded-lg border hover:bg-muted/50 transition-colors"
            >
              <div class="flex-1">
                <div class="font-medium">{{ transaction.label }}</div>
                <div class="text-sm text-muted-foreground">{{ new Date(transaction.date).toLocaleDateString() }}</div>
              </div>
              <div class="text-right">
                <div 
                  class="font-semibold"
                  :class="transaction.amount >= 0 ? 'text-green-600' : 'text-red-600'"
                >
                  {{ transaction.amount >= 0 ? '+' : '' }}{{ transaction.amount.toFixed(2) }} €
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-header {
  padding-left: 12px;
}
</style>
