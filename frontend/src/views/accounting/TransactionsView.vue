<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'

import Breadcrumb from '@/components/core/Breadcrumb.vue'
import WalletSelector from '@/components/accounting/WalletSelector.vue'
import TransactionCategoryDialog from '@/components/accounting/TransactionCategoryDialog.vue'
import CategoryCreateDialog from '@/components/accounting/CategoryCreateDialog.vue'

import AccountingService from '@/services/accounting/AccountingService'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'

import type Transaction from '@/model/accounting/Transaction'
import type { TransactionCategoryDTO } from '@/model/accounting/TransactionCategory'
import type { Wallet } from '@/model/accounting/Wallet'

import { Receipt, Trash2, Edit2, Search, Check, X } from 'lucide-vue-next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

const accountingService = new AccountingService()
const categoryService = new TransactionCategoryService()

const selectedWalletId = ref<number | null>(null)
const selectedWallet = ref<Wallet | null>(null)
const transactions = ref<Transaction[]>([])
const categories = ref<TransactionCategoryDTO[]>([])
const loading = ref(false)
const searchQuery = ref('')
const filterStatus = ref<'all' | 'categorized' | 'uncategorized'>('uncategorized') // Default to uncategorized
const currentPage = ref(1)
const itemsPerPage = 20
const categoryDialogOpen = ref(false)
const createCategoryDialogOpen = ref(false)
const selectedTransaction = ref<Transaction | null>(null)
const editingLabelId = ref<number | null>(null)
const editingLabelValue = ref('')
const recentCategoryIds = ref<number[]>([])

const filteredTransactions = computed(() => {
  let filtered = transactions.value

  // Note: All transactions are already uncategorized from the API
  // Filter options kept for future when we add categorized transactions

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(t => 
      t.importLabel?.toLowerCase().includes(query) ||
      t.customLabel?.toLowerCase().includes(query)
    )
  }

  return filtered
})

const paginatedTransactions = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredTransactions.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredTransactions.value.length / itemsPerPage)
})

const uncategorizedCount = computed(() => {
  return transactions.value.filter(t => t.category === null).length
})

function onWalletSelected(walletId: number) {
  selectedWalletId.value = walletId
  fetchTransactions()
}

function onWalletChanged(wallet: Wallet) {
  selectedWallet.value = wallet
}

async function fetchTransactions() {
  if (!selectedWalletId.value) return
  
  loading.value = true
  try {
    // Fetch only uncategorized transactions
    const data = await accountingService.getTransactionsToCategorize(selectedWalletId.value)
    
    console.log('Uncategorized transactions data:', data)
    
    // UncategorizedTransactions contains a transactions array
    if (data && data.transactions && Array.isArray(data.transactions)) {
      transactions.value = data.transactions
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      
      console.log('Loaded transactions to categorize:', transactions.value.length)
    } else {
      console.warn('Unexpected data format:', data)
      transactions.value = []
    }
  } catch (err) {
    console.error('Error fetching transactions:', err)
    transactions.value = []
  } finally {
    loading.value = false
  }
}

function openCategoryDialog(transaction: Transaction) {
  selectedTransaction.value = transaction
  categoryDialogOpen.value = true
}

async function handleCategorySelected(category: TransactionCategoryDTO) {
  if (!selectedTransaction.value) return
  
  try {
    // Save to recent categories
    saveRecentCategory(category.id)
    
    // Update the transaction category
    const index = transactions.value.findIndex(t => t.id === selectedTransaction.value!.id)
    if (index !== -1) {
      transactions.value[index].category = category as any
      
      // Call API to save the categorized transaction
      await accountingService.updateTransaction(transactions.value[index], category.id)
      
      // Remove from uncategorized list since it's now categorized
      transactions.value.splice(index, 1)
    }
    
    categoryDialogOpen.value = false
    selectedTransaction.value = null
  } catch (error) {
    console.error('Error updating category:', error)
    // Revert the change on error
    const index = transactions.value.findIndex(t => t.id === selectedTransaction.value!.id)
    if (index !== -1) {
      transactions.value[index].category = null as any
    }
  }
}

function handleCreateCategory() {
  categoryDialogOpen.value = false
  createCategoryDialogOpen.value = true
}

function handleCategoryCreated() {
  createCategoryDialogOpen.value = false
  // Reload categories
  loadCategories()
  // Reopen the category selection dialog
  if (selectedTransaction.value) {
    categoryDialogOpen.value = true
  }
}

async function loadCategories() {
  try {
    categories.value = await categoryService.getAllCategories()
  } catch (error) {
    console.error('Error loading categories:', error)
  }
}

function loadRecentCategories() {
  const stored = localStorage.getItem('recentCategories')
  if (!stored) return
  
  try {
    const data = JSON.parse(stored)
    const sevenDaysAgo = Date.now() - 7 * 24 * 60 * 60 * 1000
    
    // Filter categories used in the last 7 days
    const recent = data
      .filter((item: any) => item.timestamp > sevenDaysAgo)
      .sort((a: any, b: any) => b.timestamp - a.timestamp)
      .slice(0, 5)
      .map((item: any) => item.categoryId)
    
    recentCategoryIds.value = recent
  } catch (error) {
    console.error('Error loading recent categories:', error)
  }
}

function saveRecentCategory(categoryId: number) {
  const stored = localStorage.getItem('recentCategories')
  let data: any[] = []
  
  if (stored) {
    try {
      data = JSON.parse(stored)
    } catch (error) {
      console.error('Error parsing recent categories:', error)
    }
  }
  
  // Remove existing entry for this category
  data = data.filter(item => item.categoryId !== categoryId)
  
  // Add new entry at the beginning
  data.unshift({
    categoryId,
    timestamp: Date.now()
  })
  
  // Keep only the last 20 entries
  data = data.slice(0, 20)
  
  localStorage.setItem('recentCategories', JSON.stringify(data))
  loadRecentCategories()
}

function startEditLabel(transaction: Transaction) {
  editingLabelId.value = transaction.id
  editingLabelValue.value = transaction.customLabel || ''
}

function cancelEditLabel() {
  editingLabelId.value = null
  editingLabelValue.value = ''
}

async function saveLabel(transaction: Transaction) {
  try {
    const index = transactions.value.findIndex(t => t.id === transaction.id)
    if (index !== -1) {
      const oldLabel = transactions.value[index].customLabel
      transactions.value[index].customLabel = editingLabelValue.value
      
      // Call API to save the custom label (no category change)
      await accountingService.updateTransaction(transactions.value[index])
      
      editingLabelId.value = null
      editingLabelValue.value = ''
    }
  } catch (error) {
    console.error('Error updating label:', error)
    // Revert on error
    const index = transactions.value.findIndex(t => t.id === transaction.id)
    if (index !== -1) {
      transactions.value[index].customLabel = transaction.customLabel
    }
  }
}

async function deleteTransaction(transaction: Transaction) {
  if (!confirm(`Delete transaction "${transaction.customLabel || transaction.importLabel}"?`)) {
    return
  }
  
  try {
    // TODO: Add API call when backend endpoint is ready
    // await accountingService.deleteTransaction(transaction.id)
    
    const index = transactions.value.findIndex(t => t.id === transaction.id)
    if (index !== -1) {
      transactions.value.splice(index, 1)
    }
  } catch (error) {
    console.error('Error deleting transaction:', error)
  }
}

function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}

function formatDate(dateString: string | Date): string {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
  }).format(date)
}

function getCategoryDisplay(transaction: Transaction): string {
  if (!transaction.category) return 'Add category...'
  return (transaction.category as any).title || 'Unknown'
}

function getCategoryIcon(transaction: Transaction): string {
  if (!transaction.category) return '🏷️'
  return (transaction.category as any).icon || '📁'
}

onMounted(() => {
  // Wallet selector will auto-load and select first wallet
  loadRecentCategories()
  loadCategories()
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
      { label: 'Transactions' }
    ]" />

    <!-- Page Header -->
    <div class="flex items-center justify-between">
      <div>
        <div class="flex items-center gap-3 mb-2">
          <Receipt class="w-8 h-8 text-accounting" />
          <h1 class="text-3xl font-bold text-foreground">Transactions</h1>
        </div>
        <p class="text-sm text-accounting">
          Categorize and manage your transactions
        </p>
      </div>
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
        <!-- Filters Bar -->
        <div class="flex items-center justify-between gap-3 p-4 bg-card border rounded-lg">
          <div class="flex items-center gap-3 flex-1">
            <div class="relative flex-1">
              <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                v-model="searchQuery"
                placeholder="Search transactions..."
                class="pl-10"
              />
            </div>
          </div>
          <Badge v-if="transactions.length > 0" variant="secondary" class="text-sm">
            {{ transactions.length }} waiting for categories
          </Badge>
        </div>

        <!-- Transactions Table -->
        <div class="bg-card border rounded-lg overflow-hidden">
          <div v-if="loading" class="text-center py-12 text-muted-foreground">
            Loading transactions...
          </div>

          <div v-else-if="paginatedTransactions.length === 0" class="text-center py-12 text-muted-foreground">
            <p v-if="searchQuery">No transactions found matching "{{ searchQuery }}"</p>
            <p v-else>No transactions to categorize! 🎉</p>
          </div>

          <Table v-else>
            <TableHeader>
              <TableRow>
                <TableHead class="w-[120px]">Date</TableHead>
                <TableHead>Import Label</TableHead>
                <TableHead>Custom Label</TableHead>
                <TableHead class="w-[160px]">Category</TableHead>
                <TableHead class="text-right w-[120px]">Amount</TableHead>
                <TableHead class="text-right w-[80px]">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              <TableRow v-for="transaction in paginatedTransactions" :key="transaction.id">
                <!-- Date -->
                <TableCell class="text-sm text-muted-foreground">
                  {{ formatDate(transaction.date) }}
                </TableCell>
                
                <!-- Import Label -->
                <TableCell class="text-xs text-muted-foreground max-w-[200px]">
                  <div class="line-clamp-2 leading-tight">
                    {{ transaction.importLabel }}
                  </div>
                </TableCell>
                
                <!-- Custom Label (Editable) -->
                <TableCell>
                  <div v-if="editingLabelId === transaction.id" class="flex items-center gap-1">
                    <Input
                      v-model="editingLabelValue"
                      class="h-8 flex-1"
                      placeholder="Add custom label..."
                      @keyup.enter="saveLabel(transaction)"
                      @keyup.escape="cancelEditLabel"
                      autofocus
                    />
                    <Button size="icon" variant="ghost" class="h-8 w-8 shrink-0" @click="saveLabel(transaction)" title="Save (Enter)">
                      <Check class="w-4 h-4 text-green-600" />
                    </Button>
                    <Button size="icon" variant="ghost" class="h-8 w-8 shrink-0" @click="cancelEditLabel" title="Cancel (Esc)">
                      <X class="w-4 h-4 text-muted-foreground" />
                    </Button>
                  </div>
                  <div v-else class="flex items-center gap-2 group">
                    <span v-if="transaction.customLabel" class="text-sm flex-1">{{ transaction.customLabel }}</span>
                    <span v-else class="text-sm text-muted-foreground italic flex-1">No custom label</span>
                    <Button
                      size="icon"
                      variant="ghost"
                      class="h-6 w-6 opacity-0 group-hover:opacity-100 shrink-0"
                      @click="startEditLabel(transaction)"
                    >
                      <Edit2 class="w-3 h-3" />
                    </Button>
                  </div>
                </TableCell>
                
                <!-- Category -->
                <TableCell>
                  <Button
                    size="sm"
                    variant="outline"
                    class="gap-2 w-full justify-start border-dashed border-primary/50 text-primary hover:bg-primary/5"
                    @click="openCategoryDialog(transaction)"
                  >
                    <span>{{ getCategoryIcon(transaction) }}</span>
                    <span class="text-xs truncate">{{ getCategoryDisplay(transaction) }}</span>
                  </Button>
                </TableCell>
                
                <!-- Amount -->
                <TableCell class="text-right">
                  <span
                    :class="[
                      'font-semibold',
                      transaction.amount >= 0 ? 'text-green-600' : 'text-red-600'
                    ]"
                  >
                    {{ transaction.amount >= 0 ? '+' : '' }}{{ formatCurrency(transaction.amount) }}
                  </span>
                </TableCell>
                
                <!-- Actions -->
                <TableCell class="text-right">
                  <Button
                    size="icon"
                    variant="ghost"
                    class="h-8 w-8"
                    @click="deleteTransaction(transaction)"
                  >
                    <Trash2 class="w-4 h-4 text-destructive" />
                  </Button>
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="flex items-center justify-between p-4 border-t bg-muted/30">
            <div class="text-sm text-muted-foreground">
              Page {{ currentPage }} of {{ totalPages }} • {{ filteredTransactions.length }} transactions
            </div>
            <div class="flex items-center gap-2">
              <Button
                size="sm"
                variant="outline"
                :disabled="currentPage === 1"
                @click="currentPage--"
              >
                Previous
              </Button>
              <Button
                size="sm"
                variant="outline"
                :disabled="currentPage === totalPages"
                @click="currentPage++"
              >
                Next
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Category Selection Dialog -->
    <TransactionCategoryDialog
      v-model:open="categoryDialogOpen"
      :transaction-label="selectedTransaction?.customLabel || selectedTransaction?.importLabel || ''"
      :recent-category-ids="recentCategoryIds"
      @category-selected="handleCategorySelected"
      @create-category="handleCreateCategory"
    />
    
    <!-- Category Create Dialog -->
    <CategoryCreateDialog
      v-model:open="createCategoryDialogOpen"
      :categories="categories"
      @category-created="handleCategoryCreated"
    />
  </main>
</template>

<style scoped>
.page-header {
  padding-left: 12px;
}
</style>
