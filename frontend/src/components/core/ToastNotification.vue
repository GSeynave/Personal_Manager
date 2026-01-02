<script setup lang="ts">
import { useNotificationStore } from '@/stores/notifications'
import { X } from 'lucide-vue-next'
import { computed } from 'vue'

const notificationStore = useNotificationStore()

const notification = computed(() => notificationStore.currentToast)

const getNotificationStyle = (type: string) => {
  switch (type) {
    case 'LEVEL_UP':
      return {
        borderColor: '#A855F7', // Purple
        iconColor: '#A855F7'
      }
    case 'ACHIEVEMENT_UNLOCKED':
      return {
        borderColor: '#F59E0B', // Amber
        iconColor: '#F59E0B'
      }
    case 'ESSENCE_GAINED':
      return {
        borderColor: '#3B82F6', // Blue
        iconColor: '#3B82F6'
      }
    case 'REWARD_UNLOCKED':
      return {
        borderColor: '#10B981', // Green
        iconColor: '#10B981'
      }
    default:
      return {
        borderColor: 'hsl(var(--border))',
        iconColor: 'hsl(var(--foreground))'
      }
  }
}
</script>

<template>
  <Transition name="toast">
    <div
      v-if="notification"
      class="toast-container"
      @click="notificationStore.hideToast()"
    >
      <div 
        class="toast"
        :style="{
          '--toast-border-color': getNotificationStyle(notification.type).borderColor,
          '--toast-icon-color': getNotificationStyle(notification.type).iconColor
        }"
      >
        <div class="toast-icon">{{ notification.icon }}</div>
        <div class="toast-content">
          <h4 class="toast-title">{{ notification.title }}</h4>
          <p class="toast-message">{{ notification.message }}</p>
        </div>
        <button 
          class="toast-close" 
          @click.stop="notificationStore.hideToast()"
          aria-label="Close notification"
        >
          <X class="w-4 h-4" />
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  cursor: pointer;
}

.toast {
  min-width: 320px;
  max-width: 400px;
  padding: 1rem;
  border-radius: 0.75rem;
  background: hsl(var(--card));
  border: 2px solid var(--toast-border-color);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1), 0 6px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  color: hsl(var(--foreground));
  animation: bounce 0.5s ease-out;
  transition: all 0.2s ease;
}

.toast:hover {
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15), 0 8px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.toast-icon {
  font-size: 2rem;
  line-height: 1;
  flex-shrink: 0;
  animation: pulse 1s infinite;
  filter: drop-shadow(0 0 4px var(--toast-icon-color));
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes bounce {
  0% {
    transform: translateY(-100px);
    opacity: 0;
  }
  60% {
    transform: translateY(10px);
    opacity: 1;
  }
  80% {
    transform: translateY(-5px);
  }
  100% {
    transform: translateY(0);
  }
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  line-height: 1.2;
  color: hsl(var(--foreground));
}

.toast-message {
  font-size: 0.875rem;
  margin: 0;
  line-height: 1.4;
  color: hsl(var(--muted-foreground));
}

.toast-close {
  background: transparent;
  border: none;
  color: hsl(var(--muted-foreground));
  width: 24px;
  height: 24px;
  border-radius: 0.375rem;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  padding: 0;
}

.toast-close:hover {
  background: hsl(var(--accent));
  color: hsl(var(--foreground));
}

/* Transition */
.toast-enter-active {
  animation: bounce 0.5s ease-out;
}

.toast-leave-active {
  transition: all 0.3s ease-in;
}

.toast-leave-to {
  transform: translateX(400px);
  opacity: 0;
}

@media (max-width: 768px) {
  .toast-container {
    top: 70px;
    right: 10px;
    left: 10px;
  }

  .toast {
    min-width: auto;
    max-width: none;
  }
}
</style>
