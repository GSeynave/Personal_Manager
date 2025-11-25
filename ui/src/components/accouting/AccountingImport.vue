<script setup lang="ts">
import { ref, watch } from 'vue'
import AccountingService from '@/services/AccountingService'
import type TransactionCsvRow from '@/model/TransactionCsvRow'
import CardContent from '../Dashboard/CardContent.vue'

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
  <CardContent :title="'CSV Import'">
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
  </CardContent>

  <div v-if="importedLines != -1">Nb line imported : {{ importedLines }}</div>
</template>

<style scoped></style>
