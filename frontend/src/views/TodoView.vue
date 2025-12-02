<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import TodoList from '../components/todo/TodoList.vue'
import TodoForm from '../components/todo/TodoForm.vue'
import { CheckSquare, Flame } from 'lucide-vue-next'
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
    <div>
      <div class="flex items-center gap-3 mb-2">
        <CheckSquare class="w-8 h-8 text-productivity" />


        <h1 class="text-3xl font-bold text-foreground">Todo</h1>
      </div>
      <p class="text-sm text-productivity">Manage your tasks and priorities</p>
    </div>
    
    <TodoForm />
    
    <TodoList />
  </main>
</template>

<style scoped>
main {
  background: hsl(var(--background));
}
</style>
