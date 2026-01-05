<script setup lang="ts">
import { computed } from 'vue'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { TrendingUp, TrendingDown, DollarSign, ChevronLeft, ChevronRight, Calendar } from 'lucide-vue-next'
import type AccountingSummary from '@/model/accounting/AccountingSummary'

const props = defineProps<{
  accountingSummary?: AccountingSummary
  walletName?: string
  period: string
  dateRange?: { minDate: string, maxDate: string }
}>()

const emit = defineEmits<{
  periodChange: [value: string]
}>()

const balance = computed(() => {
  if (!props.accountingSummary) return null
  // Only use actual wallet balance from backend, don't calculate
  return props.accountingSummary.balance ?? null
})

const income = computed(() => props.accountingSummary?.income ?? null)
const expense = computed(() => props.accountingSummary?.expense ?? null)

const net = computed(() => {
  if (income.value === null || expense.value === null) return null
  // Expense is stored as positive value, so subtract it from income
  return income.value - expense.value
})

const formatCurrency = (amount: number | null) => {
  if (amount === null || amount === undefined) {
    return '€ -,--'
  }
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}

const dateRangeDisplay = computed(() => {
  if (!props.dateRange) return ''
  
  const start = new Date(props.dateRange.minDate)
  const end = new Date(props.dateRange.maxDate)
  
  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      year: 'numeric'
    })
  }
  
  return `${formatDate(start)} - ${formatDate(end)}`
})
</script>

<template>
  <div class="wallet-summary space-y-4">
    <!-- Account Balance - Always Visible -->
    <div class="flex items-center justify-between pb-3 border-b border-border">
      <div class="flex items-center gap-3">
        <DollarSign class="w-6 h-6 text-accounting" />
        <div>
          <p class="text-sm text-muted-foreground">{{ walletName || 'Wallet' }} Balance</p>
          <p 
            :class="[
              'text-2xl font-bold',
              balance === null ? 'text-muted-foreground' : balance >= 0 ? 'text-green-600' : 'text-red-600'
            ]"
          >
            {{ formatCurrency(balance) }}
          </p>
        </div>
      </div>
    </div>

    <!-- Period Selector with Navigation -->
    <div class="space-y-3">
      <!-- Quick Period Tabs -->
      <div class="flex items-center gap-2 p-1 bg-muted/30 rounded-lg">
        <button
          @click="emit('periodChange', 'this-month')"
          :class="[
            'flex-1 py-2 px-3 rounded text-sm font-medium transition-all',
            period === 'this-month' 
              ? 'bg-background shadow-sm text-foreground' 
              : 'text-muted-foreground hover:text-foreground'
          ]"
        >
          This Month
        </button>
        <button
          @click="emit('periodChange', 'last-month')"
          :class="[
            'flex-1 py-2 px-3 rounded text-sm font-medium transition-all',
            period === 'last-month' 
              ? 'bg-background shadow-sm text-foreground' 
              : 'text-muted-foreground hover:text-foreground'
          ]"
        >
          Last Month
        </button>
        <button
          @click="emit('periodChange', 'this-year')"
          :class="[
            'flex-1 py-2 px-3 rounded text-sm font-medium transition-all',
            period === 'this-year' 
              ? 'bg-background shadow-sm text-foreground' 
              : 'text-muted-foreground hover:text-foreground'
          ]"
        >
          This Year
        </button>
      </div>
      
      <!-- Date Range Display -->
      <div v-if="dateRangeDisplay" class="flex items-center justify-center gap-2 py-2 px-3 rounded-lg bg-muted/20">
        <Calendar class="w-4 h-4 text-muted-foreground" />
        <span class="text-sm font-medium text-muted-foreground">{{ dateRangeDisplay }}</span>
      </div>
      
      <!-- Month/Year Navigation -->
      <div class="flex items-center justify-between px-2">
        <button 
          @click="emit('periodChange', 'navigate-prev')"
          class="flex items-center gap-1 px-3 py-1.5 rounded hover:bg-muted transition-colors text-sm"
          title="Previous period"
        >
          <ChevronLeft class="w-4 h-4" />
          <span class="text-muted-foreground">Previous</span>
        </button>
        <button 
          @click="emit('periodChange', 'navigate-next')"
          class="flex items-center gap-1 px-3 py-1.5 rounded hover:bg-muted transition-colors text-sm"
          title="Next period"
        >
          <span class="text-muted-foreground">Next</span>
          <ChevronRight class="w-4 h-4" />
        </button>
      </div>
    </div>

    <!-- Income/Expenses/Net Summary -->
    <div class="grid grid-cols-3 gap-3">
      <!-- Income -->
      <div class="flex flex-col gap-1 p-3 rounded-lg bg-green-500/10 border border-green-500/20">
        <div class="flex items-center gap-1">
          <TrendingUp class="w-4 h-4 text-green-600" />
          <span class="text-xs text-green-600 font-medium">Income</span>
        </div>
        <p :class="['text-lg font-bold', income === null ? 'text-muted-foreground' : 'text-green-600']">
          {{ formatCurrency(income) }}
        </p>
      </div>

      <!-- Expenses -->
      <div class="flex flex-col gap-1 p-3 rounded-lg bg-red-500/10 border border-red-500/20">
        <div class="flex items-center gap-1">
          <TrendingDown class="w-4 h-4 text-red-600" />
          <span class="text-xs text-red-600 font-medium">Expenses</span>
        </div>
        <p :class="['text-lg font-bold', expense === null ? 'text-muted-foreground' : 'text-red-600']">
          {{ formatCurrency(expense !== null ? Math.abs(expense) : null) }}
        </p>
      </div>

      <!-- Net -->
      <div 
        :class="[
          'flex flex-col gap-1 p-3 rounded-lg border',
          net === null ? 'bg-muted/10 border-muted/20' :
          net >= 0 
            ? 'bg-blue-500/10 border-blue-500/20' 
            : 'bg-orange-500/10 border-orange-500/20'
        ]"
      >
        <span 
          :class="[
            'text-xs font-medium',
            net === null ? 'text-muted-foreground' :
            net >= 0 ? 'text-blue-600' : 'text-orange-600'
          ]"
        >
          Net
        </span>
        <p 
          :class="[
            'text-lg font-bold',
            net === null ? 'text-muted-foreground' :
            net >= 0 ? 'text-blue-600' : 'text-orange-600'
          ]"
        >
          {{ formatCurrency(net) }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.wallet-summary {
  padding: 16px;
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}
</style>
