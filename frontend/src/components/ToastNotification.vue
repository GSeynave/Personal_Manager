<script setup lang="ts">
import { useNotificationStore } from '@/stores/notifications'
import { computed } from 'vue'

const notificationStore = useNotificationStore()

const notification = computed(() => notificationStore.currentToast)

const getNotificationColor = (type: string) => {
  switch (type) {
    case 'LEVEL_UP':
      return 'bg-gradient-to-r from-purple-500 to-pink-500'
    case 'ACHIEVEMENT_UNLOCKED':
      return 'bg-gradient-to-r from-yellow-400 to-orange-500'
    case 'ESSENCE_GAINED':
      return 'bg-gradient-to-r from-blue-500 to-cyan-500'
    case 'REWARD_UNLOCKED':
      return 'bg-gradient-to-r from-green-500 to-emerald-500'
    default:
      return 'bg-gradient-to-r from-gray-500 to-gray-600'
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
      <div class="toast" :class="getNotificationColor(notification.type)">
        <div class="toast-icon">{{ notification.icon }}</div>
        <div class="toast-content">
          <h4 class="toast-title">{{ notification.title }}</h4>
          <p class="toast-message">{{ notification.message }}</p>
        </div>
        <button class="toast-close" @click.stop="notificationStore.hideToast()">×</button>
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
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2), 0 6px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  color: white;
  animation: bounce 0.5s ease-out;
}

.toast-icon {
  font-size: 2rem;
  line-height: 1;
  flex-shrink: 0;
  animation: pulse 1s infinite;
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
}

.toast-title {
  font-size: 1rem;
  font-weight: 700;
  margin: 0 0 0.25rem 0;
  line-height: 1.2;
}

.toast-message {
  font-size: 0.875rem;
  margin: 0;
  opacity: 0.95;
  line-height: 1.4;
}

.toast-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 1.5rem;
  line-height: 1;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.toast-close:hover {
  background: rgba(255, 255, 255, 0.3);
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
