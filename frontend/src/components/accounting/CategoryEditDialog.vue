<script setup lang="ts">
import { ref, computed, watch } from 'vue'
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
  UpdateTransactionCategoryRequest 
} from '@/model/accounting/TransactionCategory'

const props = defineProps<{
  open: boolean
  category: TransactionCategoryDTO | null
  categories: TransactionCategoryDTO[]
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'category-updated': [category: TransactionCategoryDTO]
}>()

const categoryService = new TransactionCategoryService()
const loading = ref(false)
const error = ref<string | null>(null)

const formData = ref<UpdateTransactionCategoryRequest>({
  title: '',
  icon: undefined,
  description: '',
  expectedAmount: undefined,
  parentCategoryId: undefined,
})

const parentCategories = computed(() => {
  if (!props.category) return []
  
  // Filter out the current category and its descendants to prevent circular references
  return props.categories.filter(cat => 
    cat.parentCategoryId === null && 
    cat.id !== props.category!.id
  )
})

const isFormValid = computed(() => {
  return formData.value.title && formData.value.title.trim().length > 0
})

// Load category data when dialog opens
watch(() => props.category, (category) => {
  if (category) {
    formData.value = {
      title: category.title,
      icon: category.icon || undefined,
      description: category.description || '',
      expectedAmount: category.expectedAmount || undefined,
      parentCategoryId: category.parentCategoryId || undefined,
    }
  }
}, { immediate: true })

async function updateCategory() {
  if (!isFormValid.value || !props.category) return

  loading.value = true
  error.value = null

  try {
    const request: UpdateTransactionCategoryRequest = {
      title: formData.value.title?.trim() || undefined,
      icon: formData.value.icon || undefined,
      description: formData.value.description?.trim() || undefined,
      expectedAmount: formData.value.expectedAmount || undefined,
      parentCategoryId: formData.value.parentCategoryId || undefined,
    }

    const updatedCategory = await categoryService.updateCategory(props.category.id, request)
    emit('category-updated', updatedCategory)
    emit('update:open', false)
  } catch (err: any) {
    console.error('Error updating category:', err)
    error.value = err.response?.data?.message || 'Failed to update category'
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
    <DialogContent class="sm:max-w-[500px]">
      <DialogHeader>
        <DialogTitle>Edit Category</DialogTitle>
        <DialogDescription>
          Update the category details below.
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-4 py-4">
        <!-- Error Message -->
        <div v-if="error" class="bg-destructive/10 text-destructive text-sm p-3 rounded-md">
          {{ error }}
        </div>

        <!-- Icon and Title -->
        <div class="space-y-2">
          <Label for="edit-category-title">
            Category <span class="text-destructive">*</span>
          </Label>
          <div class="flex gap-2">
            <EmojiPicker v-model="formData.icon" />
            <Input
              id="edit-category-title"
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
          <Label for="edit-category-description">Description</Label>
          <Textarea
            id="edit-category-description"
            v-model="formData.description"
            placeholder="Optional description..."
            :disabled="loading"
            rows="3"
          />
        </div>

        <!-- Expected Amount -->
        <div class="space-y-2">
          <Label for="edit-category-amount">Expected Budget (€)</Label>
          <Input
            id="edit-category-amount"
            v-model.number="formData.expectedAmount"
            type="number"
            step="0.01"
            placeholder="e.g., 500.00"
            :disabled="loading"
          />
        </div>

        <!-- Parent Category -->
        <div class="space-y-2">
          <Label for="edit-parent-category">Parent Category (Optional)</Label>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <input
                id="edit-no-parent"
                type="radio"
                :checked="!formData.parentCategoryId"
                @change="formData.parentCategoryId = undefined"
                :disabled="loading"
                class="h-4 w-4"
              />
              <Label for="edit-no-parent" class="font-normal cursor-pointer">
                None (Root Category)
              </Label>
            </div>
            <div v-for="cat in parentCategories" :key="cat.id" class="flex items-center gap-2">
              <input
                :id="`edit-parent-${cat.id}`"
                type="radio"
                :checked="formData.parentCategoryId === cat.id"
                @change="formData.parentCategoryId = cat.id"
                :disabled="loading"
                class="h-4 w-4"
              />
              <Label :for="`edit-parent-${cat.id}`" class="font-normal cursor-pointer">
                {{ cat.title }}
              </Label>
            </div>
          </div>
          <p class="text-xs text-muted-foreground">
            Select a parent category to make this a subcategory
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
          @click="updateCategory"
          :disabled="!isFormValid || loading"
        >
          {{ loading ? 'Updating...' : 'Update Category' }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
