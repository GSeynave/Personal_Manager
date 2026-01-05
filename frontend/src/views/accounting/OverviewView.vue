<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

import Breadcrumb from '@/components/core/Breadcrumb.vue'
import WalletSelector from '@/components/accounting/WalletSelector.vue'
import WalletSummary from '@/components/accounting/WalletSummary.vue'
import CategoryTransactionGroup from '@/components/accounting/CategoryTransactionGroup.vue'

import AccountingService from '@/services/accounting/AccountingService'
import WalletService from '@/services/accounting/WalletService'

import AccountingSummary from '@/model/accounting/AccountingSummary'
import type { Wallet } from '@/model/accounting/Wallet'
import type Transaction from '@/model/accounting/Transaction'
import type UncategorizedTransactions from '@/model/accounting/UncategorizedTransactions'

import { Wallet as WalletIcon, Plus, Upload, Tag, FileText, AlertCircle } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { useAccountingStore } from '@/stores/accounting'

const accountingService = new AccountingService()
const walletService = new WalletService()
const accountingStore = useAccountingStore()
const router = useRouter()

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

interface CategoryGroup {
  categoryName: string
  categoryIcon: string | null
  totalAmount: number
  transactions: Transaction[]
}

const groupedTransactions = ref<CategoryGroup[]>([])

function onWalletSelected(walletId: number) {
  selectedWalletId.value = walletId
  fetchOverviewData()
}

function onWalletChanged(wallet: Wallet | null) {
  selectedWallet.value = wallet
}

function onPeriodChange(period: string) {
  // Handle navigation
  if (period === 'navigate-prev') {
    navigatePeriod(-1)
    return
  }
  if (period === 'navigate-next') {
    navigatePeriod(1)
    return
  }
  
  selectedPeriod.value = period
  
  // Update date params based on period
  if (period === 'this-month') {
    dateParam.value = getDefaultDateParam()
  } else if (period === 'last-month') {
    const today = new Date()
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1)
    const year = lastMonth.getFullYear()
    const month = String(lastMonth.getMonth() + 1).padStart(2, '0')
    const lastDay = new Date(year, parseInt(month) + 1, 0).getDate()
    
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
  } else if (period === 'custom') {
    // TODO: Open custom date range picker dialog
    console.log('Custom date range picker not yet implemented')
    return
  }
  
  fetchOverviewData()
}

function navigatePeriod(direction: number) {
  // Parse current date range
  const currentDate = new Date(dateParam.value.minDate)
  
  let newMinDate: Date
  let newMaxDate: Date
  
  if (selectedPeriod.value === 'this-year') {
    // Navigate by year
    const newYear = currentDate.getFullYear() + direction
    newMinDate = new Date(newYear, 0, 1)
    newMaxDate = new Date(newYear, 11, 31)
  } else {
    // Navigate by month
    newMinDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + direction, 1)
    newMaxDate = new Date(newMinDate.getFullYear(), newMinDate.getMonth() + 1, 0)
  }
  
  const formatDateStr = (date: Date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
  
  dateParam.value = {
    minDate: formatDateStr(newMinDate),
    maxDate: formatDateStr(newMaxDate),
  }
  
  // Update period label based on current date
  const today = new Date()
  
  if (selectedPeriod.value === 'this-year') {
    const isThisYear = newMinDate.getFullYear() === today.getFullYear()
    selectedPeriod.value = isThisYear ? 'this-year' : 'custom'
  } else {
    const isThisMonth = newMinDate.getFullYear() === today.getFullYear() && 
                       newMinDate.getMonth() === today.getMonth()
    const isLastMonth = newMinDate.getFullYear() === today.getFullYear() && 
                        newMinDate.getMonth() === today.getMonth() - 1
    
    if (isThisMonth) {
      selectedPeriod.value = 'this-month'
    } else if (isLastMonth) {
      selectedPeriod.value = 'last-month'
    } else {
      selectedPeriod.value = 'custom'
    }
  }
  
  fetchOverviewData()
}

const fetchOverviewData = async () => {
  if (!selectedWalletId.value) return
  
  try {
    await Promise.all([
      fetchSummary(),
      fetchRecentTransactions(),
      accountingStore.fetchUncategorizedCount(selectedWalletId.value || undefined)
    ])
  } catch (err) {
    console.error('Error fetching overview data:', err)
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
  console.log('Fetching recent transactions with params:', {
    minDate: dateParam.value.minDate,
    maxDate: dateParam.value.maxDate,
    walletId: selectedWalletId.value
  })
  
  const data = await accountingService.getTransactionsSummary(
    getMinMaxDateParam(),
    selectedWalletId.value || undefined
  )
  
  console.log('Transactions summary data received:', data)
  console.log('Is array?', Array.isArray(data))
  console.log('Type of data:', typeof data)
  
  // The API might return a single object or an array of TransactionSummary
  let allTransactions: any[] = []
  
  if (Array.isArray(data)) {
    // Array of TransactionSummary objects
    allTransactions = data.flatMap(summary => summary.transactions || [])
  } else if (data && typeof data === 'object') {
    // Could be a single TransactionSummary or a wrapper object
    if ('transactions' in data) {
      allTransactions = data.transactions || []
    } else if ('data' in data) {
      // Might be wrapped
      const wrappedData = (data as any).data
      allTransactions = Array.isArray(wrappedData) 
        ? wrappedData.flatMap((s: any) => s.transactions || [])
        : (wrappedData.transactions || [])
    }
  }
  
  console.log('All transactions extracted:', allTransactions)
  
  // Get only the most recent transactions
  recentTransactions.value = allTransactions
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 20) // Get more transactions to have enough for grouping
  
  console.log('Recent transactions after processing:', recentTransactions.value)
  
  // Group transactions by parent category
  groupTransactionsByCategory()
}

function groupTransactionsByCategory() {
  const categoryMap = new Map<string, CategoryGroup>()
  
  recentTransactions.value.forEach(transaction => {
    // Use parent category title if available, otherwise use the category title
    const categoryName = transaction.category?.parentCategoryTitle || transaction.category?.title || 'Uncategorized'
    const categoryIcon = transaction.category?.icon || null
    
    if (!categoryMap.has(categoryName)) {
      categoryMap.set(categoryName, {
        categoryName,
        categoryIcon,
        totalAmount: 0,
        transactions: []
      })
    }
    
    const group = categoryMap.get(categoryName)!
    group.totalAmount += transaction.amount
    group.transactions.push(transaction)
  })
  
  // Convert map to array and sort by absolute amount (highest first)
  groupedTransactions.value = Array.from(categoryMap.values())
    .sort((a, b) => Math.abs(b.totalAmount) - Math.abs(a.totalAmount))
    .slice(0, 10) // Limit to top 10 categories
  
  console.log('Grouped transactions:', groupedTransactions.value)
}

function getMinMaxDateParam() {
  const params = new URLSearchParams()
  params.append('minDate', dateParam.value!.minDate)
  params.append('maxDate', dateParam.value!.maxDate)
  return params
}

async function loadWallets() {
  try {
    wallets.value = await walletService.getWalletsForCurrentUser()
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
    <!-- Breadcrumb -->
    <Breadcrumb :items="[
      { label: 'Finance & Resources', to: '/finance/overview' },
      { label: 'Overview' }
    ]" />

    <!-- Page Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-foreground">Overview</h1>
        <p class="text-sm text-accounting mt-1">
          Financial snapshot and quick access
        </p>
      </div>
    </div>

    <!-- Two-column layout: Wallets + Content -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- Left: Wallet Selector -->
      <div class="lg:col-span-1">
        <div class="sticky top-6 space-y-4">
          <WalletSelector 
            @wallet-selected="onWalletSelected" 
            @wallet-changed="onWalletChanged"
          />
        </div>
      </div>

      <!-- Right: Overview Content -->
      <div class="lg:col-span-3 space-y-6">
        <!-- Uncategorized Transactions Alert -->
        <div 
          v-if="accountingStore.uncategorizedCount > 0 && selectedWalletId"
          class="card p-4 border-l-4 border-l-orange-500 bg-orange-50 dark:bg-orange-950/20"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <AlertCircle class="w-5 h-5 text-orange-600 dark:text-orange-500" />
              <div>
                <h3 class="font-semibold text-orange-900 dark:text-orange-100">
                  {{ accountingStore.uncategorizedCount }} transaction{{ accountingStore.uncategorizedCount > 1 ? 's' : '' }} to categorize
                </h3>
                <p class="text-sm text-orange-700 dark:text-orange-300">
                  Categorize your transactions to get better insights
                </p>
              </div>
            </div>
            <Button 
              variant="outline" 
              size="sm"
              class="border-orange-600 text-orange-600 hover:bg-orange-100 dark:hover:bg-orange-900/30"
              @click="router.push('/finance/categories')"
            >
              <Tag class="w-4 h-4 mr-2" />
              Categorize Now
            </Button>
          </div>
        </div>

        <!-- Period & Summary -->
        <WalletSummary
          :accounting-summary="accountingSummary"
          :wallet-name="selectedWallet?.name"
          :period="selectedPeriod"
          :date-range="dateParam"
          @period-change="onPeriodChange"
        />

        <!-- Recent Transactions -->
        <div class="card p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-xl font-semibold">Recent Transactions</h2>
            <Button 
              variant="ghost" 
              size="sm"
              @click="router.push('/finance/transactions')"
            >
              View all
            </Button>
          </div>
          
          <div v-if="groupedTransactions.length === 0" class="text-center py-8 text-muted-foreground">
            No transactions for this period
          </div>
          <div v-else class="divide-y divide-border">
            <CategoryTransactionGroup
              v-for="(group, index) in groupedTransactions"
              :key="group.categoryName"
              :category-group="group"
            />
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="card p-6">
          <h2 class="text-xl font-semibold mb-4">Quick Actions</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <Button 
              variant="outline" 
              class="h-auto py-4 flex-col gap-2"
              @click="router.push('/finance/transactions')"
            >
              <Plus class="w-5 h-5" />
              <span>Add Transaction</span>
            </Button>
            <Button 
              variant="outline" 
              class="h-auto py-4 flex-col gap-2"
              @click="router.push('/finance/import')"
            >
              <Upload class="w-5 h-5" />
              <span>Import</span>
            </Button>
            <Button 
              variant="outline" 
              class="h-auto py-4 flex-col gap-2"
              @click="router.push('/finance/categories')"
            >
              <Tag class="w-5 h-5" />
              <span>Categorize</span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
