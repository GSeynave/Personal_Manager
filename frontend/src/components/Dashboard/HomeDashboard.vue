<script setup lang="ts">
import { ref, reactive, type Component, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import TodoService from '@/services/TodoService'
import { moduleColor } from '@/config/moduleColors'
// Implemented modules only
import TodoCard from './TodoCard.vue'
import HabitsCard from './HabitsCard.vue'

type Widget = {
  id: string
  key: string
  label: string
  component: Component
  dataPeak?: string
  props?: Record<string, unknown>
}

// Dashboard widgets for implemented modules only
const widgets = reactive<Widget[]>([
  {
    id: 'w-todo',
    key: 'todo',
    label: 'Todo',
    component: TodoCard,
    dataPeak: 'Next: Buy milk',
    props: { data: { title: 'Buy milk', priority: 'high', daysUntilDue: 1, isOverdue: false } },
  },
  {
    id: 'w-habits',
    key: 'habits',
    label: 'Habits',
    component: HabitsCard,
    dataPeak: '🔥 5d streak',
    props: { data: { currentStreak: 5, bestStreak: 12, completedToday: true } },
  },
])

// Module visibility/order store controls which widgets are shown and in which order
import { useModuleStore } from '@/stores/modules'
const moduleStore = useModuleStore()

const router = useRouter()

// map widget keys to routes (clicking a card navigates to its view)
const routeMap: Record<string, string | undefined> = {
  todo: '/todo',
  habits: '/habits',
}

// Module colors are centralized in src/config/moduleColors.ts and imported above

function onCardClick(w: Widget) {
  // avoid navigating while dragging
  if (draggingKey.value) return
  const path = routeMap[w.key]
  if (path) router.push(path)
}

const visibleWidgets = computed(() => {
  // Force dashboard re-render when visibleWidgets changes
  const dashboardKey = ref(0)
  watch(
    visibleWidgets,
    () => {
      dashboardKey.value++
    },
    { deep: true },
  )
  const byKey = new Map(widgets.map((w) => [w.key, w]))
  const ordered: Widget[] = []
  // First, include keys in moduleStore.order (if widget exists and enabled)
  for (const k of moduleStore.order) {
    const w = byKey.get(k)
    if (w && moduleStore.enabled[k]) ordered.push({ ...w, props: { ...w.props } })
  }
  // Then append any other enabled widgets not in the order
  for (const w of widgets) {
    if (!moduleStore.order.includes(w.key) && moduleStore.enabled[w.key])
      ordered.push({ ...w, props: { ...w.props } })
  }
  return ordered
})

// Force dashboard re-render when visibleWidgets changes
const dashboardKey = ref(0)
watch(
  visibleWidgets,
  () => {
    dashboardKey.value++
  },
  { deep: true },
)

const draggingKey = ref<string | null>(null)

function headerPeak(w: Widget) {
  // Prefer live props.data values when available so header stays in sync with fetched previews
  const propsData = (w.props && (w.props as Record<string, unknown>).data) as
    | Record<string, unknown>
    | undefined
  if (propsData) {
    if (typeof propsData.next === 'string') return `Next: ${propsData.next}`
    if (typeof propsData.revenue === 'string') return `Revenue ${propsData.revenue}`
    if (typeof propsData.consumed === 'string' && typeof propsData.allowed === 'string')
      return `${propsData.consumed} / ${propsData.allowed} kcal`
    if (typeof propsData.status === 'string') return propsData.status
    if (typeof propsData.title === 'string') return propsData.title
  }
  return w.dataPeak
}

// Services (simple, can be replaced by Pinia stores or composables)
const todoService = new TodoService()

// Fetch small previews (data peaks) from backend services and populate widget props.
onMounted(async () => {
  try {
    // Todos: pick the next (first) pending todo and compute priority + days until due
    const todos = await todoService.getTodos()
    const todoWidget = widgets.find((w) => w.key === 'todo')
    if (todoWidget && Array.isArray(todos) && todos.length > 0) {
      const firstTodo = todos[0]
      if (firstTodo) {
        // Calculate priority: can infer from title keywords or default to 'medium'.
        // For now, use mock priority; later add a priority field to Todo model.
        const priority = firstTodo.title?.toLowerCase().includes('urgent')
          ? 'high'
          : firstTodo.title?.toLowerCase().includes('soon')
            ? 'medium'
            : 'low'
        // Calculate days until due
        let daysUntilDue = 7
        let isOverdue = false
        if (firstTodo.due_date) {
          const dueDate = new Date(firstTodo.due_date)
          const now = new Date()
          now.setHours(0, 0, 0, 0)
          dueDate.setHours(0, 0, 0, 0)
          const diff = Math.floor((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
          daysUntilDue = Math.max(0, diff)
          isOverdue = diff < 0
        }
        todoWidget.props = {
          ...todoWidget.props,
          data: {
            title: firstTodo.title,
            priority,
            daysUntilDue,
            isOverdue,
          },
        }
      }
    }

    // TODO: Add data fetching for habits widget when API is ready
  } catch (err) {
    // Non-blocking: keep sample data but log for debugging
    console.error('Error fetching dashboard previews', err)
  }
})

function onDragStart(e: DragEvent, key: string) {
  draggingKey.value = key
  try {
    e.dataTransfer?.setData('text/plain', String(key))
    e.dataTransfer!.effectAllowed = 'move'
  } catch {}
}

function onDragOver(e: DragEvent) {
  e.preventDefault() // allow drop
  e.dataTransfer!.dropEffect = 'move'
}

function onDrop(e: DragEvent, targetKey: string) {
  e.preventDefault()
  const fromKey = draggingKey.value ?? String(e.dataTransfer?.getData('text/plain'))
  if (!fromKey || fromKey === targetKey) {
    draggingKey.value = null
    return
  }
  const fromIndex = widgets.findIndex((w) => w.key === fromKey)
  const targetIndex = widgets.findIndex((w) => w.key === targetKey)
  if (fromIndex === -1 || targetIndex === -1) {
    draggingKey.value = null
    return
  }
  const item = widgets.splice(fromIndex, 1)[0]!
  widgets.splice(targetIndex, 0, item)
  draggingKey.value = null
}

function onDragEnd() {
  draggingKey.value = null
}
</script>

<template>
  <div class="bento-dashboard">
    <header class="bento-header">
      <h1>Home Dashboard</h1>
      <p class="subtitle">Arrange cards by dragging — designed for tablet display</p>
    </header>

    <section class="bento-grid" aria-label="Bento dashboard" :key="dashboardKey">
      <div
        v-for="w in visibleWidgets"
        :key="w.id + '-' + JSON.stringify(w.props?.data)"
        class="widget-card"
        :class="{ dragging: draggingKey === w.key, clickable: !!routeMap[w.key] }"
        :style="{ '--module-color': moduleColor(w.key) }"
        draggable="true"
        @dragstart="(e) => onDragStart(e, w.key)"
        @dragover="onDragOver"
        @drop="(e) => onDrop(e, w.key)"
        @dragend="onDragEnd"
        @click="() => onCardClick(w)"
        role="button"
        tabindex="0"
      >
        <div class="widget-header">
          <strong class="widget-title">{{ w.label }}</strong>
          <span class="data-peak">{{ headerPeak(w) }}</span>
        </div>

        <div class="widget-body">
          <!-- Render the card component in compact mode and pass widget-specific props -->
          <component :is="w.component" v-bind="w.props" :compact="true" :data-peak="w.dataPeak" />
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.bento-dashboard {
  padding: 20px;
  background: transparent;
  min-height: 100vh;
}

.bento-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 24px;
}

.bento-header h1 {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.bento-header .subtitle {
  margin: 0;
  font-size: 0.95rem;
  color: #666;
  font-weight: 500;
}

.bento-grid {
  display: grid;
  gap: 16px;
  /* 4 columns for optimal layout with 15 cards */
  grid-template-columns: repeat(4, 1fr);
}

.widget-card {
  /* neutral card shell; inner component roots are made transparent below */
  background: var(--card-bg, rgba(255, 255, 255, 0.02));
  border-radius: 14px;
  padding: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  min-height: 140px;
  min-width: 220px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  touch-action: manipulation;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease;
  cursor: grab;
  position: relative;
}

.widget-card.clickable {
  cursor: pointer;
}

.widget-card:hover {
  transform: translateY(-4px);
  filter: drop-shadow(0 12px 40px rgba(0, 0, 0, 0.15));
}

.widget-card.dragging {
  opacity: 0.7;
  transform: scale(0.97) rotate(2deg);
  filter: drop-shadow(0 20px 50px rgba(0, 0, 0, 0.2));
  z-index: 100;
}

.widget-header {
  display: none;
}

.widget-body {
  flex: 1 1 auto;
  display: flex;
  align-items: stretch;
  justify-content: stretch;
  overflow: hidden;
  width: 100%;
}

/* Accent bar and dot driven by --module-color set inline on each .widget-card */
.widget-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.03), rgba(0, 0, 0, 0.04)),
    var(--module-color, #7f8c8d);
}
.widget-card::after {
  content: '';
  position: absolute;
  top: 12px;
  right: 12px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--module-color, #7f8c8d);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.45);
}

/* Shared card base rules factored into `card-base.css` to centralize dashboard visuals.
   See src/components/Dashboard/card-base.css
*/

/* Responsive: 2 columns on narrow tablets, 1 column on phones */
@media (max-width: 1400px) {
  .bento-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1000px) {
  .bento-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .bento-grid {
    grid-template-columns: 1fr;
  }
  .widget-card {
    min-height: 120px;
  }
  .bento-header h1 {
    font-size: 1.5rem;
  }
}
</style>

<!-- import the shared card base styles (global for this component) -->
<style src="./card-base.css"></style>
