<script setup lang="ts">
import { onMounted, ref } from 'vue'

const emit = defineEmits(['onDateParamUpdate'])
const monthNames: Record<string, number> = {
  January: 1,
  February: 2,
  March: 3,
  April: 4,
  May: 5,
  June: 6,
  July: 7,
  August: 8,
  September: 9,
  October: 10,
  November: 11,
  December: 12,
}

const transactionMonth = ref<string>(new Date().toLocaleString('default', { month: '2-digit' }))
const transactionYear = ref<string>(new Date().getFullYear().toString())
function getMinMaxDate() {
  const date = new Date(parseInt(transactionYear.value), parseInt(transactionMonth.value), 0)
  const minDate = date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + '01'
  const maxDate =
    date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + date.getDate()
  updateDateParam(minDate, maxDate)
}

function updateDateParam(minDate: string, maxDate: string) {
  emit('onDateParamUpdate', { minDate, maxDate })
}

onMounted(getMinMaxDate)
</script>

<template>
  <div class="form-container">
    <div class="form-title">Select the month of the report</div>
    <form @change.prevent="getMinMaxDate">
      <div class="form-row">
        <div class="form-field">
          <label for="month">Month</label>
          <select id="month" v-model="transactionMonth" required>
            <option
              v-for="(monthNumber, monthName) in monthNames"
              :key="monthName"
              :value="monthNumber"
            >
              {{ monthName }}
            </option>
          </select>
        </div>
        <div class="form-field">
          <label for="year">Year</label>
          <select id="year" v-model="transactionYear" required>
            <option value="2025">2025</option>
            <option value="2026">2026</option>
          </select>
        </div>
      </div>
      <button type="submit" class="fetch-btn">
        <span class="btn-icon">📊</span>
        <span>Fetch Summary</span>
      </button>
    </form>
  </div>
</template>

<style scoped>
.form-container {
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(
    180deg,
    rgba(var(--module-color-rgb, 46, 125, 50), var(--module-tint-alpha, 0.06)),
    rgba(0, 0, 0, 0.02)
  );
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  margin-bottom: 16px;
}

.form-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--module-color, #2e7d32);
  text-align: center;
}

form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: stretch;
}

.form-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.form-field {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field label {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-field select {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(var(--module-color-rgb, 46, 125, 50), 0.2);
  background: rgba(255, 255, 255, 0.03);
  color: var(--text-primary);
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.form-field select:hover {
  background: rgba(var(--module-color-rgb, 46, 125, 50), 0.08);
  border-color: rgba(var(--module-color-rgb, 46, 125, 50), 0.4);
}

.form-field select:focus {
  outline: none;
  border-color: var(--module-color, #2e7d32);
  box-shadow: 0 0 0 3px rgba(var(--module-color-rgb, 46, 125, 50), 0.12);
}

.fetch-btn {
  padding: 12px 24px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(
    135deg,
    var(--module-color, #2e7d32),
    rgba(var(--module-color-rgb, 46, 125, 50), 0.85)
  );
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 6px 18px rgba(var(--module-color-rgb, 46, 125, 50), 0.3);
}

.fetch-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(var(--module-color-rgb, 46, 125, 50), 0.4);
}

.fetch-btn:active {
  transform: translateY(0);
}

.btn-icon {
  font-size: 1.2rem;
}

@media (max-width: 600px) {
  .form-row {
    flex-direction: column;
  }
}
</style>
