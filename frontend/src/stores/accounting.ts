import { defineStore } from 'pinia'
import { ref } from 'vue'
import AccountingService from '@/services/accounting/AccountingService'

const accountingService = new AccountingService()

export const useAccountingStore = defineStore('accounting', () => {
  const uncategorizedCount = ref<number>(0)
  const isLoading = ref<boolean>(false)

  async function fetchUncategorizedCount(walletId?: number) {
    try {
      isLoading.value = true
      const data = await accountingService.getTransactionsToCategorize(walletId)
      uncategorizedCount.value = data.totalElements || 0
    } catch (err) {
      console.error('Error fetching uncategorized count:', err)
      uncategorizedCount.value = 0
    } finally {
      isLoading.value = false
    }
  }

  function clearUncategorizedCount() {
    uncategorizedCount.value = 0
  }

  return {
    uncategorizedCount,
    isLoading,
    fetchUncategorizedCount,
    clearUncategorizedCount,
  }
})
