<!-- components/DomainIcon.vue -->
<script setup>
import { computed } from 'vue'
import * as LucideIcons from 'lucide-vue-next'

const props = defineProps({
  name: String,          // 'CheckCircle', 'Flame', etc.
  domain: String,        // 'productivity', 'energy', 'social', 'achievement', 'growth'
  size: { type: Number, default: 24 },
  strokeWidth: { type: Number, default: 2 }
})

const IconComponent = computed(() => LucideIcons[props.name])
const colorClass = computed(() => `text-${props.domain}`)
</script>

<template>
  <component 
    :is="IconComponent" 
    :size="size"
    :stroke-width="strokeWidth"
    :class="[colorClass, 'transition-all hover:scale-110']"
  />
</template>