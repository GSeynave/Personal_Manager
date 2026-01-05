<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { DollarSign, Plus, Search, Pencil, Trash2, GripVertical, Tag } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import Breadcrumb from '@/components/core/Breadcrumb.vue'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'
import CategoryCreateDialog from '@/components/accounting/CategoryCreateDialog.vue'
import CategoryEditDialog from '@/components/accounting/CategoryEditDialog.vue'
import CategoryDeleteDialog from '@/components/accounting/CategoryDeleteDialog.vue'
import type { TransactionCategoryDTO } from '@/model/accounting/TransactionCategory'

const categoryService = new TransactionCategoryService()

const categories = ref<TransactionCategoryDTO[]>([])
const allCategories = ref<TransactionCategoryDTO[]>([])
const searchQuery = ref('')
const loading = ref(false)
const createDialogOpen = ref(false)
const editDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const selectedCategory = ref<TransactionCategoryDTO | null>(null)

// Drag and drop state
const draggedCategory = ref<TransactionCategoryDTO | null>(null)
const draggedSubCategory = ref<TransactionCategoryDTO | null>(null)
const dragOverCategoryId = ref<number | null>(null)

// Filtered categories based on search
const filteredCategories = computed(() => {
  if (!searchQuery.value) return categories.value
  
  const query = searchQuery.value.toLowerCase()
  return categories.value
    .map(category => ({
      ...category,
      subCategories: category.subCategories?.filter(sub =>
        sub.title.toLowerCase().includes(query)
      ) || null
    }))
    .filter(category =>
      category.title.toLowerCase().includes(query) ||
      (category.subCategories && category.subCategories.length > 0)
    )
})

async function loadCategories() {
  loading.value = true
  try {
    categories.value = await categoryService.getRootCategories()
    allCategories.value = await categoryService.getAllCategories()
  } catch (error) {
    console.error('Error loading categories:', error)
  } finally {
    loading.value = false
  }
}

function getCategoryIcon(category: TransactionCategoryDTO): string {
  return category.icon || '📁'
}

function getUsageCount(categoryId: number): number {
  // TODO: Implement actual usage tracking from transactions
  // For now, return mock data
  return Math.floor(Math.random() * 50)
}

function handleCreateCategory() {
  createDialogOpen.value = true
}

function handleEditCategory(category: TransactionCategoryDTO) {
  selectedCategory.value = category
  editDialogOpen.value = true
}

function handleDeleteCategory(category: TransactionCategoryDTO) {
  selectedCategory.value = category
  deleteDialogOpen.value = true
}

function handleCategoryCreated(category: TransactionCategoryDTO) {
  loadCategories()
}

function handleCategoryUpdated(category: TransactionCategoryDTO) {
  loadCategories()
}

function handleCategoryDeleted(categoryId: number) {
  loadCategories()
}

// Drag and drop handlers
function handleDragStart(category: TransactionCategoryDTO, isSubCategory: boolean = false) {
  if (isSubCategory) {
    draggedSubCategory.value = category
  } else {
    draggedCategory.value = category
  }
}

function handleDragOver(event: DragEvent, categoryId: number) {
  event.preventDefault()
  dragOverCategoryId.value = categoryId
}

function handleDragLeave() {
  dragOverCategoryId.value = null
}

function handleDrop(event: DragEvent, targetCategory: TransactionCategoryDTO) {
  event.preventDefault()
  dragOverCategoryId.value = null
  
  const draggedItem = draggedCategory.value || draggedSubCategory.value
  if (!draggedItem || draggedItem.id === targetCategory.id) {
    draggedCategory.value = null
    draggedSubCategory.value = null
    return
  }

  // Create a new array to avoid mutating the original
  const newCategories = [...categories.value]

  // Case 1: Dragging a parent category
  if (draggedCategory.value) {
    const draggedIndex = newCategories.findIndex(c => c.id === draggedItem.id)
    const targetIndex = newCategories.findIndex(c => c.id === targetCategory.id)
    
    if (draggedIndex !== -1 && targetIndex !== -1) {
      // Remove from old position
      const [movedCategory] = newCategories.splice(draggedIndex, 1)
      // Insert at new position
      newCategories.splice(targetIndex, 0, movedCategory)
      categories.value = newCategories
    }
  }
  
  // Case 2: Dragging a subcategory
  else if (draggedSubCategory.value) {
    // Find parent of dragged subcategory
    let sourceParentIndex = -1
    let sourceSubIndex = -1
    
    for (let i = 0; i < newCategories.length; i++) {
      const subIndex = newCategories[i].subCategories?.findIndex(s => s.id === draggedItem.id) ?? -1
      if (subIndex !== -1) {
        sourceParentIndex = i
        sourceSubIndex = subIndex
        break
      }
    }
    
    // Find target parent (if dropping on a subcategory, use its parent; if dropping on parent, use that)
    let targetParentIndex = -1
    let targetSubIndex = -1
    
    // Check if target is a parent category
    targetParentIndex = newCategories.findIndex(c => c.id === targetCategory.id)
    
    // If not found as parent, search in subcategories
    if (targetParentIndex === -1) {
      for (let i = 0; i < newCategories.length; i++) {
        const subIndex = newCategories[i].subCategories?.findIndex(s => s.id === targetCategory.id) ?? -1
        if (subIndex !== -1) {
          targetParentIndex = i
          targetSubIndex = subIndex
          break
        }
      }
    }
    
    if (sourceParentIndex !== -1 && targetParentIndex !== -1) {
      // Remove from source
      const [movedSub] = newCategories[sourceParentIndex].subCategories!.splice(sourceSubIndex, 1)
      
      // Add to target
      if (targetSubIndex === -1) {
        // Dropped on a parent category - add to end of its subcategories
        if (!newCategories[targetParentIndex].subCategories) {
          newCategories[targetParentIndex].subCategories = []
        }
        newCategories[targetParentIndex].subCategories!.push(movedSub)
      } else {
        // Dropped on a subcategory - insert at that position
        newCategories[targetParentIndex].subCategories!.splice(targetSubIndex, 0, movedSub)
      }
      
      categories.value = newCategories
    }
  }
  
  draggedCategory.value = null
  draggedSubCategory.value = null
  
  // Persist the new order to the backend
  saveOrder()
}

function handleDragEnd() {
  draggedCategory.value = null
  draggedSubCategory.value = null
  dragOverCategoryId.value = null
}

async function saveOrder() {
  try {
    // Collect all category IDs in their current order
    const categoryIds: number[] = []
    
    for (const category of categories.value) {
      categoryIds.push(category.id)
      if (category.subCategories) {
        categoryIds.push(...category.subCategories.map(sub => sub.id))
      }
    }
    
    await transactionCategoryService.reorderCategories(categoryIds)
  } catch (error) {
    console.error('Failed to save category order:', error)
    // Reload categories to revert to server state
    await loadCategories()
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<template>
  <main class="p-6 min-h-screen space-y-6">
    <!-- Breadcrumb -->
    <Breadcrumb :items="[
      { label: 'Finance & Resources', to: '/finance/overview' },
      { label: 'Categories' }
    ]" />

    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <Tag class="w-8 h-8 text-accounting" />
        <h1 class="text-3xl font-bold text-foreground">Categories</h1>
      </div>
      <p class="text-sm text-accounting">
        Manage your transaction categories and subcategories
      </p>
    </div>

    <!-- Actions Bar -->
    <div class="flex items-center gap-3">
      <Button @click="handleCreateCategory" class="gap-2">
        <Plus class="w-4 h-4" />
        New Category
      </Button>
      
      <div class="relative flex-1 max-w-sm">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Search categories..."
          class="pl-9"
        />
      </div>
    </div>

    <!-- Categories List -->
    <div class="space-y-3">
      <div v-if="loading" class="text-center py-12 text-muted-foreground">
        Loading categories...
      </div>

      <div v-else-if="filteredCategories.length === 0" class="text-center py-12 text-muted-foreground">
        <p v-if="searchQuery">No categories found matching "{{ searchQuery }}"</p>
        <p v-else>No categories yet. Create your first category to get started!</p>
      </div>

      <div v-else class="space-y-2">
        <!-- Parent Category -->
        <div 
          v-for="category in filteredCategories" 
          :key="category.id"
          class="border rounded-lg bg-card"
          :class="{ 'ring-2 ring-primary': dragOverCategoryId === category.id }"
        >
          <!-- Parent Category Row -->
          <div 
            class="flex items-center gap-3 p-4 group/parent"
            draggable="true"
            @dragstart="handleDragStart(category)"
            @dragover="handleDragOver($event, category.id)"
            @dragleave="handleDragLeave"
            @drop="handleDrop($event, category)"
            @dragend="handleDragEnd"
          >
            <!-- Drag Handle -->
            <GripVertical class="w-5 h-5 text-muted-foreground cursor-grab opacity-0 group-hover/parent:opacity-100 transition-opacity" />
            
            <!-- Icon & Title -->
            <div class="flex items-center gap-3 flex-1 min-w-0">
              <span class="text-2xl">{{ getCategoryIcon(category) }}</span>
              <div class="flex-1 min-w-0">
                <div class="font-semibold text-lg">{{ category.title }}</div>
                <div v-if="category.description" class="text-sm text-muted-foreground truncate">
                  {{ category.description }}
                </div>
              </div>
            </div>

            <!-- Usage Count -->
            <Badge variant="secondary" class="flex-shrink-0">
              Used {{ getUsageCount(category.id) }} times
            </Badge>

            <!-- Actions -->
            <div class="flex items-center gap-1 flex-shrink-0">
              <Button
                @click="handleEditCategory(category)"
                variant="ghost"
                size="sm"
                class="gap-2"
              >
                <Pencil class="w-4 h-4 text-muted-foreground" />
                Edit
              </Button>
              <Button
                @click="handleDeleteCategory(category)"
                variant="ghost"
                size="sm"
                class="gap-2"
              >
                <Trash2 class="w-4 h-4 text-destructive" />
                Delete
              </Button>
            </div>
          </div>

          <!-- Subcategories -->
          <div 
            v-if="category.subCategories && category.subCategories.length > 0"
            class="border-t bg-muted/30"
          >
            <div
              v-for="subCategory in category.subCategories"
              :key="subCategory.id"
              class="flex items-center gap-3 p-3 pl-16 group/sub border-b last:border-b-0 hover:bg-muted/50 transition-colors"
              :class="{ 'ring-2 ring-primary': dragOverCategoryId === subCategory.id }"
              draggable="true"
              @dragstart="handleDragStart(subCategory, true)"
              @dragover="handleDragOver($event, subCategory.id)"
              @dragleave="handleDragLeave"
              @drop="handleDrop($event, subCategory)"
              @dragend="handleDragEnd"
            >
              <!-- Drag Handle -->
              <GripVertical class="w-4 h-4 text-muted-foreground cursor-grab opacity-0 group-hover/sub:opacity-100 transition-opacity" />
              
              <!-- Icon & Title -->
              <div class="flex items-center gap-3 flex-1 min-w-0">
                <span class="text-xl">{{ getCategoryIcon(subCategory) }}</span>
                <div class="flex-1 min-w-0">
                  <div class="font-medium">{{ subCategory.title }}</div>
                  <div v-if="subCategory.description" class="text-xs text-muted-foreground truncate">
                    {{ subCategory.description }}
                  </div>
                </div>
              </div>

              <!-- Usage Count -->
              <Badge variant="outline" class="flex-shrink-0 text-xs">
                Used {{ getUsageCount(subCategory.id) }} times
              </Badge>

              <!-- Actions -->
              <div class="flex items-center gap-1 flex-shrink-0 opacity-0 group-hover/sub:opacity-100 transition-opacity">
                <Button
                  @click="handleEditCategory(subCategory)"
                  variant="ghost"
                  size="sm"
                  class="h-8"
                >
                  <Pencil class="w-3 h-3 text-muted-foreground" />
                </Button>
                <Button
                  @click="handleDeleteCategory(subCategory)"
                  variant="ghost"
                  size="sm"
                  class="h-8"
                >
                  <Trash2 class="w-3 h-3 text-destructive" />
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Drag Hint -->
      <div v-if="!loading && filteredCategories.length > 0" class="text-center text-sm text-muted-foreground pt-4">
        💡 Drag categories to reorder them
      </div>
    </div>

    <!-- Dialogs -->
    <CategoryCreateDialog
      v-model:open="createDialogOpen"
      :categories="categories"
      @category-created="handleCategoryCreated"
    />

    <CategoryEditDialog
      v-model:open="editDialogOpen"
      :category="selectedCategory"
      :categories="categories"
      @category-updated="handleCategoryUpdated"
    />

    <CategoryDeleteDialog
      v-model:open="deleteDialogOpen"
      :category="selectedCategory"
      @category-deleted="handleCategoryDeleted"
    />
  </main>
</template>

<style scoped>
.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
