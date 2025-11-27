<script setup lang="ts">
import { ref } from 'vue'
import UserService from '../services/UserService'
import CardContent from './Dashboard/CardContent.vue'

const userService = new UserService()
const userTag = ref<string>('')
const emit = defineEmits(['identity-set'])

async function setUserTag() {
  try {
    await userService.updateUserTag(userTag.value)
    console.log('User tag set')
    emit('identity-set')
  } catch (err) {
    console.error('Error setting user tag:', err)
  }
}
</script>

<template>
  <CardContent :title="'Define User Tag'">
    <form class="user-tag-form-container" @submit.prevent="setUserTag">
      <div class="form-group">
        <label for="userTag">User Tag:</label>
        <input type="text" id="userTag" v-model="userTag" required />
      </div>
      <button type="submit" class="submit-button">Validate</button>
    </form>
  </CardContent>
</template>

<style scoped>
.user-tag-form-container {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.submit-button {
  align-self: flex-end;
  padding: 0.5rem 1rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  align-self: center;
}
</style>
