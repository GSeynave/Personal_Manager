<script setup lang="ts">
import { ref } from 'vue'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { AlertCircle } from 'lucide-vue-next'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'
import type { TransactionCategoryDTO } from '@/model/accounting/TransactionCategory'

const props = defineProps<{
  open: boolean
  category: TransactionCategoryDTO | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'category-deleted': [categoryId: number]
}>()

const categoryService = new TransactionCategoryService()
const loading = ref(false)
const error = ref<string | null>(null)

async function confirmDelete() {
  if (!props.category) return

  loading.value = true
  error.value = null

  try {
    await categoryService.deleteCategory(props.category.id)
    emit('category-deleted', props.category.id)
    emit('update:open', false)
  } catch (err: any) {
    console.error('Error deleting category:', err)
    error.value = err.response?.data?.message || 'Failed to delete category'
  } finally {
    loading.value = false
  }
}

function handleOpenChange(open: boolean) {
  if (!open) {
    error.value = null
  }
  emit('update:open', open)
}
</script>

<template>
  <Dialog :open="open" @update:open="handleOpenChange">
    <DialogContent class="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle class="flex items-center gap-2">
          <AlertCircle class="w-5 h-5 text-destructive" />
          Delete Category
        </DialogTitle>
        <DialogDescription>
          Are you sure you want to delete this category?
        </DialogDescription>
      </DialogHeader>

      <div class="py-4 space-y-4">
        <!-- Category Info -->
        <div v-if="category" class="p-4 bg-muted rounded-md">
          <div class="flex items-center gap-3">
            <span class="text-2xl">{{ category.icon || '📁' }}</span>
            <div>
              <div class="font-medium">{{ category.title }}</div>
              <div v-if="category.description" class="text-sm text-muted-foreground">
                {{ category.description }}
              </div>
            </div>
          </div>
        </div>

        <!-- Warning -->
        <div class="bg-destructive/10 text-destructive text-sm p-3 rounded-md">
          <p class="font-medium">⚠️ Warning</p>
          <p class="mt-1">
            This action cannot be undone. 
            <span v-if="category?.subCategories && category.subCategories.length > 0">
              This category has {{ category.subCategories.length }} subcategories that must be deleted first.
            </span>
          </p>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="bg-destructive/10 text-destructive text-sm p-3 rounded-md">
          {{ error }}
        </div>
      </div>

      <DialogFooter>
        <Button
          variant="outline"
          @click="handleOpenChange(false)"
          :disabled="loading"
        >
          Cancel
        </Button>
        <Button
          variant="destructive"
          @click="confirmDelete"
          :disabled="loading"
        >
          {{ loading ? 'Deleting...' : 'Delete Category' }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
