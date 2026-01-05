<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'

const props = defineProps<{
  modelValue: string | null | undefined
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | null | undefined]
}>()

const showPicker = ref(false)

// Curated list of emojis for categories - can be expanded over time
const emojiCategories = {
  'Money & Finance': ['💰', '💵', '💴', '💶', '💷', '💳', '💸', '🏦', '📈', '📉', '💼', '🪙'],
  'Food & Dining': ['🍔', '🍕', '🍝', '🍜', '🍱', '🍛', '🍲', '🥗', '🥘', '🍳', '🥐', '🍞', '🧀', '🥓', '🥩', '🍗', '🌮', '🌯', '🥙', '🍿', '🧈', '🍰', '🎂', '🍪', '🍩', '🧁', '🥧'],
  'Beverages': ['☕', '🍵', '🧃', '🥤', '🍺', '🍻', '🍷', '🥂', '🍾', '🧉'],
  'Groceries': ['🛒', '🥬', '🥦', '🥕', '🌽', '🍅', '🥒', '🫑', '🥑', '🍇', '🍊', '🍋', '🍌', '🍉', '🍓', '🥝'],
  'Transport': ['🚗', '🚕', '🚙', '🚌', '🚎', '🚐', '🚑', '🚒', '🚓', '🚔', '🚘', '🛻', '🏎️', '🚲', '🛴', '🛵', '🏍️', '🚂', '🚆', '🚇', '🚊', '🚝', '🚄', '🚈', '✈️', '🛫', '🛬', '🚁', '🛶', '⛵', '🚤', '⛽'],
  'Shopping': ['🛍️', '👕', '👔', '👗', '👠', '👞', '👟', '🎽', '🧥', '🧦', '🧤', '👜', '👛', '🎒', '💄', '💅', '👓', '🕶️'],
  'Home & Living': ['🏠', '🏡', '🏘️', '🏚️', '🏗️', '🏢', '🏬', '🏛️', '🪑', '🛏️', '🚪', '🪟', '🛋️', '🚿', '🛁', '🚽', '🧹', '🧺'],
  'Utilities': ['💡', '🔌', '🔋', '💧', '🚰', '🔥', '♨️', '📶', '📡', '📞', '☎️', '📺', '📻', '🌡️'],
  'Health & Medical': ['🏥', '⚕️', '💊', '💉', '🩺', '🩹', '🧬', '🧪', '🦷', '🧠', '🫀', '🫁', '❤️‍🩹', '🏃', '🧘', '🏋️'],
  'Entertainment': ['🎬', '🎮', '🎯', '🎲', '🎰', '🎪', '🎭', '🎨', '🎤', '🎧', '🎵', '🎶', '🎸', '🎹', '🎺', '🎻', '📚', '📖', '📝', '🎟️', '🎫'],
  'Education': ['📚', '📖', '📓', '📔', '📕', '📗', '📘', '📙', '📒', '🎓', '🏫', '🖊️', '✏️', '📝', '🧮', '📐', '📏', '🔬', '🔭', '🌍', '🌎', '🌏'],
  'Travel': ['✈️', '🗺️', '🧳', '🎒', '🏖️', '🏝️', '🗼', '🏰', '🏯', '🗾', '🎡', '🎢', '🚠', '🚡', '🛩️', '🚂', '🚆', '⛱️', '🏕️', '⛺'],
  'Sports & Fitness': ['⚽', '🏀', '🏈', '⚾', '🥎', '🎾', '🏐', '🏉', '🥏', '🎱', '🏓', '🏸', '🏒', '🏑', '🥍', '⛳', '🏹', '🎣', '🥊', '🥋', '⛷️', '🏂', '🏊', '🏄', '🚴', '🏋️', '🤸'],
  'Pets & Animals': ['🐶', '🐱', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', '🦁', '🐮', '🐷', '🐸', '🐵', '🐔', '🐧', '🐦', '🐤', '🦆', '🦅', '🦉', '🦇', '🐺', '🐗', '🐴', '🦄', '🐝', '🐛', '🦋', '🐌', '🐞', '🐜', '🦟', '🦗'],
  'Tech & Electronics': ['💻', '🖥️', '⌨️', '🖱️', '🖨️', '📱', '☎️', '📞', '📟', '📠', '📡', '🔋', '🔌', '💾', '💿', '📀', '🎮', '🕹️', '📷', '📹', '🎥', '📽️', '🎬'],
  'Work & Office': ['💼', '👔', '📊', '📈', '📉', '🗂️', '📁', '📂', '🗃️', '📋', '📌', '📍', '✂️', '📎', '🖇️', '📏', '📐', '✏️', '🖊️', '🖋️', '📝', '📄', '📃', '📑', '📜'],
  'Other': ['⭐', '✨', '💫', '🔥', '💧', '⚡', '☀️', '🌙', '⭐', '🎁', '🎀', '🎈', '🎉', '🎊', '🏆', '🥇', '🥈', '🥉', '📁', '📂', '🗂️', '❓', '❔', '❗', '❕', '💬', '💭', '🔔', '🔕', '⏰', '⏱️', '⏲️', '⏳', '⌛']
}

function selectEmoji(emoji: string) {
  emit('update:modelValue', emoji)
  showPicker.value = false
}

function clearEmoji() {
  emit('update:modelValue', null)
  showPicker.value = false
}
</script>

<template>
  <div class="flex items-center gap-2">
    <!-- Selected Emoji Display / Trigger -->
    <Button
      type="button"
      variant="outline"
      class="h-10 w-10 p-0 text-2xl"
      @click="showPicker = true"
    >
      {{ modelValue || '📁' }}
    </Button>

    <!-- Emoji Picker Dialog -->
    <Dialog v-model:open="showPicker">
      <DialogContent class="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>Select an Icon</DialogTitle>
        </DialogHeader>

        <div class="max-h-[500px] overflow-y-auto space-y-4 pr-2">
          <div v-for="(emojis, category) in emojiCategories" :key="category" class="space-y-2">
            <h3 class="text-sm font-medium text-muted-foreground">{{ category }}</h3>
            <div class="grid grid-cols-8 gap-2">
              <button
                v-for="emoji in emojis"
                :key="emoji"
                type="button"
                @click="selectEmoji(emoji)"
                class="h-10 w-10 flex items-center justify-center text-2xl hover:bg-accent rounded-md transition-colors"
                :class="{ 'bg-accent': modelValue === emoji }"
              >
                {{ emoji }}
              </button>
            </div>
          </div>

          <!-- Clear Option -->
          <div class="space-y-2 pt-4 border-t">
            <h3 class="text-sm font-medium text-muted-foreground">Default</h3>
            <Button
              type="button"
              variant="outline"
              @click="clearEmoji"
              class="w-full justify-start gap-2"
            >
              <span class="text-xl">📁</span>
              <span>Use default folder icon</span>
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  </div>
</template>

<style scoped>
/* Custom scrollbar for the emoji picker */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: hsl(var(--muted-foreground) / 0.3);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: hsl(var(--muted-foreground) / 0.5);
}
</style>
