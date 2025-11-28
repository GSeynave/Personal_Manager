<script setup lang="ts">
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import TodoList from '../components/todo/TodoList.vue'
import TodoForm from '../components/todo/TodoForm.vue'
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('todo'),
      '--module-color-rgb': hexToRgbStr(moduleColor('todo')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('todo')),
    }"
  >
    <div class="page-header">
      <h1 class="page-title">✓ Todo</h1>
      <p class="page-subtitle">Manage your tasks and priorities</p>
    </div>
    <TodoForm />
    <TodoList />
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  background: var(--bg);
  min-height: 100vh;
}

main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: var(--primary);
  border-radius: 4px 0 0 4px;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}

.page-title {
  margin: 0 0 6px 0;
  font-size: 2rem;
  font-weight: 700;
  color: var(--module-color, #6a1b9a);
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-subtitle {
  margin: 0;
  font-size: 0.95rem;
  color: var(--text-secondary);
}
</style>
