<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AccountingService from '@/services/accounting/AccountingService'
import WalletService from '@/services/accounting/WalletService'
import type TransactionCsvRow from '@/model/accounting/TransactionCsvRow'
import type { Wallet } from '@/model/accounting/Wallet'
import CardContent from '@/components/core/Dashboard/CardContent.vue'
import { Upload, Wallet as WalletIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Label } from '@/components/ui/label'

const accountingService = new AccountingService()
const walletService = new WalletService()

const csv = ref<TransactionCsvRow[]>([])
const wallets = ref<Wallet[]>([])
const selectedWalletId = ref<string | null>(null)
const importedLines = ref<number>(-1)
const isImporting = ref(false)

async function loadWallets() {
  try {
    wallets.value = await walletService.getWalletsForCurrentUser()
    // Auto-select first wallet if available
    if (wallets.value.length > 0 && !selectedWalletId.value) {
      const firstWallet = wallets.value[0]
      if (firstWallet) {
        selectedWalletId.value = firstWallet.id.toString()
      }
    }
  } catch (error) {
    console.error('Error loading wallets:', error)
  }
}

async function handleImport() {
  if (csv.value.length === 0) {
    console.warn('No CSV data to import')
    return
  }
  
  if (!selectedWalletId.value) {
    console.error('No wallet selected for import')
    return
  }

  isImporting.value = true
  try {
    importedLines.value = csv.value.length
    await accountingService.importTransactionsFromCsv(csv.value, parseInt(selectedWalletId.value))
  } catch (error) {
    console.error('Error importing transactions:', error)
    importedLines.value = -1
  } finally {
    isImporting.value = false
  }
}

onMounted(() => {
  loadWallets()
})

</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-3 mb-4">
      <Upload class="w-6 h-6 text-accounting" />
      <h2 class="text-xl font-semibold text-foreground">CSV Import</h2>
    </div>

    <!-- Wallet Selection -->
    <CardContent :title="'Select Wallet'">
      <div class="space-y-2">
        <Label for="wallet-select">
          <div class="flex items-center gap-2 mb-2">
            <WalletIcon class="w-4 h-4 text-accounting" />
            <span>Import transactions to:</span>
          </div>
        </Label>
        <Select v-model="selectedWalletId">
          <SelectTrigger id="wallet-select">
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
        <p v-else-if="!selectedWalletId" class="text-sm text-muted-foreground mt-2">
          Please select a wallet before importing transactions.
        </p>
      </div>
    </CardContent>
    
    <CardContent :title="'Upload File'">
      <div v-if="!selectedWalletId" class="p-6 text-center text-muted-foreground border border-dashed rounded-lg">
        <p>Please select a wallet above before uploading your CSV file.</p>
      </div>
      <div v-else class="csv-import-wrapper">        
        <vue-csv-import
          v-model="csv"
          :fields="{
            date: { required: true, label: 'Date' },
            amount: { required: true, label: 'Montant' },
            description: { required: true, label: 'Libellé' },
            currentBalance: { required: false, label: 'Solde' },
          }"
        >
          <!-- Not working yet -->
          <!-- <vue-csv-toggle-headers></vue-csv-toggle-headers> -->
          <vue-csv-errors></vue-csv-errors>
          <vue-csv-input></vue-csv-input>
          <vue-csv-map></vue-csv-map>
        </vue-csv-import>
        
        <!-- Import Button -->
        <div class="mt-4 flex justify-end">
          <Button 
            @click="handleImport" 
            :disabled="csv.length === 0 || !selectedWalletId || isImporting"
            class="bg-accounting hover:bg-accounting/90"
          >
            {{ isImporting ? 'Importing...' : `Import ${csv.length} transactions` }}
          </Button>
        </div>
      </div>
    </CardContent>

    <div v-if="importedLines != -1" class="text-sm text-accounting font-medium">
      Successfully imported {{ importedLines }} transactions
    </div>
  </div>
</template>

<style scoped></style>

<style>
/* Style tables and selects */
:deep(select) {
  padding: 0.5rem 0.75rem !important;
  border-radius: 0.375rem !important;
  border: 1px solid hsl(var(--border)) !important;
  background-color: hsl(var(--background)) !important;
}

:deep(table) {
  width: 100% !important;
  border-collapse: collapse !important;
  margin-top: 1rem !important;
}

:deep(th),
:deep(td) {
  padding: 0.75rem 1rem !important;
  text-align: left !important;
  border: 1px solid hsl(var(--border)) !important;
}

:deep(th) {
  background-color: hsl(var(--card)) !important;
  font-weight: 600 !important;
  color: hsl(var(--foreground)) !important;
}

:deep(td) {
  color: hsl(var(--muted-foreground)) !important;
}
</style>
