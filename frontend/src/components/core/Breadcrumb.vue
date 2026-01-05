<script setup lang="ts">
import { ChevronRight } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'

interface BreadcrumbItem {
  label: string
  to?: string
}

defineProps<{
  items: BreadcrumbItem[]
}>()
</script>

<template>
  <nav class="flex items-center gap-2 text-sm text-muted-foreground mb-6">
    <template v-for="(item, index) in items" :key="index">
      <ChevronRight v-if="index > 0" class="w-4 h-4" />
      <RouterLink
        v-if="item.to"
        :to="item.to"
        class="hover:text-foreground transition-colors"
      >
        {{ item.label }}
      </RouterLink>
      <span
        v-else
        class="text-foreground font-medium"
      >
        {{ item.label }}
      </span>
    </template>
  </nav>
</template>
