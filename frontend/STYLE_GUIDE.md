# Personal Manager - CSS/Style Guide

> **Purpose**: This document provides a comprehensive reference for the styling patterns, color system, and component structure used in the Personal Manager application. Use this as context when creating or modifying views and components.

---

## Table of Contents
1. [Color System](#color-system)
2. [View Structure](#view-structure)
3. [Component Patterns](#component-patterns)
4. [Typography](#typography)
5. [Spacing & Layout](#spacing--layout)
6. [UI Component Library](#ui-component-library)
7. [Dashboard Cards](#dashboard-cards)
8. [Animations & Transitions](#animations--transitions)
9. [Icons](#icons)

---

## Color System

### CSS Custom Properties (base.css)

#### Light Mode
```css
--background: 0 0% 100%;           /* White background */
--foreground: 0 0% 3.9%;           /* Near-black text */
--card: 10 10% 96%;                /* Light gray cards */
--card-item: 10 10% 90%;           /* Slightly darker card items */
--primary: 0 0% 9%;                /* Dark primary */
--muted: 0 0% 96.1%;               /* Muted backgrounds */
--border: 0 0% 89.8%;              /* Border color */
```

#### Dark Mode
```css
--background: 0 0% 3.9%;           /* Dark background */
--foreground: 0 0% 98%;            /* White text */
--card: 0 0% 3.9%;                 /* Dark cards */
--card-item: 0 0% 5.9%;            /* Slightly lighter card items */
--primary: 0 0% 98%;               /* Light primary */
--border: 0 0% 14.9%;              /* Dark border */
```

### Module/Domain Colors

**IMPORTANT: Domain Accent Colors for Visual Context**

Each module belongs to a domain with a specific accent color. This color system helps users immediately recognize which domain they're working in. Each view **must** incorporate its domain accent color in:
- Page header icons
- Section titles/descriptions
- Primary action buttons (create, add, etc.)
- Left border accent bar

#### Domain Categories & Colors

**🧘 Wellness & Health** (Red/Rose palette)
- `health: '#E53935'` - Red
- `habits: '#7B1FA2'` - Deep Purple
- `sleep: '#5C6BC0'` - Indigo
- `diet: '#D81B60'` - Rose
- Tailwind class: `.text-health`

**💼 Productivity & Focus** (Purple/Blue palette)
- `todo: '#6A1B9A'` - Purple
- `projects: '#0288D1'` - Light Blue
- `pomodoro: '#F4511E'` - Tomato
- `notes: '#8BC34A'` - Lime
- `calendar: '#00897B'` - Teal
- Tailwind class: `.text-productivity`

**💰 Finance & Resources** (Green palette)
- `accounting: '#2E7D32'` - Green
- `finance: '#2E7D32'` - Green
- Tailwind class: `.text-accounting`

**🎯 Growth & Achievement** (Yellow/Orange palette)
- `goals: '#FFD600'` - Amber
- `fitness: '#FB8C00'` - Orange
- `learning: '#3F51B5'` - Indigo
- Tailwind class: `.text-achievement`

**🗺️ Social & Places** (Teal palette)
- `poi: '#009688'` - Teal
- `energy: '#1976D2'` - Blue
- Tailwind class: `.text-social`

#### Complete Module Color Reference

```typescript
const MODULE_COLORS = {
  // Wellness & Health
  health: '#E53935',        // Red
  habits: '#7B1FA2',        // Deep Purple
  sleep: '#5C6BC0',         // Indigo
  diet: '#D81B60',          // Rose
  
  // Productivity & Focus
  todo: '#6A1B9A',          // Purple
  projects: '#0288D1',      // Light Blue
  pomodoro: '#F4511E',      // Tomato
  notes: '#8BC34A',         // Lime
  calendar: '#00897B',      // Teal
  
  // Finance & Resources
  accounting: '#2E7D32',    // Green
  finance: '#2E7D32',       // Green
  
  // Growth & Achievement
  goals: '#FFD600',         // Amber
  fitness: '#FB8C00',       // Orange
  learning: '#3F51B5',      // Indigo
  
  // Social & Places
  poi: '#009688',           // Teal
  energy: '#1976D2',        // Blue
}
```

### Domain Color Utilities

**Tailwind CSS Classes** (defined in `base.css`):
- `.text-productivity` - Purple color for productivity-related items
- `.text-health` - Red color for health-related items
- `.text-accounting` - Green/accounting color
- `.text-social` - Social color
- `.text-achievement` - Achievement color
- `.text-growth` - Growth color

### Module Color System Functions

```typescript
// Get module color
moduleColor('todo') // Returns: '#6A1B9A'

// Convert hex to RGB string for CSS variables
hexToRgbStr('#6A1B9A') // Returns: '106, 27, 154'

// Calculate tint alpha for colored backgrounds
moduleTintAlpha('#6A1B9A') // Returns: 0.03-0.08 based on luminance
```

---

## View Structure

### Standard View Template (TodoView.vue / HabitsView.vue)

```vue
<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import { IconComponent } from 'lucide-vue-next'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('moduleName'),
      '--module-color-rgb': hexToRgbStr(moduleColor('moduleName')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('moduleName')),
    }"
    class="p-6 min-h-screen space-y-8"
  >
    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <IconComponent class="w-8 h-8 text-[domain-color]" />
        <h1 class="text-3xl font-bold text-foreground">Module Name</h1>
      </div>
      <p class="text-sm text-[domain-color]">Module description</p>
    </div>
    
    <!-- Content Area -->
    <div class="space-y-6 p-6">
      <!-- Your components here -->
    </div>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
```

### Key Elements:
1. **CSS Variables Injection**: Module colors are injected as CSS variables (for future use)
2. **Page Header**: Icon + Title + Description pattern
3. **Content Wrapper**: `.space-y-6 p-6` for consistent spacing
4. **Domain Accent Touches**: Subtle color touches on key elements (icons, descriptions, buttons)

### Domain Accent Usage Requirements

**Every view MUST include domain accent colors as subtle touches on key elements:**

1. **Page Header Icon** - Use domain color class (PRIMARY)
   ```vue
   <CheckSquare class="w-8 h-8 text-productivity" />
   ```

2. **Page Description** - Use domain color class (PRIMARY)
   ```vue
   <p class="text-sm text-productivity">Description</p>
   ```

3. **Primary Action Button Icons** - Use colored icons (RECOMMENDED)
   ```vue
   <Button>
     <CirclePlus class="w-4 h-4 mr-2 text-productivity" />
     Create Todo
   </Button>
   ```

4. **Status Indicators** - Use domain colors for active/selected states (OPTIONAL)
   ```vue
   <Circle class="w-4 h-4 text-productivity" />
   ```

5. **Section Icons** - Can use domain colors for emphasis (OPTIONAL)
   ```vue
   <Clock class="w-5 h-5 text-health" />
   ```

**Goal**: 3-5 subtle touches of the domain accent color throughout the view to provide visual context without overwhelming the design.

---

## Component Patterns

### Card-Based Components

#### Todo Component Pattern
```vue
<div 
  :class="[
    'group relative flex items-center justify-between gap-4 p-4',
    'border rounded-lg transition-all cursor-grab',
    'hover:shadow-md hover:border-primary hover:-translate-y-0.5',
    completed ? 'bg-muted/50 opacity-70 line-through' : 'bg-card',
  ]" 
>
  <!-- Delete button (hidden until hover) -->
  <Button
    variant="ghost"
    size="icon"
    class="absolute top-1 right-1 h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity"
  >
    <X class="h-4 w-4" />
  </Button>

  <!-- Content -->
  <div class="flex items-center gap-3 flex-1 min-w-0">
    <!-- Icon/Status indicator -->
    <CircleCheck v-if="completed" class="h-5 w-5 text-green-600" />
    
    <!-- Text -->
    <span class="font-medium text-foreground truncate">
      {{ title }}
    </span>
  </div>
  
  <!-- Metadata (badges, avatars, etc.) -->
  <div class="flex items-center gap-3 flex-shrink-0">
    <Badge variant="outline">{{ date }}</Badge>
  </div>
</div>
```

**Key Patterns**:
- `.group` for hover effects on children
- `.opacity-0 group-hover:opacity-100` for reveal buttons
- `.transition-all` for smooth animations
- `.hover:-translate-y-0.5` for lift effect
- `.flex-1 min-w-0` for text truncation
- `.flex-shrink-0` for metadata that shouldn't shrink

#### Habit Card Pattern (Grid Layout)
```vue
<Card 
  class="cursor-pointer transition-all hover:shadow-lg"
  :class="{ 'ring-2 ring-green-500': isComplete }"
>
  <CardHeader>
    <div class="flex justify-between items-start">
      <CardTitle class="text-lg">{{ title }}</CardTitle>
      <Button variant="ghost" size="icon">
        <Pencil class="w-4 h-4" />
      </Button>
    </div>
    <CardDescription>{{ description }}</CardDescription>
  </CardHeader>

  <CardFooter class="flex-col items-start gap-2">
    <div class="flex items-center gap-2 w-full">
      <Badge variant="secondary">{{ category }}</Badge>
      <span class="text-sm text-muted-foreground ml-auto">
        {{ count }} logs
      </span>
    </div>
    <Badge v-if="isComplete" variant="default" class="w-full justify-center">
      <Check class="w-3 h-3 mr-1" />
      Logged Today
    </Badge>
  </CardFooter>
</Card>
```

**Key Patterns**:
- `.ring-2 ring-[color]` for status indication
- `.flex-col items-start` for footer layout
- `.w-full justify-center` for full-width badges
- `.ml-auto` for pushing items to the right

### Form Patterns

```vue
<div class="space-y-4">
  <div>
    <Label for="field-id">Field Label</Label>
    <Input 
      id="field-id" 
      v-model="value" 
      placeholder="Placeholder"
      @keyup.enter="handleSubmit" 
    />
  </div>

  <div>
    <Label for="select-id">Select Label</Label>
    <Select v-model="selectedValue">
      <SelectTrigger id="select-id">
        <SelectValue placeholder="Select..." />
      </SelectTrigger>
      <SelectContent>
        <SelectItem 
          v-for="option in options" 
          :key="option.value" 
          :value="option.value"
        >
          {{ option.label }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>

  <div class="flex gap-2">
    <Button @click="handleSubmit" class="flex-1">
      <Icon class="w-4 h-4 mr-2" />
      Submit
    </Button>
  </div>
</div>
```

---

## Typography

### Heading Hierarchy
```css
h1 { font-size: 1.875rem; }  /* text-3xl - Page titles */
h2 { font-size: 1.5rem; }    /* text-2xl - Section titles */
h3 { font-size: 1.25rem; }   /* text-xl - Subsection titles */
```

### Text Sizes (Tailwind)
- `.text-xs` - 0.75rem (12px) - Small labels
- `.text-sm` - 0.875rem (14px) - Body text, descriptions
- `.text-base` - 1rem (16px) - Default text
- `.text-lg` - 1.125rem (18px) - Card titles
- `.text-xl` - 1.25rem (20px) - Section headers
- `.text-2xl` - 1.5rem (24px) - Detail view titles
- `.text-3xl` - 1.875rem (30px) - Page titles

### Font Weights
- `.font-medium` - 500 - Standard emphasis
- `.font-semibold` - 600 - Headers
- `.font-bold` - 700 - Strong emphasis

### Text Colors
- `.text-foreground` - Primary text
- `.text-muted-foreground` - Secondary/descriptive text
- `.text-destructive` - Error/delete actions
- `.text-[domain]` - Domain-specific colors (productivity, health, etc.)

---

## Spacing & Layout

### Spacing Scale (Tailwind)
```css
gap-1    /* 0.25rem = 4px */
gap-2    /* 0.5rem = 8px */
gap-3    /* 0.75rem = 12px */
gap-4    /* 1rem = 16px */
gap-6    /* 1.5rem = 24px */
gap-8    /* 2rem = 32px */

p-2, p-3, p-4, p-6  /* Same scale for padding */
space-y-2, space-y-4, space-y-6  /* Vertical spacing between children */
```

### Common Layout Patterns

#### Flex Row with Gap
```html
<div class="flex items-center gap-3">
  <Icon />
  <span>Text</span>
</div>
```

#### Grid Layouts
```html
<!-- Responsive grid -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <!-- Cards -->
</div>

<!-- Two-column form -->
<div class="grid grid-cols-2 gap-4">
  <!-- Inputs -->
</div>

<!-- Sidebar + Content -->
<div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
  <div class="lg:col-span-1"><!-- Sidebar --></div>
  <div class="lg:col-span-3"><!-- Content --></div>
</div>
```

#### Vertical Stacking
```html
<div class="space-y-4">
  <!-- Auto-spacing between children -->
</div>
```

---

## UI Component Library

### Button Variants

```vue
<!-- Primary -->
<Button class="bg-primary hover:bg-primary/80">Primary</Button>

<!-- Ghost -->
<Button variant="ghost">Ghost</Button>

<!-- Outline -->
<Button variant="outline">Outline</Button>

<!-- Destructive -->
<Button variant="destructive">Delete</Button>

<!-- With Icon -->
<Button>
  <Plus class="w-4 h-4 mr-2" />
  Add Item
</Button>

<!-- Icon Only -->
<Button variant="ghost" size="icon">
  <Pencil class="w-4 h-4" />
</Button>
```

### Badge Variants

```vue
<!-- Default -->
<Badge>Default</Badge>

<!-- Secondary -->
<Badge variant="secondary">Category</Badge>

<!-- Outline -->
<Badge variant="outline">Date</Badge>

<!-- With Icon -->
<Badge variant="default" class="w-full justify-center">
  <Check class="w-3 h-3 mr-1" />
  Complete
</Badge>
```

### Card Structure

```vue
<Card>
  <CardHeader>
    <CardTitle>Title</CardTitle>
    <CardDescription>Description</CardDescription>
  </CardHeader>
  
  <CardContent>
    <!-- Main content -->
  </CardContent>
  
  <CardFooter>
    <!-- Actions or metadata -->
  </CardFooter>
</Card>
```

### Dialog Pattern

```vue
<Dialog v-model:open="showDialog">
  <DialogContent>
    <DialogHeader>
      <DialogTitle>Dialog Title</DialogTitle>
      <DialogDescription>
        Description text
      </DialogDescription>
    </DialogHeader>

    <div class="space-y-4 py-4">
      <!-- Dialog content -->
    </div>

    <DialogFooter>
      <Button @click="confirm">Confirm</Button>
      <Button @click="cancel" variant="outline">Cancel</Button>
    </DialogFooter>
  </DialogContent>
</Dialog>
```

---

## Dashboard Cards

### Widget Card Base Style (card-base.css)

```css
/* Forces inner component transparency */
.widget-card > * {
  background: transparent !important;
  box-shadow: none !important;
  width: 100%;
  height: 100%;
  padding: 14px;
  box-sizing: border-box;
}

/* Compact preview layout */
.widget-card .compact-peek {
  background: transparent;
  border-radius: 0;
  padding: 0;
  display: flex;
  gap: 8px;
  align-items: center;
}

/* Data pill styling */
.widget-card .data-peak {
  background: var(--module-color, #7f8c8d);
  color: white;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 0.78rem;
}

.widget-card .widget-title {
  padding-left: 8px;
}
```

---

## Animations & Transitions

### Common Transition Classes

```css
.transition-all          /* Smooth all property transitions */
.transition-colors       /* Color transitions only */
.transition-opacity      /* Opacity transitions */
.transition-transform    /* Transform transitions */

/* Duration modifiers */
.duration-150            /* 150ms */
.duration-200            /* 200ms */
.duration-300            /* 300ms */
```

### Hover Effects

```vue
<!-- Lift effect -->
<div class="hover:-translate-y-0.5 transition-transform">

<!-- Scale effect -->
<div class="hover:scale-105 transition-transform">

<!-- Shadow effect -->
<div class="hover:shadow-lg transition-shadow">

<!-- Opacity reveal -->
<div class="opacity-0 group-hover:opacity-100 transition-opacity">
```

### Focus States

```vue
<!-- Ring on focus -->
<Input class="focus:ring-2 focus:ring-primary" />

<!-- Border color on focus -->
<div class="focus-within:border-primary" />
```

---

## State Indicators

### Completion States
```vue
<!-- Completed item -->
<div :class="completed ? 'bg-muted/50 opacity-70 line-through' : 'bg-card'">

<!-- Active/Selected state -->
<div :class="{ 'bg-accent': isActive, 'hover:bg-accent/50': !isActive }">

<!-- Success indicator -->
<div :class="{ 'ring-2 ring-green-500': isSuccess }">
```

### Loading States
```vue
<div v-if="isLoading" class="text-muted-foreground">
  Loading...
</div>
```

### Error States
```vue
<div v-if="error" class="text-destructive">
  {{ error }}
</div>
```

---

## Icons

### Icon Library
The project uses **Lucide Vue Next** icons exclusively for consistency across the application.

### Icon Style Guidelines

**IMPORTANT: Always use Colored Outline Icons**

Icons should maintain a soft but colorful aesthetic throughout the application. This approach keeps the UI friendly and vibrant while maintaining clarity.

#### Standard Icon Pattern
```vue
<script setup lang="ts">
import { IconName } from 'lucide-vue-next'
</script>

<template>
  <!-- Use domain/module colors for icons -->
  <IconName class="w-8 h-8 text-productivity" />
  <IconName class="w-5 h-5 text-health" />
  <IconName class="w-4 h-4 text-accounting" />
</template>
```

#### Icon Sizing
- **Page Headers**: `w-8 h-8` (32px) - Large icons for main page titles
- **Section Headers**: `w-6 h-6` (24px) - Medium icons for subsections
- **Cards/Lists**: `w-5 h-5` (20px) - Standard icon size for card headers
- **Buttons/Badges**: `w-4 h-4` (16px) - Small icons for inline elements
- **Tiny/Metadata**: `w-3 h-3` (12px) - Minimal icons for badges or compact areas

#### Icon Colors

**Use Domain Colors for Visual Distinction:**
```vue
<!-- Domain-specific colors -->
<User class="w-8 h-8 text-productivity" />      <!-- Purple -->
<Heart class="w-8 h-8 text-health" />           <!-- Red -->
<DollarSign class="w-8 h-8 text-accounting" />  <!-- Green -->
<Dumbbell class="w-8 h-8 text-[#FB8C00]" />     <!-- Orange for fitness -->
<Book class="w-8 h-8 text-[#3F51B5]" />         <!-- Indigo for learning -->
```

**Contextual Colors:**
```vue
<!-- Status indicators -->
<Check class="w-4 h-4 text-green-600" />        <!-- Success -->
<X class="w-4 h-4 text-destructive" />          <!-- Error/delete -->
<AlertCircle class="w-4 h-4 text-yellow-600" /> <!-- Warning -->

<!-- Muted/secondary -->
<Clock class="w-4 h-4 text-muted-foreground" /> <!-- Metadata -->
<Calendar class="w-4 h-4 text-muted-foreground" />
```

#### Common Icons by Context

**Navigation & Actions:**
- `Plus`, `CirclePlus` - Add/create actions
- `Pencil`, `Edit` - Edit actions
- `Trash2` - Delete actions
- `X` - Close/cancel
- `Check`, `CircleCheck` - Confirm/complete
- `ChevronRight`, `ChevronLeft` - Navigation

**User & Profile:**
- `User` - Profile/account
- `Mail` - Email
- `Tag` - User tags
- `Key` - Authentication/ID
- `LogOut` - Logout action

**Data & Content:**
- `Calendar` - Dates
- `Clock` - Time/duration
- `ChartLine` - Analytics/progress
- `Info` - Information
- `CheckCircle2` - Completion status

**Module-Specific:**
- `CheckSquare` - Todo module
- `Repeat` - Habits module
- `Heart` - Health module
- `Dumbbell` - Fitness module
- `DollarSign` - Finance/accounting

#### Best Practices
1. **Always use outline style** - Never use filled/solid variants
2. **Color icons appropriately** - Match domain colors or use contextual colors
3. **Consistent sizing** - Use the size scale defined above
4. **Pair with text** - Icons should complement, not replace text labels
5. **Accessibility** - Ensure sufficient contrast with backgrounds

#### Example Implementation
```vue
<script setup lang="ts">
import { CheckSquare, Plus, Clock, Tag } from 'lucide-vue-next'
</script>

<template>
  <!-- Page header with colored icon -->
  <div class="flex items-center gap-3">
    <CheckSquare class="w-8 h-8 text-productivity" />
    <h1 class="text-3xl font-bold">Todo</h1>
  </div>

  <!-- Button with icon -->
  <Button>
    <Plus class="w-4 h-4 mr-2" />
    Add Task
  </Button>

  <!-- Metadata with muted icons -->
  <div class="flex items-center gap-2">
    <Clock class="w-4 h-4 text-muted-foreground" />
    <span class="text-sm">2 hours ago</span>
  </div>

  <!-- Status indicator -->
  <Badge variant="secondary">
    <Tag class="w-3 h-3 mr-1 text-health" />
    Important
  </Badge>
</template>
```

---

## Best Practices

### 1. **Use CSS Variables for Module Colors**
Always inject module colors as CSS variables in view templates (for potential future use):
```vue
:style="{
  '--module-color': moduleColor('moduleName'),
  '--module-color-rgb': hexToRgbStr(moduleColor('moduleName')),
  '--module-tint-alpha': moduleTintAlpha(moduleColor('moduleName')),
}"
```

**Domain Accent Requirements:**
- ✅ Page header icon uses domain color (PRIMARY)
- ✅ Page description uses domain color (PRIMARY)
- ✅ Primary action button icons use domain color (RECOMMENDED)
- ✅ 3-5 subtle touches of the accent color throughout the view
- ❌ No heavy borders or overwhelming color usage

### 2. **Consistent Spacing**
- Use `.space-y-6 p-6` for content wrappers
- Use `.gap-4` for grid layouts
- Use `.gap-3` for flex items

### 3. **Hover Interactions**
- Add `.group` to parent for child hover effects
- Use `.group-hover:opacity-100` for reveal patterns
- Include `.transition-all` for smooth animations

### 4. **Responsive Design**
- Use `grid-cols-1 md:grid-cols-2 lg:grid-cols-3` for responsive grids
- Use `flex-col md:flex-row` for responsive flex layouts
- Test on mobile, tablet, and desktop breakpoints

### 5. **Icons**
- Always use **colored outline icons** from Lucide Vue Next
- Match icon colors to domain/module colors
- Use appropriate sizing: `w-8 h-8` for headers, `w-4 h-4` for buttons
- Keep icons vibrant and colorful for a friendly UI

### 6. **Accessibility**
- Always pair icons with text labels or aria-labels
- Maintain sufficient color contrast
- Use semantic HTML elements
- Include `:focus` and `:focus-visible` states

### 7. **Component Composition**
- Break down complex components into smaller pieces
- Use slots for flexible content areas
- Emit events for parent communication
- Props for configuration, events for actions

---

## Quick Reference Cheat Sheet

### Common Class Combinations

```vue
<!-- Interactive card -->
class="bg-card border rounded-lg p-4 hover:shadow-md hover:border-primary transition-all cursor-pointer"

<!-- Button with icon -->
class="flex items-center gap-2 bg-primary hover:bg-primary/80 text-white px-4 py-2 rounded-md"

<!-- Badge/pill -->
class="inline-flex items-center gap-1 bg-secondary text-secondary-foreground px-2 py-1 rounded-full text-xs"

<!-- Form field wrapper -->
class="space-y-2"

<!-- Grid container -->
class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"

<!-- Flex row with spacing -->
class="flex items-center gap-3"

<!-- Vertical stack -->
class="space-y-4"

<!-- Hidden on mobile, visible on desktop -->
class="hidden lg:block"

<!-- Text truncation -->
class="truncate"

<!-- Full width centered -->
class="w-full flex justify-center items-center"
```

---

## File Locations

- **Base CSS**: `/src/assets/base.css`
- **Global CSS**: `/src/assets/global.css`
- **Module Colors**: `/src/config/moduleColors.ts`
- **Tailwind Config**: `/tailwind.config.js`
- **UI Components**: `/src/components/ui/*`
- **Dashboard Card Base**: `/src/components/Dashboard/card-base.css`

---

**Last Updated**: December 2024
**Version**: 1.0
