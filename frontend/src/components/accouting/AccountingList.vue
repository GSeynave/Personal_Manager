<script setup lang="ts">
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import { ref } from 'vue'
import CardContent from '../Dashboard/CardContent.vue'

const props = defineProps(['transactionSummary'])
const expandedRows = ref({})
</script>

<template>
  <CardContent :title="'Transactions'">
    <div class="table-wrapper">
      <DataTable
        class="accent-table"
        :value="props.transactionSummary"
        v-model:expandedRows="expandedRows"
        dataKey="category"
        rowExpansionTemplate
      >
        <Column expander style="width: 3em"></Column>
        <Column field="category" header="Category"></Column>
        <Column field="expense" header="Expense"></Column>
        <!-- add a percent symbol -->

        <Column field="percent" header="Percent"></Column>
        <Column field="maxExpected" header="Max Expected"></Column>
        <!-- Make a column that is expandable on the transactions value, and group them by transaction.customCategory -->
        <template #expansion="slotProps">
          <DataTable :value="slotProps.data.transactions">
            <Column field="customCategory" header="Description"></Column>
            <Column field="amount" header="Amount"></Column>
            <Column field="date" header="Date"></Column>
          </DataTable>
        </template>
      </DataTable>
    </div>
  </CardContent>
</template>
<style scoped>
.table-wrapper {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

/* PrimeVue table header tint and accent */
:deep(.accent-table .p-datatable-thead > tr > th) {
  background: linear-gradient(
    90deg,
    rgba(var(--module-color-rgb, 46, 125, 50), 0.08),
    rgba(var(--module-color-rgb, 46, 125, 50), 0.03)
  );
  color: var(--module-color, #2e7d32);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

:deep(.accent-table .p-datatable) {
  background: var(--card-bg, rgba(255, 255, 255, 0.02));
}

:deep(.accent-table .p-datatable-tbody > tr > td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.02);
}
</style>
