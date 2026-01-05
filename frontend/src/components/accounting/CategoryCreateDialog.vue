<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'
import EmojiPicker from '@/components/accounting/EmojiPicker.vue'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'
import type { 
  TransactionCategoryDTO, 
  CreateTransactionCategoryRequest 
} from '@/model/accounting/TransactionCategory'

const props = defineProps<{
  open: boolean
  categories: TransactionCategoryDTO[]
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'category-created': [category: TransactionCategoryDTO]
}>()

const categoryService = new TransactionCategoryService()
const loading = ref(false)
const error = ref<string | null>(null)

const formData = ref<CreateTransactionCategoryRequest>({
  title: '',
  icon: undefined,
  description: '',
  expectedAmount: undefined,
  parentCategoryId: undefined,
})

const parentCategories = computed(() => {
  // Only show root categories (no parent) as potential parents
  return props.categories.filter(cat => cat.parentCategoryId === null)
})

const isFormValid = computed(() => {
  return formData.value.title.trim().length > 0
})

async function createCategory() {
  if (!isFormValid.value) return

  loading.value = true
  error.value = null

  try {
    const request: CreateTransactionCategoryRequest = {
      title: formData.value.title.trim(),
      icon: formData.value.icon || undefined,
      description: formData.value.description?.trim() || undefined,
      expectedAmount: formData.value.expectedAmount || undefined,
      parentCategoryId: formData.value.parentCategoryId || undefined,
    }

    const newCategory = await categoryService.createCategory(request)
    emit('category-created', newCategory)
    emit('update:open', false)
    resetForm()
  } catch (err: any) {
    console.error('Error creating category:', err)
    error.value = err.response?.data?.message || 'Failed to create category'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  formData.value = {
    title: '',
    icon: undefined,
    description: '',
    expectedAmount: undefined,
    parentCategoryId: undefined,
  }
  error.value = null
}

function handleOpenChange(open: boolean) {
  if (!open) {
    resetForm()
  }
  emit('update:open', open)
}
</script>

<template>
  <Dialog :open="open" @update:open="handleOpenChange">
    <DialogContent class="sm:max-w-[500px]">
      <DialogHeader>
        <DialogTitle>Create New Category</DialogTitle>
        <DialogDescription>
          Add a new transaction category. You can create a parent category or a subcategory.
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-4 py-4">
        <!-- Error Message -->
        <div v-if="error" class="bg-destructive/10 text-destructive text-sm p-3 rounded-md">
          {{ error }}
        </div>

        <!-- Icon and Title -->
        <div class="space-y-2">
          <Label for="category-title">
            Category <span class="text-destructive">*</span>
          </Label>
          <div class="flex gap-2">
            <EmojiPicker v-model="formData.icon" />
            <Input
              id="category-title"
              v-model="formData.title"
              placeholder="e.g., Groceries"
              :disabled="loading"
              class="flex-1"
            />
          </div>
          <p class="text-xs text-muted-foreground">
            Click the icon to choose an emoji
          </p>
        </div>

        <!-- Description -->
        <div class="space-y-2">
          <Label for="category-description">Description</Label>
          <Textarea
            id="category-description"
            v-model="formData.description"
            placeholder="Optional description..."
            :disabled="loading"
            rows="3"
          />
        </div>

        <!-- Expected Amount -->
        <div class="space-y-2">
          <Label for="category-amount">Expected Budget (€)</Label>
          <Input
            id="category-amount"
            v-model.number="formData.expectedAmount"
            type="number"
            step="0.01"
            placeholder="e.g., 500.00"
            :disabled="loading"
          />
        </div>

        <!-- Parent Category -->
        <div class="space-y-2">
          <Label for="parent-category">Parent Category (Optional)</Label>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <input
                id="no-parent"
                type="radio"
                :checked="!formData.parentCategoryId"
                @change="formData.parentCategoryId = undefined"
                :disabled="loading"
                class="h-4 w-4"
              />
              <Label for="no-parent" class="font-normal cursor-pointer">
                None (Root Category)
              </Label>
            </div>
            <div v-for="category in parentCategories" :key="category.id" class="flex items-center gap-2">
              <input
                :id="`parent-${category.id}`"
                type="radio"
                :checked="formData.parentCategoryId === category.id"
                @change="formData.parentCategoryId = category.id"
                :disabled="loading"
                class="h-4 w-4"
              />
              <Label :for="`parent-${category.id}`" class="font-normal cursor-pointer">
                {{ category.title }}
              </Label>
            </div>
          </div>
          <p class="text-xs text-muted-foreground">
            Select a parent category to create a subcategory
          </p>
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
          @click="createCategory"
          :disabled="!isFormValid || loading"
        >
          {{ loading ? 'Creating...' : 'Create Category' }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
