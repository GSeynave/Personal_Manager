<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Plus, ChevronRight, ChevronDown } from 'lucide-vue-next'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'
import type { TransactionCategoryDTO } from '@/model/accounting/TransactionCategory'

const props = defineProps<{
  open: boolean
  transactionLabel: string
  recentCategoryIds?: number[]
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'category-selected': [category: TransactionCategoryDTO]
  'create-category': []
}>()

const categoryService = new TransactionCategoryService()
const categories = ref<TransactionCategoryDTO[]>([])
const allCategories = ref<TransactionCategoryDTO[]>([])
const searchQuery = ref('')
const loading = ref(false)
const expandedCategories = ref<Set<number>>(new Set())

const recentCategories = computed(() => {
  if (!props.recentCategoryIds || props.recentCategoryIds.length === 0) {
    return []
  }
  
  // Find categories by ID from all categories (including subcategories)
  const flatCategories: TransactionCategoryDTO[] = []
  
  const flatten = (cats: TransactionCategoryDTO[]) => {
    cats.forEach(cat => {
      flatCategories.push(cat)
      if (cat.subCategories) {
        flatten(cat.subCategories)
      }
    })
  }
  
  flatten(allCategories.value)
  
  return props.recentCategoryIds
    .map(id => flatCategories.find(cat => cat.id === id))
    .filter(Boolean) as TransactionCategoryDTO[]
})

const filteredCategories = computed(() => {
  if (!searchQuery.value) return categories.value
  
  const query = searchQuery.value.toLowerCase()
  
  // When searching, auto-expand all matching categories
  const filtered = categories.value
    .map(category => {
      const titleMatches = category.title.toLowerCase().includes(query)
      const filteredSubs = category.subCategories?.filter(sub =>
        sub.title.toLowerCase().includes(query)
      ) || null
      
      // If searching and has matching subcategories, expand this category
      if (filteredSubs && filteredSubs.length > 0) {
        expandedCategories.value.add(category.id)
      }
      
      return {
        ...category,
        subCategories: filteredSubs
      }
    })
    .filter(category =>
      category.title.toLowerCase().includes(query) ||
      (category.subCategories && category.subCategories.length > 0)
    )
  
  return filtered
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

function toggleCategory(categoryId: number) {
  if (expandedCategories.value.has(categoryId)) {
    expandedCategories.value.delete(categoryId)
  } else {
    expandedCategories.value.add(categoryId)
  }
}

function isExpanded(categoryId: number): boolean {
  return expandedCategories.value.has(categoryId)
}

function selectCategory(category: TransactionCategoryDTO) {
  emit('category-selected', category)
  emit('update:open', false)
  // Reset state
  searchQuery.value = ''
  expandedCategories.value.clear()
}

function createNewCategory() {
  emit('create-category')
}

function getCategoryIcon(category: TransactionCategoryDTO): string {
  // Use the icon from the database, fallback to folder icon
  return category.icon || '📁'
}

onMounted(() => {
  loadCategories()
})

// Reload categories when dialog opens
watch(() => props.open, (newValue) => {
  if (newValue) {
    loadCategories()
  }
})
</script>

<template>
  <Dialog :open="open" @update:open="emit('update:open', $event)">
    <DialogContent class="sm:max-w-[550px]">
      <DialogHeader>
        <DialogTitle>Categorize: {{ transactionLabel }}</DialogTitle>
        <DialogDescription>
          Select a category for this transaction
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-4 mt-4">
        <!-- Search Input with New Button -->
        <div class="flex gap-2">
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              v-model="searchQuery"
              placeholder="Search categories..."
              class="pl-9"
            />
          </div>
          <Button
            @click="createNewCategory"
            size="default"
            class="gap-2"
          >
            <Plus class="w-4 h-4" />
            New
          </Button>
        </div>

        <!-- Categories List -->
        <div class="max-h-[450px] overflow-y-auto space-y-3 pr-2">
          <div v-if="loading" class="text-center py-8 text-muted-foreground">
            Loading categories...
          </div>

          <div v-else-if="filteredCategories.length === 0" class="text-center py-8 text-muted-foreground">
            No categories found
          </div>

          <template v-else>
            <!-- Recent Categories Section -->
            <div v-if="recentCategories.length > 0 && !searchQuery" class="space-y-2">
              <div class="flex items-center gap-2 text-xs font-medium text-muted-foreground">
                <span>⚡</span>
                <span>Recent (last 7 days):</span>
              </div>
              <div class="flex flex-wrap gap-2">
                <Badge
                  v-for="category in recentCategories"
                  :key="category.id"
                  variant="secondary"
                  class="cursor-pointer hover:bg-accent transition-colors px-3 py-1.5"
                  @click="selectCategory(category)"
                >
                  <span class="mr-1">{{ getCategoryIcon(category) }}</span>
                  {{ category.title }}
                </Badge>
              </div>
              <Separator class="my-3" />
            </div>

            <!-- All Categories -->
            <div class="space-y-2">
              <div v-for="category in filteredCategories" :key="category.id" class="space-y-1">
                <!-- Parent Category -->
                <div class="flex items-center gap-2">
                  <button
                    @click="selectCategory(category)"
                    class="flex-1 flex items-center gap-3 px-3 py-2 rounded-md hover:bg-accent transition-colors text-left"
                  >
                    <span class="text-xl">{{ getCategoryIcon(category) }}</span>
                    <div class="flex-1">
                      <div class="font-medium">{{ category.title }}</div>
                      <div v-if="category.description" class="text-xs text-muted-foreground line-clamp-1">
                        {{ category.description }}
                      </div>
                    </div>
                  </button>
                  
                  <!-- Expand/Collapse Button -->
                  <Button
                    v-if="category.subCategories && category.subCategories.length > 0"
                    @click="toggleCategory(category.id)"
                    variant="ghost"
                    size="sm"
                    class="h-8 w-8 p-0"
                  >
                    <ChevronDown 
                      v-if="isExpanded(category.id)" 
                      class="w-4 h-4 text-muted-foreground"
                    />
                    <ChevronRight 
                      v-else 
                      class="w-4 h-4 text-muted-foreground"
                    />
                  </Button>
                </div>

                <!-- Subcategories (Expandable) -->
                <div 
                  v-if="category.subCategories && category.subCategories.length > 0 && isExpanded(category.id)" 
                  class="ml-12 space-y-1"
                >
                  <button
                    v-for="subCategory in category.subCategories"
                    :key="subCategory.id"
                    @click="selectCategory(subCategory)"
                    class="w-full flex items-center gap-2 px-3 py-1.5 rounded-md hover:bg-accent transition-colors text-left"
                  >
                    <span class="text-sm text-muted-foreground">•</span>
                    <div class="flex-1">
                      <div class="text-sm">{{ subCategory.title }}</div>
                    </div>
                  </button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </DialogContent>
  </Dialog>
</template>

<style scoped>
/* Custom scrollbar for the categories list */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: hsl(var(--muted-foreground) / 0.3);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: hsl(var(--muted-foreground) / 0.5);
}
</style>
