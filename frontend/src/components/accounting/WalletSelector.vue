<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'
import type { Wallet } from '@/model/accounting/Wallet'
import WalletService from '@/services/accounting/WalletService'
import WalletDialog from './WalletDialog.vue'

const walletService = new WalletService()

const wallets = ref<Wallet[]>([])
const selectedWalletId = ref<number | null>(null)
const showWalletDialog = ref(false)
const editingWallet = ref<Wallet | null>(null)
const isLoading = ref(false)

const emit = defineEmits<{
  walletSelected: [walletId: number]
  walletChanged: [wallet: Wallet | null]
}>()

const selectedWallet = computed(() => {
  return wallets.value.find(w => w.id === selectedWalletId.value) || null
})

async function loadWallets() {
  isLoading.value = true
  try {
    wallets.value = await walletService.getWalletsForCurrentUser()
    
    // Auto-select first wallet if none selected
    if (wallets.value.length > 0 && !selectedWalletId.value) {
      const firstWallet = wallets.value[0]
      if (firstWallet) {
        selectWallet(firstWallet.id)
      }
    }
  } catch (error) {
    console.error('Error loading wallets:', error)
  } finally {
    isLoading.value = false
  }
}

function selectWallet(walletId: number) {
  selectedWalletId.value = walletId
  emit('walletSelected', walletId)
  emit('walletChanged', selectedWallet.value)
}

function openCreateDialog() {
  editingWallet.value = null
  showWalletDialog.value = true
}

function openEditDialog(wallet: Wallet) {
  editingWallet.value = wallet
  showWalletDialog.value = true
}

async function handleWalletSaved() {
  showWalletDialog.value = false
  editingWallet.value = null
  await loadWallets()
}

async function handleDeleteWallet(wallet: Wallet) {
  if (!confirm(`Are you sure you want to delete "${wallet.name}"?`)) {
    return
  }
  
  try {
    await walletService.deleteWallet(wallet.id)
    await loadWallets()
    
    // If we deleted the selected wallet, select another one
    if (selectedWalletId.value === wallet.id && wallets.value.length > 0) {
      const firstWallet = wallets.value[0]
      if (firstWallet) {
        selectWallet(firstWallet.id)
      }
    }
  } catch (error) {
    console.error('Error deleting wallet:', error)
  }
}

function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount)
}

onMounted(() => {
  loadWallets()
})
</script>

<template>
  <div class="wallet-selector h-full">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-xl font-semibold text-foreground">Wallets</h2>
      <Button 
        variant="ghost" 
        size="icon"
        @click="openCreateDialog"
        class="h-8 w-8"
      >
        <Plus class="w-4 h-4 text-accounting" />
      </Button>
    </div>

    <div v-if="isLoading" class="text-sm text-muted-foreground">
      Loading wallets...
    </div>

    <div v-else-if="wallets.length === 0" class="text-center py-8">
      <p class="text-sm text-muted-foreground mb-4">No wallets yet</p>
      <Button @click="openCreateDialog" variant="outline" size="sm">
        <Plus class="w-4 h-4 mr-2 text-accounting" />
        Create Wallet
      </Button>
    </div>

    <div v-else class="space-y-2">
      <Card
        v-for="wallet in wallets"
        :key="wallet.id"
        :class="[
          'group relative p-3 cursor-pointer transition-all border',
          selectedWalletId === wallet.id 
            ? 'bg-accent border-accounting ring-1 ring-accounting' 
            : 'bg-card hover:bg-accent/50 hover:border-primary'
        ]"
        @click="selectWallet(wallet.id)"
      >
        <div class="flex items-start justify-between gap-2">
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between gap-2 mb-1">
              <h3 class="font-medium text-foreground truncate">{{ wallet.name }}</h3>
              <span v-if="wallet.balance !== undefined" class="text-sm font-semibold text-accounting flex-shrink-0">
                {{ formatCurrency(wallet.balance) }}
              </span>
            </div>
            <p v-if="wallet.description" class="text-xs text-muted-foreground truncate">
              {{ wallet.description }}
            </p>
          </div>
          
          <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity flex-shrink-0">
            <Button
              variant="ghost"
              size="icon"
              class="h-6 w-6"
              @click.stop="openEditDialog(wallet)"
            >
              <Pencil class="w-3 h-3" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              class="h-6 w-6"
              @click.stop="handleDeleteWallet(wallet)"
            >
              <Trash2 class="w-3 h-3 text-destructive" />
            </Button>
          </div>
        </div>
      </Card>
    </div>

    <WalletDialog
      v-model:open="showWalletDialog"
      :wallet="editingWallet"
      @wallet-saved="handleWalletSaved"
    />
  </div>
</template>

<style scoped>
.wallet-selector {
  display: flex;
  flex-direction: column;
}
</style>
