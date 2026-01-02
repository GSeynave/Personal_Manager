<script lang="ts" setup>
const props = defineProps<{
  title?: string
  compact?: boolean
  dataPeak?: string
}>()
</script>

<template>
  <div class="container">
    <h2>{{ props.title }}</h2>
    <template v-if="props.compact">
      <div class="compact-peek">{{ props.dataPeak ?? '' }}</div>
    </template>
    <template v-else>
      <slot></slot>
    </template>
  </div>
</template>

<style scoped>
h2 {
  margin-bottom: 0.5rem;
  font-weight: bold;
}
.container {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  margin: 1rem 0;
}
</style>
