<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import TodoList from '../components/todo/TodoList.vue'
import TodoForm from '../components/todo/TodoForm.vue'
import { CheckSquare } from 'lucide-vue-next'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('todo'),
      '--module-color-rgb': hexToRgbStr(moduleColor('todo')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('todo')),
    }"
    class="p-6 min-h-screen space-y-8"
  >
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <CheckSquare class="w-8 h-8 text-productivity" />

        <h1 class="text-3xl font-bold text-foreground">Todo</h1>
      </div>
      <p class="text-sm text-productivity">Manage your tasks and priorities</p>
    </div>
    
    <div class="space-y-6 p-6">

    <TodoForm />
    
    <TodoList />
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
