<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  compact?: boolean
  dataPeak?: string
  data?: { revenue?: string; leftover?: string; series?: { income?: number[]; expense?: number[] } }
}>()

// Helpers: parsing and formatting numbers safely
function parseNumberFromMaybeCurrency(v: unknown): number {
  if (v == null) return 0
  if (typeof v === 'number') return v
  if (typeof v === 'string') {
    const cleaned = v.replace(/[^0-9.\-]+/g, '')
    const n = Number(cleaned)
    return Number.isFinite(n) ? n : 0
  }
  return 0
}

function formatMoney(n: number) {
  try {
    return new Intl.NumberFormat(undefined, {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 2,
    }).format(n)
  } catch {
    // fallback
    return `€${n.toFixed(2)}`
  }
}

const numbers = computed(() => {
  const income = parseNumberFromMaybeCurrency(props.data?.revenue)
  const remaining = parseNumberFromMaybeCurrency(props.data?.leftover)
  const expense = Math.max(0, income - remaining)
  return {
    income,
    expense,
    remaining,
    incomeStr: formatMoney(income),
    expenseStr: formatMoney(expense),
    remainingStr: formatMoney(remaining),
  }
})
</script>

<template>
  <div class="finance-card">
    <template v-if="props.compact">
      <h3>
        <i class="pi pi-wallet" style="font-size: 1em; margin-right: 0.3em"></i>
        Accounting
      </h3>
      <div class="compact-summary">
        <div class="summary-item income">
          <div class="label">Income</div>
          <div class="value">{{ numbers.incomeStr }}</div>
        </div>
        <div class="summary-item expense">
          <div class="label">Expense</div>
          <div class="value">{{ numbers.expenseStr }}</div>
        </div>
        <div class="summary-item remaining">
          <div class="label">Remaining</div>
          <div class="value">{{ numbers.remainingStr }}</div>
        </div>
      </div>
    </template>

    <template v-else>
      <h2>
        <i class="pi pi-wallet" style="font-size: 1em; margin-right: 0.3em"></i>
        Accounting
      </h2>
      <p class="feature-description">Manage your finances and track expenses.</p>
      <p class="feature-overview">
        Revenue: {{ props.data?.revenue ?? '€3,000' }}, Leftover:
        {{ props.data?.leftover ?? '€2,000' }}
      </p>
      <p class="feature-go-to">Go to Accounting -></p>
    </template>
  </div>
</template>

<!-- no additional script -->

<style scoped>
.finance-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Headings align with centralized shell colors */
.finance-card h3,
.finance-card h2 {
  margin: 0 0 12px 0;
  padding-left: 8px;
  color: var(--card-foreground, #fff);
  font-weight: 600;
}

.compact-summary {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-top: 8px;
}

.summary-item {
  flex: 1 1 0;
  text-align: center;
  padding: 10px 8px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.02);
}

.summary-item .label {
  font-size: 0.72rem;
  color: rgba(255, 255, 255, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.summary-item .value {
  font-weight: 700;
  margin-top: 6px;
  font-size: 0.95rem;
  color: var(--card-foreground, #fff);
}

.summary-item.income .value {
  color: var(--module-color, #d4f1d4);
}

.summary-item.expense .value {
  color: var(--module-color, #ffe4e4);
}

.summary-item.remaining .value {
  color: var(--module-color, #e0f2f1);
}

.finance-card .feature-description {
  color: rgba(255, 255, 255, 0.86);
  font-size: 0.95rem;
  margin: 8px 0;
}

.finance-card .feature-overview {
  color: rgba(255, 255, 255, 0.76);
  font-size: 0.95rem;
  margin: 8px 0;
}

.finance-card .feature-go-to {
  color: var(--module-color, #b3f5a8);
  font-weight: 600;
  margin-top: 12px;
}
</style>
