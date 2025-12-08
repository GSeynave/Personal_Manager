<script setup lang="ts">
import type { HTMLAttributes } from "vue"
import { cn } from "@/lib/utils"
import { Separator } from '@/components/ui/separator'

const props = defineProps<{
  class?: HTMLAttributes["class"]
}>()
</script>

<template>
  <Separator
    data-sidebar="separator"
    :class="cn('mx-auto w-[60%] bg-sidebar-border/50', props.class)"
  >
    <slot />
  </Separator>
</template>
