<script setup lang="ts">
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
  type ChartOptions
} from 'chart.js'
import type Transaction from '@/model/accounting/Transaction'
import { TransactionType } from '@/model/accounting/Transaction'

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const props = defineProps<{
  transactions: Transaction[]
}>()

// Process transactions to get daily income and expense
const chartData = computed(() => {
  if (!props.transactions || props.transactions.length === 0) {
    return {
      labels: [],
      datasets: []
    }
  }

  // Group transactions by date
  const dailyData = new Map<string, { income: number; expense: number }>()

  props.transactions.forEach((transaction) => {
    const dateStr = new Date(transaction.date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    })

    if (!dailyData.has(dateStr)) {
      dailyData.set(dateStr, { income: 0, expense: 0 })
    }

    const data = dailyData.get(dateStr)!
    if (transaction.type === TransactionType.CREDIT) {
      data.income += transaction.amount
    } else {
      data.expense += Math.abs(transaction.amount)
    }
  })

  // Sort by date
  const sortedEntries = Array.from(dailyData.entries()).sort((a, b) => {
    return new Date(a[0]).getTime() - new Date(b[0]).getTime()
  })

  const labels = sortedEntries.map(([date]) => date)
  const incomeData = sortedEntries.map(([, data]) => data.income)
  const expenseData = sortedEntries.map(([, data]) => data.expense)

  return {
    labels,
    datasets: [
      {
        label: 'Income',
        data: incomeData,
        borderColor: 'hsl(var(--chart-1))',
        backgroundColor: 'hsla(var(--chart-1), 0.1)',
        tension: 0.4,
        fill: true,
        pointRadius: 4,
        pointHoverRadius: 6,
        borderWidth: 2
      },
      {
        label: 'Expense',
        data: expenseData,
        borderColor: 'hsl(var(--chart-2))',
        backgroundColor: 'hsla(var(--chart-2), 0.1)',
        tension: 0.4,
        fill: true,
        pointRadius: 4,
        pointHoverRadius: 6,
        borderWidth: 2
      }
    ]
  }
})

const chartOptions = computed<ChartOptions<'line'>>(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top',
      labels: {
        usePointStyle: true,
        padding: 15,
        font: {
          size: 12,
          family: 'Inter, system-ui, sans-serif'
        },
        color: 'hsl(var(--foreground))'
      }
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      backgroundColor: 'hsl(var(--background))',
      titleColor: 'hsl(var(--foreground))',
      bodyColor: 'hsl(var(--foreground))',
      borderColor: 'hsl(var(--border))',
      borderWidth: 1,
      padding: 12,
      displayColors: true,
      callbacks: {
        label: function (context) {
          let label = context.dataset.label || ''
          if (label) {
            label += ': '
          }
          if (context.parsed.y !== null) {
            label += new Intl.NumberFormat('en-US', {
              style: 'currency',
              currency: 'EUR'
            }).format(context.parsed.y)
          }
          return label
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: 'hsl(var(--muted-foreground))',
        font: {
          size: 11
        }
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: 'hsl(var(--border))',
        drawBorder: false
      },
      ticks: {
        color: 'hsl(var(--muted-foreground))',
        font: {
          size: 11
        },
        callback: function (value) {
          return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR',
            notation: 'compact'
          }).format(value as number)
        }
      }
    }
  },
  interaction: {
    mode: 'nearest',
    axis: 'x',
    intersect: false
  }
}))
</script>

<template>
  <div class="transaction-chart">
    <div v-if="chartData.labels.length === 0" class="empty-state">
      <p class="text-sm text-muted-foreground">No transaction data available for the selected period</p>
    </div>
    <div v-else class="chart-container">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<style scoped>
.transaction-chart {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(
    180deg,
    rgba(var(--module-color-rgb, 46, 125, 50), var(--module-tint-alpha, 0.03)),
    rgba(0, 0, 0, 0.02)
  );
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-container {
  height: 280px;
  width: 100%;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

@media (max-width: 768px) {
  .chart-container {
    height: 240px;
  }
}
</style>
