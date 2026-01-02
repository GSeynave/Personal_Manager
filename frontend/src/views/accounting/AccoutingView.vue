<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

import AccountingDateForm from '@/components/accounting/AccountingDateForm.vue'
import WalletSelector from '@/components/accounting/WalletSelector.vue'
import WalletSummary from '@/components/accounting/WalletSummary.vue'
import AccountingList from '@/components/accounting/AccountingList.vue'
import TransactionChart from '@/components/accounting/TransactionChart.vue'

import AccountingService from '@/services/accounting/AccountingService'

import AccountingSummary from '@/model/accounting/AccountingSummary'
import TransactionSummary from '@/model/accounting/TransactionSummary'
import type Transaction from '@/model/accounting/Transaction'
import AccountingImport from '@/components/accounting/AccountingImport.vue'
import AccountingCategorize from '@/components/accounting/AccountingCategorize.vue'

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Wallet } from 'lucide-vue-next'

const accountingService = new AccountingService()

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
const selectedWallet = ref<any>(null)
const selectedPeriod = ref('this-month')

function onDateParamUpdate(event: DateParam) {
  dateParam.value = { minDate: event.minDate, maxDate: event.maxDate }
  fetchAccounting()
}

function onWalletSelected(walletId: number) {
  selectedWalletId.value = walletId
  fetchAccounting()
}

function onWalletChanged(wallet: any) {
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
  // 'custom' period will be handled by the date form
  
  if (period !== 'custom') {
    fetchAccounting()
  }
}

const accountingSummary = ref<AccountingSummary>()
const transactionSummary = ref<TransactionSummary>()

// Compute all transactions from the summary for the chart
const allTransactions = computed<Transaction[]>(() => {
  if (!transactionSummary.value || !Array.isArray(transactionSummary.value)) {
    return []
  }
  
  return transactionSummary.value.flatMap(summary => summary.transactions || [])
})

const fetchAccounting = async () => {
  if (!selectedWalletId.value) return
  
  try {
    await fetchSummary()
    await fetchTransactions()
  } catch (err) {
    console.error('Error fetching accounting summary:', err)
  } finally {
    console.info('Accounting summary fetch ended')
  }
}

async function fetchSummary() {
  const data = await accountingService.getAccountingSummary(
    getMinMaxDateParam(), 
    selectedWalletId.value || undefined
  )
  accountingSummary.value = data
}

async function fetchTransactions() {
  const data = await accountingService.getTransactionsSummary(
    getMinMaxDateParam(),
    selectedWalletId.value || undefined
  )
  transactionSummary.value = data
}

function getMinMaxDateParam() {
  const params = new URLSearchParams()
  params.append('minDate', dateParam.value!.minDate)
  params.append('maxDate', dateParam.value!.maxDate)
  return params
}

onMounted(() => {
  // Wallet selector will auto-load and select first wallet
  // which will trigger fetchAccounting via onWalletSelected
})
</script>

<template>
  <main :style="{
    '--module-color': moduleColor('accounting'),
    '--module-color-rgb': hexToRgbStr(moduleColor('accounting')),
    '--module-tint-alpha': moduleTintAlpha(moduleColor('accounting')),
  }" class="p-6 min-h-screen space-y-8">
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Wallet class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Finance & Resources</h1>
      </div>
      <p class="text-sm text-accounting">Manage your transactions and budget</p>
    </div>

    <div class="space-y-6">
      <Tabs default-value="report" class="w-full">
        <TabsList class="grid w-full grid-cols-3">
          <TabsTrigger value="report">Report</TabsTrigger>
          <TabsTrigger value="import">Import</TabsTrigger>
          <TabsTrigger value="categorize">Categorize</TabsTrigger>
        </TabsList>

        <TabsContent value="report">
          <!-- Two-column layout: Wallet Selector + Main Content -->
          <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 mt-6">
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

              <!-- Custom Date Range (shown when period is 'custom') -->
              <AccountingDateForm 
                v-if="selectedPeriod === 'custom'"
                @onDateParamUpdate="onDateParamUpdate($event)" 
              />

              <!-- Transaction Chart -->
              <TransactionChart :transactions="allTransactions" />

              <!-- Transactions List -->
              <AccountingList :transaction-summary="transactionSummary" />
            </div>
          </div>
        </TabsContent>

        <TabsContent value="import">
          <AccountingImport />
        </TabsContent>

        <TabsContent value="categorize">
          <AccountingCategorize />
        </TabsContent>
      </Tabs>
    </div>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
