<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AccountingService from '@/services/accounting/AccountingService'
import WalletService from '@/services/accounting/WalletService'
import TransactionCategory from '@/model/accounting/TransactionCategory'
import type { Wallet } from '@/model/accounting/Wallet'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import CardContent from '@/components/core/Dashboard/CardContent.vue'
import { Tags, Save, Wallet as WalletIcon } from 'lucide-vue-next'
import type UncategorizedTransactions from '@/model/accounting/UncategorizedTransactions'

const accountingService = new AccountingService()
const walletService = new WalletService()
const toCategorize = ref<UncategorizedTransactions>()
const wallets = ref<Wallet[]>([])
const selectedWalletId = ref<string | null>(null)

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
})

const categories = Object.values(TransactionCategory)
  .filter((k) => isNaN(Number(k)))
  .map((cat) => ({ label: cat, value: cat }))

function updateToCategorize() {
  accountingService
    .updateTransaction(toCategorize.value?.transactions || [])
    .then(() => {
      console.log('Transactions updated successfully')
      loadTransactions()
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
                <TableHead>Subcategory</TableHead>
                <TableHead>Custom Category</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              <!-- TODO turn this into a data table ? for pagination -->
              <TableRow v-for="(transaction, idx) in toCategorize.transactions" :key="idx">
                <TableCell>{{ transaction.date }}</TableCell>
                <TableCell>{{ transaction.importLabel }}</TableCell>
                <TableCell :class="isPositive(transaction.amount) ? 'text-green-400' : 'text-red-400'">{{
                  transaction.amount }}</TableCell>
                <TableCell>
                  <Select v-model="transaction.category">
                    <SelectTrigger class="w-[180px]">
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem v-for="cat in categories" :key="cat.value" :value="cat.value">
                        {{ cat.label }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </TableCell>
                <TableCell>
                  <Input v-model="transaction.customLabel" type="text" class="w-full" />
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
  </div>
</template>

<style scoped></style>
