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
import { TrendingUp, TrendingDown, DollarSign } from 'lucide-vue-next'
import type AccountingSummary from '@/model/accounting/AccountingSummary'

const props = defineProps<{
  accountingSummary?: AccountingSummary
  walletName?: string
  period: string
}>()

const emit = defineEmits<{
  periodChange: [value: string]
}>()

const balance = computed(() => {
  if (!props.accountingSummary) return null
  // Only use actual wallet balance from backend, don't calculate
  return props.accountingSummary.balance ?? null
})

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}
</script>

<template>
  <div class="wallet-summary space-y-4">
    <!-- Account Balance -->
    <div v-if="balance !== null" class="flex items-center justify-between pb-3 border-b border-border">
      <div class="flex items-center gap-3">
        <DollarSign class="w-6 h-6 text-accounting" />
        <div>
          <p class="text-sm text-muted-foreground">{{ walletName || 'Wallet' }} Balance</p>
          <p 
            :class="[
              'text-2xl font-bold',
              balance >= 0 ? 'text-green-600' : 'text-red-600'
            ]"
          >
            {{ formatCurrency(balance) }}
          </p>
        </div>
      </div>
    </div>

    <!-- Period Selector -->
    <div class="flex items-center gap-2">
      <span class="text-sm text-muted-foreground">Period:</span>
      <Select :model-value="period" @update:model-value="(val) => emit('periodChange', val as string)">
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="Select period" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="this-month">This Month</SelectItem>
          <SelectItem value="last-month">Last Month</SelectItem>
          <SelectItem value="this-year">This Year</SelectItem>
          <SelectItem value="custom">Custom Range</SelectItem>
        </SelectContent>
      </Select>
    </div>

    <!-- Income/Expenses/Net Summary -->
    <div v-if="accountingSummary" class="grid grid-cols-3 gap-3">
      <!-- Income -->
      <div class="flex flex-col gap-1 p-3 rounded-lg bg-green-500/10 border border-green-500/20">
        <div class="flex items-center gap-1">
          <TrendingUp class="w-4 h-4 text-green-600" />
          <span class="text-xs text-green-600 font-medium">Income</span>
        </div>
        <p class="text-lg font-bold text-green-600">
          {{ formatCurrency(accountingSummary.income) }}
        </p>
      </div>

      <!-- Expenses -->
      <div class="flex flex-col gap-1 p-3 rounded-lg bg-red-500/10 border border-red-500/20">
        <div class="flex items-center gap-1">
          <TrendingDown class="w-4 h-4 text-red-600" />
          <span class="text-xs text-red-600 font-medium">Expenses</span>
        </div>
        <p class="text-lg font-bold text-red-600">
          {{ formatCurrency(Math.abs(accountingSummary.expense)) }}
        </p>
      </div>

      <!-- Net -->
      <div 
        :class="[
          'flex flex-col gap-1 p-3 rounded-lg border',
          balance >= 0 
            ? 'bg-blue-500/10 border-blue-500/20' 
            : 'bg-orange-500/10 border-orange-500/20'
        ]"
      >
        <span 
          :class="[
            'text-xs font-medium',
            balance >= 0 ? 'text-blue-600' : 'text-orange-600'
          ]"
        >
          Net
        </span>
        <p 
          :class="[
            'text-lg font-bold',
            balance >= 0 ? 'text-blue-600' : 'text-orange-600'
          ]"
        >
          {{ formatCurrency(balance) }}
        </p>
      </div>
    </div>

    <div v-else class="text-sm text-muted-foreground">
      Loading summary...
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
