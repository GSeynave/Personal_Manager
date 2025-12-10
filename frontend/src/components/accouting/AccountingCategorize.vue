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
  <div class="space-y-4">
    <div class="flex items-center gap-3 mb-4">
      <Tags class="w-6 h-6 text-accounting" />
      <h2 class="text-xl font-semibold text-foreground">Categorize Transactions</h2>
    </div>

    <CardContent v-if="toCategorize && toCategorize.length > 0">
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
