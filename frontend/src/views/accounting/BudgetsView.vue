<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import Breadcrumb from '@/components/core/Breadcrumb.vue'
import { PiggyBank } from 'lucide-vue-next'
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
      { label: 'Budgets' }
    ]" />

    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <PiggyBank class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Budgets</h1>
      </div>
      <p class="text-sm text-accounting">
        Set and track your spending budgets
      </p>
    </div>

    <!-- Coming Soon Message -->
    <div class="flex flex-col items-center justify-center py-20 space-y-4">
      <PiggyBank class="w-20 h-20 text-muted-foreground/30" />
      <h2 class="text-2xl font-semibold text-muted-foreground">Coming Soon</h2>
      <p class="text-muted-foreground text-center max-w-md">
        Set monthly budgets for categories, track spending against limits, and receive alerts when approaching budget thresholds.
      </p>
    </div>
  </main>
</template>

<style scoped>
.page-header {
  padding-left: 12px;
}
</style>
