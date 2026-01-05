# Transaction Category Modal Components

This document describes the new transaction categorization modal components.

## Components Created

### 1. **TransactionCategoryDialog.vue**
A modal for selecting transaction categories with search functionality.

**Features:**
- Search categories by name
- Hierarchical display (parent categories with subcategories)
- Icons for visual identification
- Quick selection of subcategories
- Option to create new categories

**Props:**
- `open: boolean` - Controls dialog visibility
- `transactionLabel: string` - The transaction description to display in the title

**Emits:**
- `update:open: (value: boolean)` - Dialog open/close state
- `category-selected: (category: TransactionCategoryDTO)` - When a category is selected
- `create-category: ()` - When user wants to create a new category

**Usage:**
```vue
<TransactionCategoryDialog
  v-model:open="dialogOpen"
  :transaction-label="transaction.importLabel"
  @category-selected="handleCategorySelected"
  @create-category="openCreateDialog"
/>
```

---

### 2. **CategoryCreateDialog.vue**
A modal for creating new transaction categories (root or subcategories).

**Features:**
- Title, description, and budget fields
- Parent category selection (radio buttons)
- Form validation
- Error handling

**Props:**
- `open: boolean` - Controls dialog visibility
- `categories: TransactionCategoryDTO[]` - List of existing categories

**Emits:**
- `update:open: (value: boolean)` - Dialog open/close state
- `category-created: (category: TransactionCategoryDTO)` - When a new category is created

**Usage:**
```vue
<CategoryCreateDialog
  v-model:open="createDialogOpen"
  :categories="categories"
  @category-created="handleCategoryCreated"
/>
```

---

### 3. **TransactionCategoryService.ts**
Complete service for Transaction Category API operations.

**Methods:**
- `getAllCategories()` - Get flat list of all categories
- `getRootCategories()` - Get hierarchical tree structure
- `getCategoryById(id)` - Get specific category with subcategories
- `createCategory(request)` - Create new category
- `updateCategory(id, request)` - Update existing category
- `deleteCategory(id)` - Delete category

**Usage:**
```typescript
import TransactionCategoryService from '@/services/accounting/TransactionCategoryService'

const service = new TransactionCategoryService()

// Get all categories with hierarchy
const categories = await service.getRootCategories()

// Create a new category
const newCategory = await service.createCategory({
  title: 'Groceries',
  description: 'Food shopping',
  expectedAmount: 500,
  parentCategoryId: 1 // Optional
})
```

---

## Updated AccountingCategorize.vue

The categorization page has been updated to use the new modal:

**Changes:**
- Click on "Select category..." button to open the category selection modal
- Select a category from the hierarchical list
- Search categories by name
- Create new categories on-the-fly
- Categories are loaded from the API

---

## Category Icons

The modal includes emoji icons for common categories:
- 🍔 Food
- 🚗 Transport
- 🛒 Shopping
- 🎬 Entertainment
- 🏠 Housing
- 💡 Utilities
- 🏥 Health
- 📚 Education
- ✈️ Travel
- 💰 Income

You can customize these in `TransactionCategoryDialog.vue` in the `categoryIcons` object.

---

## API Models

Updated `TransactionCategory.ts` with proper TypeScript interfaces:

```typescript
interface TransactionCategoryDTO {
  id: number
  title: string
  description: string | null
  expectedAmount: number | null
  parentCategoryId: number | null
  parentCategoryTitle: string | null
  subCategories: TransactionCategoryDTO[] | null
}

interface CreateTransactionCategoryRequest {
  title: string
  description?: string
  expectedAmount?: number
  parentCategoryId?: number
}

interface UpdateTransactionCategoryRequest {
  title?: string
  description?: string
  expectedAmount?: number
  parentCategoryId?: number
}
```
