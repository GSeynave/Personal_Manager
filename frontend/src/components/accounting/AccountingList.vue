<script setup lang="ts">
import { ref } from 'vue'
import { ChevronDown, ChevronRight } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'

const props = defineProps(['transactionSummary'])
const expandedCategories = ref<Set<string>>(new Set())

function toggleCategory(category: string) {
  if (expandedCategories.value.has(category)) {
    expandedCategories.value.delete(category)
  } else {
    expandedCategories.value.add(category)
  }
}

function isExpanded(category: string) {
  return expandedCategories.value.has(category)
}

function isPositive(amount: number): boolean {
  return amount >= 0
}

function formatCurrency(amount: number): string {
  const formatted = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(Math.abs(amount))
  
  return amount >= 0 ? `+${formatted}` : `-${formatted}`
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    month: '2-digit',
    day: '2-digit',
  }).format(date)
}
</script>

<template>
  <div class="transactions-container">
    <div v-if="!props.transactionSummary || props.transactionSummary.length === 0" 
         class="text-center py-12 text-muted-foreground">
      <p>No transactions found for this period</p>
    </div>

    <div v-else class="space-y-1">
      <div 
        v-for="summary in props.transactionSummary" 
        :key="summary.category"
        class="category-group"
      >
        <!-- Category Header -->
        <div 
          class="category-header group cursor-pointer"
          @click="toggleCategory(summary.category)"
        >
          <div class="flex items-center gap-2 flex-1 min-w-0">
            <Button variant="ghost" size="icon" class="h-6 w-6 flex-shrink-0">
              <ChevronDown v-if="isExpanded(summary.category)" class="h-4 w-4" />
              <ChevronRight v-else class="h-4 w-4" />
            </Button>
            
            <span class="font-medium text-foreground truncate">
              {{ summary.category }}
            </span>
          </div>
          
          <div class="category-amount flex-shrink-0">
            <span 
              :class="[
                'font-bold text-sm',
                isPositive(summary.expense) ? 'text-green-600' : 'text-red-600'
              ]"
            >
              {{ formatCurrency(summary.expense) }}
            </span>
          </div>
        </div>

        <!-- Transaction Items (Expanded) -->
        <div 
          v-if="isExpanded(summary.category)"
          class="transaction-items"
        >
          <div 
            v-for="(transaction, idx) in summary.transactions" 
            :key="idx"
            class="transaction-item group"
          >
            <div class="flex items-center gap-2 flex-1 min-w-0 pl-10">
              <span class="text-xs text-muted-foreground flex-shrink-0">
                {{ formatDate(transaction.date) }}
              </span>
              <span class="text-sm text-foreground truncate">
                {{ transaction.customCategory || transaction.description }}
              </span>
            </div>
            
            <span 
              :class="[
                'text-sm font-medium flex-shrink-0',
                isPositive(transaction.amount) ? 'text-green-600' : 'text-red-600'
              ]"
            >
              {{ formatCurrency(transaction.amount) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.transactions-container {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  overflow: hidden;
}

.category-group {
  border-bottom: 1px solid hsl(var(--border));
}

.category-group:last-child {
  border-bottom: none;
}

.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px;
  transition: background-color 0.15s;
}

.category-header:hover {
  background: hsl(var(--accent) / 0.5);
}

.category-amount {
  display: flex;
  align-items: center;
  gap: 8px;
}

.transaction-items {
  background: hsl(var(--muted) / 0.3);
  border-top: 1px solid hsl(var(--border));
}

.transaction-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 16px;
  border-bottom: 1px solid hsl(var(--border) / 0.5);
  transition: background-color 0.15s;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-item:hover {
  background: hsl(var(--accent) / 0.3);
}
</style>
