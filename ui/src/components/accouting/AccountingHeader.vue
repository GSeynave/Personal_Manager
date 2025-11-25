<script setup lang="ts">
import CardContent from '../Dashboard/CardContent.vue'

const props = defineProps(['accountingSummary'])
</script>

<template>
  <CardContent :title="'Summary'">
    <div class="accounting_summary_container" v-if="props.accountingSummary">
      <div class="accounting_summary_card">
        <h2>Revenue</h2>
        <p class="amount">{{ props.accountingSummary.income }} €</p>
      </div>
      <div class="accounting_summary_card">
        <h2>Expense</h2>
        <p class="amount">{{ props.accountingSummary.expense }} €</p>
      </div>
      <div class="accounting_summary_card">
        <h2>Saving</h2>
        <p class="amount">{{ props.accountingSummary.saving }} €</p>
      </div>
    </div>
  </CardContent>
</template>

<style scoped>
.accounting_summary_container {
  display: flex;
  gap: 16px;
  margin-top: 18px;
}

.accounting_summary_card {
  flex: 1 1 0;
  padding: 18px;
  border-radius: 12px;
  background: linear-gradient(
    180deg,
    rgba(var(--module-color-rgb, 46, 125, 50), var(--module-tint-alpha, 0.05)),
    rgba(0, 0, 0, 0.03)
  );
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.35);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}

.accounting_summary_card h2 {
  margin: 0;
  font-size: 0.95rem;
  color: var(--module-color, #2e7d32);
}

.accounting_summary_card .amount {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
}

@media (max-width: 800px) {
  .accounting_summary_container {
    flex-direction: column;
  }
}
</style>
