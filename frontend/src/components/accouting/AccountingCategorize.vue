<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AccountingService from '@/services/AccountingService'
import Transaction, { TransactionCategory, TransactionSubCategory } from '@/model/Transaction'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import CardContent from '../Dashboard/CardContent.vue'
import { Tags, Save } from 'lucide-vue-next'
import type UncategorizedTransactions from '@/model/UncategorizedTransactions'

const accountingService = new AccountingService()
const toCategorize = ref<UncategorizedTransactions>()

onMounted(() => {
  accountingService.getTransactionsToCategorize().then((data) => {
    toCategorize.value = data
  })
})

const categories = Object.values(TransactionCategory)
  .filter((k) => isNaN(Number(k)))
  .map((cat) => ({ label: cat, value: cat }))
const subCategories = Object.values(TransactionSubCategory)
  .filter((k) => isNaN(Number(k)))
  .map((subCat) => ({ label: subCat, value: subCat }))

function updateToCategorize() {
  accountingService
    .updateTransaction(toCategorize.value?.transactions || [])
    .then(() => {
      console.log('Transactions updated successfully')
      accountingService.getTransactionsToCategorize().then((data) => {
        toCategorize.value = data
      })
    })
    .catch((err) => {
      console.error('Error updating transactions:', err)
    })
}

function isPositive(amount: number): boolean {
  return amount >= 0
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center gap-3 mb-4">
      <Tags class="w-6 h-6 text-accounting" />
      <h2 class="text-xl font-semibold text-foreground">Categorize Transactions</h2>
    </div>

    <CardContent v-if="toCategorize && toCategorize.transactions.length > 0">
      <div class="space-y-4">

      <div class="border rounded-md">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Category</TableHead>
              <TableHead>Subcategory</TableHead>
              <TableHead>Custom Category</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <!-- TODO turn this into a data table ? for pagination -->
            <TableRow v-for="(transaction, idx) in toCategorize.transactions" :key="idx">
              <TableCell>{{ transaction.date }}</TableCell>
              <TableCell>{{ transaction.description }}</TableCell>
              <TableCell :class="isPositive(transaction.amount) ? 'text-green-400' : 'text-red-400'">{{ transaction.amount }}</TableCell>
              <TableCell>
                <Select v-model="transaction.category">
                  <SelectTrigger class="w-[180px]">
                    <SelectValue placeholder="Select category" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem v-for="cat in categories" :key="cat.value" :value="cat.value">
                      {{ cat.label }}
                    </SelectItem>
                  </SelectContent>
                </Select>
              </TableCell>
              <TableCell>
                <Select v-model="transaction.subCategory">
                  <SelectTrigger class="w-[180px]">
                    <SelectValue placeholder="Select subcategory" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem v-for="subCat in subCategories" :key="subCat.value" :value="subCat.value">
                      {{ subCat.label }}
                    </SelectItem>
                  </SelectContent>
                </Select>
              </TableCell>
              <TableCell>
                <Input v-model="transaction.customCategory" type="text" class="w-full" />
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>

        <Button @click="updateToCategorize" class="w-full">
          <Save class="w-4 h-4 mr-2 text-accounting" />
          Save Categories
        </Button>
      </div>
    </CardContent>
    
    <CardContent v-else>
      <div class="text-center text-muted-foreground py-8">
        <p>No transactions to categorize</p>
      </div>
    </CardContent>
  </div>
</template>

<style scoped></style>
