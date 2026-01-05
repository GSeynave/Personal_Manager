<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import Breadcrumb from '@/components/core/Breadcrumb.vue'
import AccountingCategorize from '@/components/accounting/AccountingCategorize.vue'
import { Tags } from 'lucide-vue-next'
</script>

<template>
  <main :style="{
    '--module-color': moduleColor('accounting'),
    '--module-color-rgb': hexToRgbStr(moduleColor('accounting')),
    '--module-tint-alpha': moduleTintAlpha(moduleColor('accounting')),
  }" class="p-6 min-h-screen space-y-6">
    <!-- Breadcrumb -->
    <Breadcrumb :items="[
      { label: 'Finance & Resources', to: '/finance/overview' },
      { label: 'Categorize' }
    ]" />

    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Tags class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Categorize Transactions</h1>
      </div>
      <p class="text-sm text-accounting">
        Assign categories to uncategorized transactions
      </p>
    </div>

    <!-- Categorize Component -->
    <AccountingCategorize />
  </main>
</template>

<style scoped>
.page-header {
  padding-left: 12px;
}
</style>
