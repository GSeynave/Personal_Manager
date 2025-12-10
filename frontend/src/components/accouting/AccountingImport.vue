<script setup lang="ts">
import { ref, watch } from 'vue'
import AccountingService from '@/services/AccountingService'
import type TransactionCsvRow from '@/model/TransactionCsvRow'
import CardContent from '../Dashboard/CardContent.vue'
import { Upload } from 'lucide-vue-next'

const accountingService = new AccountingService()

const csv = ref<TransactionCsvRow[]>([])

const importedLines = ref<number>(-1)
watch(csv, (newVal) => {
  csv.value = newVal
  importedLines.value = newVal.length

  accountingService.importTransactionsFromCsv(csv.value)
})

</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-3 mb-4">
      <Upload class="w-6 h-6 text-accounting" />
      <h2 class="text-xl font-semibold text-foreground">CSV Import</h2>
    </div>
    
    <CardContent :title="'Upload File'">
      <div class="csv-import-wrapper">        
        <vue-csv-import
          v-model="csv"
          :fields="{
            date: { required: true, label: 'Date' },
            amount: { required: true, label: 'Montant' },
            description: { required: true, label: 'Libellé' },
          }"
        >
          <!-- Not working yet -->
          <!-- <vue-csv-toggle-headers></vue-csv-toggle-headers> -->
          <vue-csv-errors></vue-csv-errors>
          <vue-csv-input></vue-csv-input>
          <vue-csv-map></vue-csv-map>
        </vue-csv-import>
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
