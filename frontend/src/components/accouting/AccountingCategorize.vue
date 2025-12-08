<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AccountingService from '@/services/AccountingService'
import Transaction, { TransactionCategory, TransactionSubCategory } from '@/model/Transaction'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import CardContent from '../Dashboard/CardContent.vue'

const accountingService = new AccountingService()
const toCategorize = ref<Transaction[]>([])

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
    .updateTransaction(toCategorize.value)
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
</script>

<template>
  <CardContent :title="'Transaction to categorize'">
    <div class="space-y-4" v-if="toCategorize && toCategorize.length > 0">
      <h3 class="text-lg font-semibold">Transactions to categorize</h3>

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
            <TableRow v-for="(transaction, idx) in toCategorize" :key="idx">
              <TableCell>{{ transaction.date }}</TableCell>
              <TableCell>{{ transaction.description }}</TableCell>
              <TableCell>{{ transaction.amount }}</TableCell>
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

      <Button @click="updateToCategorize" class="w-full">Save Categories</Button>
    </div>
    <div v-else class="text-center text-muted-foreground py-8">
      <p>No transactions to categorize</p>
    </div>
  </CardContent>
</template>

<style scoped></style>
