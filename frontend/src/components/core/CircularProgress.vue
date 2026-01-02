<template>
  <div class="relative inline-flex items-center justify-center">
    <!-- Circular Progress SVG -->
    <svg
      class="absolute inset-0 -rotate-90"
      :width="size"
      :height="size"
      viewBox="0 0 100 100"
    >
      <!-- Background circle -->
      <circle
        cx="50"
        cy="50"
        :r="radius"
        fill="none"
        :stroke="backgroundColor"
        :stroke-width="strokeWidth"
        class="opacity-20 dark:opacity-30"
      />
      <!-- Progress circle -->
      <circle
        cx="50"
        cy="50"
        :r="radius"
        fill="none"
        :stroke="progressColor"
        :stroke-width="strokeWidth"
        :stroke-dasharray="circumference"
        :stroke-dashoffset="progressOffset"
        stroke-linecap="round"
        class="transition-all duration-500 ease-out"
        :class="{ 'animate-pulse-glow': isLevelingUp }"
      />
    </svg>
    
    <!-- Content slot (profile picture) - centered -->
    <div class="relative flex items-center justify-center">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  progress: number // 0-100
  size?: number
  strokeWidth?: number
  progressColor?: string
  backgroundColor?: string
  isLevelingUp?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 48,
  strokeWidth: 3,
  progressColor: '#6366f1',
  backgroundColor: 'hsl(var(--border))',
  isLevelingUp: false
})

const radius = computed(() => (100 - props.strokeWidth) / 2)
const circumference = computed(() => 2 * Math.PI * radius.value)
const progressOffset = computed(() => {
  const progress = Math.min(100, Math.max(0, props.progress))
  return circumference.value - (progress / 100) * circumference.value
})
</script>

<style scoped>
@keyframes pulse-glow {
  0%, 100% {
    filter: drop-shadow(0 0 2px currentColor);
    opacity: 1;
  }
  50% {
    filter: drop-shadow(0 0 8px currentColor);
    opacity: 0.8;
  }
}

.animate-pulse-glow {
  animation: pulse-glow 1.5s ease-in-out infinite;
}
</style>
