<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AccountingService from '@/services/accounting/AccountingService'
import WalletService from '@/services/accounting/WalletService'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'
import TransactionCategory from '@/model/accounting/TransactionCategory'
import type { TransactionCategoryDTO } from '@/model/accounting/TransactionCategory'
import type { Wallet } from '@/model/accounting/Wallet'
import type Transaction from '@/model/accounting/Transaction'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import CardContent from '@/components/core/Dashboard/CardContent.vue'
import TransactionCategoryDialog from '@/components/accounting/TransactionCategoryDialog.vue'
import CategoryCreateDialog from '@/components/accounting/CategoryCreateDialog.vue'
import { Tags, Save, Wallet as WalletIcon } from 'lucide-vue-next'
import type UncategorizedTransactions from '@/model/accounting/UncategorizedTransactions'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useAccountingStore } from '@/stores/accounting'

const accountingService = new AccountingService()
const walletService = new WalletService()
const categoryService = new TransactionCategoryService()
const accountingStore = useAccountingStore()
const toCategorize = ref<UncategorizedTransactions>()
const wallets = ref<Wallet[]>([])
const selectedWalletId = ref<string | null>(null)
const categories = ref<TransactionCategoryDTO[]>([])
const categoryDialogOpen = ref(false)
const createCategoryDialogOpen = ref(false)
const selectedTransaction = ref<Transaction | null>(null)
const recentCategoryIds = ref<number[]>([])

// Track recently used categories (last 7 days)
const RECENT_CATEGORIES_KEY = 'recent-transaction-categories'
const RECENT_CATEGORIES_MAX = 5

function loadRecentCategories() {
  try {
    const stored = localStorage.getItem(RECENT_CATEGORIES_KEY)
    if (stored) {
      const data = JSON.parse(stored)
      const sevenDaysAgo = Date.now() - (7 * 24 * 60 * 60 * 1000)
      // Filter categories used within last 7 days
      const recent = data
        .filter((item: any) => item.timestamp > sevenDaysAgo)
        .map((item: any) => item.categoryId)
      recentCategoryIds.value = [...new Set(recent)].slice(0, RECENT_CATEGORIES_MAX)
    }
  } catch (error) {
    console.error('Error loading recent categories:', error)
  }
}

function addRecentCategory(categoryId: number) {
  try {
    const stored = localStorage.getItem(RECENT_CATEGORIES_KEY)
    let data = stored ? JSON.parse(stored) : []
    
    // Add new entry
    data.unshift({ categoryId, timestamp: Date.now() })
    
    // Keep only last 50 entries
    data = data.slice(0, 50)
    
    localStorage.setItem(RECENT_CATEGORIES_KEY, JSON.stringify(data))
    loadRecentCategories()
  } catch (error) {
    console.error('Error saving recent category:', error)
  }
}

async function loadWallets() {
  try {
    wallets.value = await walletService.getWalletsForCurrentUser()
    // Auto-select first wallet if available
    if (wallets.value.length > 0 && !selectedWalletId.value) {
      const firstWallet = wallets.value[0]
      if (firstWallet) {
        selectedWalletId.value = firstWallet.id.toString()
        loadTransactions()
      }
    }
  } catch (error) {
    console.error('Error loading wallets:', error)
  }
}

function onWalletChange() {
  loadTransactions()
}

function loadTransactions() {
  if (!selectedWalletId.value) return
  
  accountingService.getTransactionsToCategorize(parseInt(selectedWalletId.value)).then((data) => {
    toCategorize.value = data
  })
}

onMounted(() => {
  loadWallets()
  loadCategories()
  loadRecentCategories()
})

async function loadCategories() {
  try {
    categories.value = await categoryService.getRootCategories()
  } catch (error) {
    console.error('Error loading categories:', error)
  }
}

function openCategoryDialog(transaction: Transaction) {
  selectedTransaction.value = transaction
  categoryDialogOpen.value = true
}

function handleCategorySelected(category: TransactionCategoryDTO) {
  if (selectedTransaction.value) {
    // Update the transaction with the selected category
    selectedTransaction.value.category = category as any // TODO: Fix type mismatch
    // Track this category as recently used
    addRecentCategory(category.id)
  }
}

function handleCategoryCreated(category: TransactionCategoryDTO) {
  // Reload categories to include the new one
  loadCategories()
  // Optionally select it for the current transaction
  if (selectedTransaction.value) {
    selectedTransaction.value.category = category as any
  }
}

const oldCategories = Object.values(TransactionCategory)
  .filter((k) => isNaN(Number(k)))
  .map((cat) => ({ label: cat, value: cat }))

function updateToCategorize() {
  accountingService
    .updateTransaction(toCategorize.value?.transactions || [])
    .then(() => {
      console.log('Transactions updated successfully')
      loadTransactions()
      // Update the uncategorized count in the store
      if (selectedWalletId.value) {
        accountingStore.fetchUncategorizedCount(parseInt(selectedWalletId.value))
      }
    })
    .catch((err) => {
      console.error('Error updating transactions:', err)
    })
}

function isPositive(amount: number): boolean {
  return amount >= 0
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-3 mb-4">
      <Tags class="w-6 h-6 text-accounting" />
      <h2 class="text-xl font-semibold text-foreground">Categorize Transactions</h2>
    </div>

    <!-- Wallet Selection -->
    <CardContent :title="'Select Wallet'">
      <div class="space-y-2">
        <Label for="wallet-select-categorize">
          <div class="flex items-center gap-2 mb-2">
            <WalletIcon class="w-4 h-4 text-accounting" />
            <span>Categorize transactions from:</span>
          </div>
        </Label>
        <Select v-model="selectedWalletId" @update:model-value="onWalletChange">
          <SelectTrigger id="wallet-select-categorize">
            <SelectValue placeholder="Select a wallet..." />
          </SelectTrigger>
          <SelectContent>
            <SelectItem 
              v-for="wallet in wallets" 
              :key="wallet.id" 
              :value="wallet.id.toString()"
            >
              {{ wallet.name }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="wallets.length === 0" class="text-sm text-destructive mt-2">
          No wallets found. Please create a wallet first in the Report tab.
        </p>
      </div>
    </CardContent>

    <CardContent v-if="toCategorize && toCategorize.transactions.length > 0">
      <div class="space-y-4">

        <div class="border rounded-md">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Description</TableHead>
                <TableHead>Amount</TableHead>
                <TableHead>Category</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              <TableRow v-for="(transaction, idx) in toCategorize.transactions" :key="idx">
                <TableCell>{{ transaction.date }}</TableCell>
                <TableCell>{{ transaction.importLabel }}</TableCell>
                <TableCell :class="isPositive(transaction.amount) ? 'text-green-400' : 'text-red-400'">
                  {{ transaction.amount }}€
                </TableCell>
                <TableCell>
                  <Button
                    variant="outline"
                    size="sm"
                    @click="openCategoryDialog(transaction)"
                    class="w-full justify-start"
                  >
                    <span v-if="transaction.category">
                      {{ typeof transaction.category === 'object' ? transaction.category.title : transaction.category }}
                    </span>
                    <span v-else class="text-muted-foreground">
                      Select category...
                    </span>
                  </Button>
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </div>

        <Button @click="updateToCategorize" class="w-full">
          <Save class="w-4 h-4 mr-2 text-accounting" />
          Save Categories
        </Button>
      </div>
    </CardContent>

    <CardContent v-else>
      <div class="text-center text-muted-foreground py-8">
        <p>No transactions to categorize</p>
      </div>
    </CardContent>

    <!-- Category Selection Dialog -->
    <TransactionCategoryDialog
      v-model:open="categoryDialogOpen"
      :transaction-label="selectedTransaction?.importLabel || ''"
      :recent-category-ids="recentCategoryIds"
      @category-selected="handleCategorySelected"
      @create-category="createCategoryDialogOpen = true"
    />

    <!-- Create Category Dialog -->
    <CategoryCreateDialog
      v-model:open="createCategoryDialogOpen"
      :categories="categories"
      @category-created="handleCategoryCreated"
    />
  </div>
</template>

<style scoped></style>
