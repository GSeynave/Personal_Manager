<script setup lang="ts">
import { ref } from 'vue'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { ChevronDown, ChevronRight } from 'lucide-vue-next'
import CardContent from '../Dashboard/CardContent.vue'

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
</script>

<template>
  <CardContent :title="'Transactions'">
    <div class="table-wrapper border rounded-md">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead class="w-12"></TableHead>
            <TableHead>Category</TableHead>
            <TableHead>Expense</TableHead>
            <TableHead>Percent</TableHead>
            <TableHead>Max Expected</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="summary in props.transactionSummary" :key="summary.category">
            <TableRow class="cursor-pointer hover:bg-accent/50" @click="toggleCategory(summary.category)">
              <TableCell>
                <Button variant="ghost" size="icon" class="h-6 w-6">
                  <ChevronDown v-if="isExpanded(summary.category)" class="h-4 w-4" />
                  <ChevronRight v-else class="h-4 w-4" />
                </Button>
              </TableCell>
              <TableCell class="font-medium">{{ summary.category }}</TableCell>
              <TableCell>{{ summary.expense }}</TableCell>
              <TableCell>{{ summary.percent }}</TableCell>
              <TableCell>{{ summary.maxExpected }}</TableCell>
            </TableRow>
            <TableRow v-if="isExpanded(summary.category)" class="bg-muted/50">
              <TableCell colspan="5" class="p-0">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead class="pl-16">Description</TableHead>
                      <TableHead>Amount</TableHead>
                      <TableHead>Date</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    <TableRow v-for="(transaction, idx) in summary.transactions" :key="idx" class="bg-muted/30">
                      <TableCell class="pl-16">{{ transaction.customCategory }}</TableCell>
                      <TableCell>{{ transaction.amount }}</TableCell>
                      <TableCell>{{ transaction.date }}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
      </Table>
    </div>
  </CardContent>
</template>

<style scoped>
.table-wrapper {
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}
</style>
