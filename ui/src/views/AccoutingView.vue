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

import { Tabs, TabList, TabPanel, TabPanels, Tab } from 'primevue'
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
  >
    <Tabs value="0">
      <TabList>
        <Tab value="0">Report</Tab>
        <Tab value="1">Import</Tab>
        <Tab value="2">Categorize</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <AccountingDateForm @onDateParamUpdate="onDateParamUpdate($event)" />
          <AccountinngHeader :accounting-summary="accountingSummary" />
          <AccountingList :transaction-summary="transactionSummary" />
        </TabPanel>
        <TabPanel value="1">
          <AccountingImport />
        </TabPanel>
        <TabPanel value="2">
          <AccountingCategorize />
        </TabPanel>
      </TabPanels>
    </Tabs>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  position: relative;
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 46, 125, 50), var(--module-tint-alpha, 0.04)),
    rgba(255, 255, 255, 0)
  );
}

/* subtle left accent to visually connect with the dashboard cards */
main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: var(--module-color, #2e7d32);
  border-radius: 4px 0 0 4px;
}

/* Make the Tabs header use module color for active tab */
.p-tabview .p-tabview-nav .p-highlight {
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 46, 125, 50), 0.12),
    rgba(var(--module-color-rgb, 46, 125, 50), 0.06)
  );
  border-color: rgba(var(--module-color-rgb, 46, 125, 50), 0.16) !important;
  color: var(--module-color) !important;
}
</style>
