<script setup lang="ts">
import { ref, watch } from 'vue'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import type { Wallet } from '@/model/accounting/Wallet'
import WalletService from '@/services/accounting/WalletService'

const props = defineProps<{
  open: boolean
  wallet?: Wallet | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'wallet-saved': []
}>()

const walletService = new WalletService()

const name = ref('')
const description = ref('')
const isSubmitting = ref(false)

const isEditing = ref(false)

// Watch for wallet prop changes to populate form
watch(() => props.wallet, (wallet) => {
  if (wallet) {
    name.value = wallet.name
    description.value = wallet.description || ''
    isEditing.value = true
  } else {
    name.value = ''
    description.value = ''
    isEditing.value = false
  }
}, { immediate: true })

async function handleSubmit() {
  if (!name.value.trim()) {
    return
  }

  isSubmitting.value = true
  try {
    if (props.wallet) {
      // Update existing wallet
      await walletService.updateWallet(props.wallet.id, {
        name: name.value.trim(),
        description: description.value.trim() || undefined,
      })
    } else {
      // Create new wallet
      await walletService.createWallet({
        name: name.value.trim(),
        description: description.value.trim() || undefined,
      })
    }
    
    emit('wallet-saved')
    handleClose()
  } catch (error) {
    console.error('Error saving wallet:', error)
  } finally {
    isSubmitting.value = false
  }
}

function handleClose() {
  emit('update:open', false)
  name.value = ''
  description.value = ''
}
</script>

<template>
  <Dialog :open="open" @update:open="(val) => emit('update:open', val)">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>
          {{ isEditing ? 'Edit Wallet' : 'Create New Wallet' }}
        </DialogTitle>
        <DialogDescription>
          {{ isEditing ? 'Update your wallet details' : 'Add a new wallet to manage your transactions' }}
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-4 py-4">
        <div>
          <Label for="wallet-name">Wallet Name</Label>
          <Input
            id="wallet-name"
            v-model="name"
            placeholder="e.g., Personal, Business, Savings"
            @keyup.enter="handleSubmit"
          />
        </div>

        <div>
          <Label for="wallet-description">Description (Optional)</Label>
          <Textarea
            id="wallet-description"
            v-model="description"
            placeholder="Add any additional details about this wallet"
            rows="3"
          />
        </div>
      </div>

      <DialogFooter>
        <Button @click="handleClose" variant="outline">Cancel</Button>
        <Button 
          @click="handleSubmit" 
          :disabled="!name.trim() || isSubmitting"
        >
          {{ isSubmitting ? 'Saving...' : (isEditing ? 'Update' : 'Create') }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
