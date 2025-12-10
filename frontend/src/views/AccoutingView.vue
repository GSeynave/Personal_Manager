<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

import AccountinngHeader from '@/components/accouting/AccountingHeader.vue'
import AccountingList from '@/components/accouting/AccountingList.vue'
import AccountingDateForm from '@/components/accouting/AccountingDateForm.vue'

import AccountingService from '@/services/AccountingService'

import AccountingSummary from '@/model/AccountingSummary'
import TransactionSummary from '@/model/TransactionSummary'
import AccountingImport from '@/components/accouting/AccountingImport.vue'
import AccountingCategorize from '@/components/accouting/AccountingCategorize.vue'

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

function onDateParamUpdate(event: DateParam) {
  dateParam.value = { minDate: event.minDate, maxDate: event.maxDate }
  fetchAccounting()
}

const accountingSummary = ref<AccountingSummary>()
const transactionSummary = ref<TransactionSummary>()

const fetchAccounting = async () => {
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
  const data = await accountingService.getAccountingSummary(getMinMaxDateParam())
  accountingSummary.value = data
}

async function fetchTransactions() {
  const data = await accountingService.getTransactionsSummary(getMinMaxDateParam())
  transactionSummary.value = data
}

function getMinMaxDateParam() {
  const params = new URLSearchParams()
  params.append('minDate', dateParam.value!.minDate)
  params.append('maxDate', dateParam.value!.maxDate)
  return params
}

onMounted(() => {
  // Fetch with default dates immediately
  fetchAccounting()
})
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('accounting'),
      '--module-color-rgb': hexToRgbStr(moduleColor('accounting')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('accounting')),
    }"
    class="p-6 min-h-screen space-y-8"
  >
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Wallet class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Finance & Resources</h1>
      </div>
      <p class="text-sm text-accounting">Manage your transactions and budget</p>
    </div>

    <div class="space-y-6 p-6">
      <Tabs default-value="report" class="w-full">
        <TabsList class="grid w-full grid-cols-3">
          <TabsTrigger value="report">Report</TabsTrigger>
          <TabsTrigger value="import">Import</TabsTrigger>
          <TabsTrigger value="categorize">Categorize</TabsTrigger>
        </TabsList>
        <TabsContent value="report" class="space-y-4">
          <AccountingDateForm @onDateParamUpdate="onDateParamUpdate($event)" />
          <AccountinngHeader :accounting-summary="accountingSummary" />
          <AccountingList :transaction-summary="transactionSummary" />
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
