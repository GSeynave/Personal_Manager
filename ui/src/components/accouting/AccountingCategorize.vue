<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AccountingService from '@/services/AccountingService'
import Transaction, { TransactionCategory, TransactionSubCategory } from '@/model/Transaction'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Select from 'primevue/select'
import CardContent from '../Dashboard/CardContent.vue'

const accountingService = new AccountingService()
const toCategorize = ref<Transaction[]>([])

onMounted(() => {
  accountingService.getTransactionsToCategorize().then((data) => {
    toCategorize.value = data
  })
})

const categories = Object.values(TransactionCategory)
  .filter((k) => isNaN(Number(k)))
  .map((cat) => ({ label: cat, value: cat }))
const subCategories = Object.values(TransactionSubCategory)
  .filter((k) => isNaN(Number(k)))
  .map((subCat) => ({ label: subCat, value: subCat }))
const editingRows = ref({})

function onCellEditComplete(event: any) {
  const { data, field, newValue } = event
  data[field] = newValue
}

function updateToCategorize() {
  accountingService
    .updateTransaction(toCategorize.value)
    .then(() => {
      console.log('Transactions updated successfully')
      accountingService.getTransactionsToCategorize().then((data) => {
        toCategorize.value = data
      })
    })
    .catch((err) => {
      console.error('Error updating transactions:', err)
    })
}
</script>

<template>
  <CardContent :title="'Transaction to categorize'">
    <div class="to-categorized-container" v-if="toCategorize">
      <h3>Transactions to categorize</h3>
      {{ categories }}
      {{ subCategories }}

      <DataTable
        v-model:editingRows="editingRows"
        :value="toCategorize"
        editMode="cell"
        @cell-edit-complete="onCellEditComplete"
      >
        <Column field="date" header="Date" />
        <Column field="description" header="Description" />
        <Column field="amount" header="Amount" />
        <!-- Editable CATEGORY column -->
        <Column field="category" header="Category" editor>
          <template #editor="{ data, field }">
            <Select
              v-model="data[field]"
              :options="categories"
              optionLabel="label"
              optionValue="value"
              placeholder="Select category"
              class="w-full"
            />
          </template>
        </Column>
        <!-- Editable SUBCATEGORY column -->
        <Column field="subCategory" header="Subcategory" editor>
          <template #editor="{ data, field }">
            <Select
              v-model="data[field]"
              :options="subCategories"
              optionLabel="label"
              optionValue="value"
              placeholder="Select category"
              class="w-full"
            />
          </template>
        </Column>
        <Column field="customCategory" header="Custom Category" editor>
          <template #editor="{ data, field }">
            <input type="text" v-model="data[field]" class="w-full" />
          </template>
        </Column>
      </DataTable>

      <button @click="updateToCategorize">Save Categories</button>
    </div>
    <div v-else>None</div>
  </CardContent>
</template>

<style scoped></style>
