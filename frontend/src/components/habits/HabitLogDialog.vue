<script setup lang="ts">
import { ref, watch } from 'vue'
import type { HabitType } from '@/model/habits/HabitData'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Check } from 'lucide-vue-next'

const props = defineProps<{
  open: boolean
  habitType: HabitType
  initialCount?: number
  initialDuration?: number
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  submit: [data: { completed?: boolean, numberOfTimes?: number, duration?: number }]
}>()

const countValue = ref(props.initialCount || 1)
const durationValue = ref(props.initialDuration || 15)

// Watch for prop changes (for pre-filling existing values)
watch(() => props.initialCount, (val) => { 
  if (val !== undefined) countValue.value = val 
})
watch(() => props.initialDuration, (val) => { 
  if (val !== undefined) durationValue.value = val 
})

function handleSubmit() {
  if (props.habitType === 'YES_NO') {
    emit('submit', { completed: true })
  } else if (props.habitType === 'NUMERIC') {
    emit('submit', { numberOfTimes: countValue.value })
  } else if (props.habitType === 'DURATION') {
    emit('submit', { duration: durationValue.value })
  }
  emit('update:open', false)
}
</script>

<template>
  <Dialog :open="open" @update:open="emit('update:open', $event)">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Log Habit Completion</DialogTitle>
        <DialogDescription>
          Record your progress for this habit.
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-4 py-4">
        <!-- YES_NO: Just confirmation message -->
        <div v-if="habitType === 'YES_NO'" class="text-center">
          <p class="text-muted-foreground">Click confirm to mark this habit as completed for today.</p>
        </div>

        <!-- NUMERIC: Count input -->
        <div v-else-if="habitType === 'NUMERIC'" class="space-y-2">
          <Label for="count">Number of Times</Label>
          <Input 
            id="count" 
            v-model.number="countValue" 
            type="number" 
            min="1"
            placeholder="Enter count"
            @keyup.enter="handleSubmit"
          />
        </div>

        <!-- DURATION: Minutes input -->
        <div v-else-if="habitType === 'DURATION'" class="space-y-2">
          <Label for="duration">Duration (minutes)</Label>
          <Input 
            id="duration" 
            v-model.number="durationValue" 
            type="number" 
            min="1"
            placeholder="Enter minutes"
            @keyup.enter="handleSubmit"
          />
        </div>
      </div>

      <DialogFooter>
        <Button @click="handleSubmit" class="w-full">
          <Check class="w-4 h-4 mr-2" />
          Confirm
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
