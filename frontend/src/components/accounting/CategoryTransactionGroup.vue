<script setup lang="ts">
import { ref } from 'vue'
import { ChevronDown } from 'lucide-vue-next'
import type Transaction from '@/model/accounting/Transaction'

interface CategoryGroup {
  categoryName: string
  categoryIcon: string | null
  totalAmount: number
  transactions: Transaction[]
}

defineProps<{
  categoryGroup: CategoryGroup
}>()

const isExpanded = ref(false)

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}

const formatDate = (date: Date | string) => {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return dateObj.toLocaleDateString('en-US', { 
    month: '2-digit', 
    day: '2-digit'
  })
}

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}
</script>

<template>
  <div class="border-b border-border last:border-b-0">
    <!-- Category Header -->
    <button
      @click="toggleExpand"
      class="w-full flex items-center justify-between py-3 px-2 hover:bg-muted/30 transition-colors group"
    >
      <div class="flex items-center gap-3 flex-1">
        <!-- Expand/Collapse Icon -->
        <ChevronDown 
          :class="[
            'w-4 h-4 transition-transform text-muted-foreground',
            isExpanded ? 'rotate-0' : '-rotate-90'
          ]"
        />
        
        <!-- Category Icon and Name -->
        <div class="flex items-center gap-2">
          <span v-if="categoryGroup.categoryIcon" class="text-lg">
            {{ categoryGroup.categoryIcon }}
          </span>
          <span class="font-medium">{{ categoryGroup.categoryName }}</span>
        </div>
        
        <!-- Dotted line separator -->
        <div class="flex-1 border-b border-dotted border-muted-foreground/30 mx-2 min-w-[20px]"></div>
      </div>
      
      <!-- Total Amount -->
      <div 
        class="font-semibold text-right"
        :class="categoryGroup.totalAmount >= 0 ? 'text-green-600' : 'text-red-600'"
      >
        {{ categoryGroup.totalAmount >= 0 ? '+' : '' }}{{ formatCurrency(Math.abs(categoryGroup.totalAmount)) }}
      </div>
    </button>
    
    <!-- Transactions List (Expandable) -->
    <div
      v-if="isExpanded"
      class="pb-2 space-y-1"
    >
      <div
        v-for="transaction in categoryGroup.transactions"
        :key="transaction.id"
        class="flex items-center justify-between py-2 px-2 ml-7 text-sm hover:bg-muted/20 rounded transition-colors"
      >
        <div class="flex items-center gap-2 text-muted-foreground">
          <span class="text-muted-foreground/50">└─</span>
          <span>{{ formatDate(transaction.date) }}</span>
          <span>-</span>
          <span class="text-foreground">
            {{ transaction.customLabel || transaction.importLabel || 'Unnamed transaction' }}
          </span>
        </div>
        
        <div 
          class="font-medium text-right"
          :class="transaction.amount >= 0 ? 'text-green-600' : 'text-red-600'"
        >
          {{ transaction.amount >= 0 ? '+' : '' }}{{ formatCurrency(Math.abs(transaction.amount)) }}
        </div>
      </div>
    </div>
  </div>
</template>
