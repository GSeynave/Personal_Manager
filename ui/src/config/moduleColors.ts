export enum ModuleId {
  Accounting = 'accounting',
  Finance = 'finance',
  Energy = 'energy',
  Todo = 'todo',
  Calendar = 'calendar',
  Diet = 'diet',
  Sleep = 'sleep',
  Fitness = 'fitness',
  Projects = 'projects',
  Notes = 'notes',
  Habits = 'habits',
  Health = 'health',
  Pomodoro = 'pomodoro',
  Goals = 'goals',
  Learning = 'learning',
  Poi = 'poi',
}

// Centralized palette: one color per module. Keep colors distinct and coherent per category.
export const MODULE_COLORS: Record<string, string> = {
  [ModuleId.Accounting]: '#2E7D32', // green
  [ModuleId.Finance]: '#2E7D32',
  [ModuleId.Energy]: '#1976D2', // blue
  [ModuleId.Todo]: '#6A1B9A', // purple
  [ModuleId.Calendar]: '#00897B', // teal
  [ModuleId.Diet]: '#D81B60', // rose
  [ModuleId.Sleep]: '#5C6BC0', // indigo
  [ModuleId.Fitness]: '#FB8C00', // orange
  [ModuleId.Projects]: '#0288D1', // light blue
  [ModuleId.Notes]: '#8BC34A', // lime
  [ModuleId.Habits]: '#7B1FA2', // deep purple
  [ModuleId.Health]: '#E53935', // red
  [ModuleId.Pomodoro]: '#F4511E', // tomato
  [ModuleId.Goals]: '#FFD600', // amber
  [ModuleId.Learning]: '#3F51B5', // indigo
  [ModuleId.Poi]: '#009688', // teal
}

export function hexToRgbStr(hex: string) {
  const h = hex.replace('#', '')
  const full = h.length === 3 ? h.split('').map((c) => c + c).join('') : h
  const bigint = parseInt(full, 16)
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255
  return `${r}, ${g}, ${b}`
}

export function moduleColor(id: string) {
  return MODULE_COLORS[id] ?? '#6b7280'
}

// Compute a tint alpha (0.03 - 0.08) that compensates for perceived luminance so
// lighter colors get a smaller alpha and darker colors get a larger alpha. This
// makes the faint colored card effect appear similarly visible across hues.
export function moduleTintAlpha(hex: string, minAlpha = 0.03, maxAlpha = 0.08) {
  const h = hex.replace('#', '')
  const full = h.length === 3 ? h.split('').map((c) => c + c).join('') : h
  const bigint = parseInt(full, 16)
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255

  // convert sRGB component to linear value
  const toLinear = (v: number) => {
    const s = v / 255
    return s <= 0.03928 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4)
  }

  const rl = toLinear(r)
  const gl = toLinear(g)
  const bl = toLinear(b)
  // relative luminance
  const lum = 0.2126 * rl + 0.7152 * gl + 0.0722 * bl

  // invert luminance: darker colors -> higher factor
  const factor = 1 - lum
  const alpha = Math.min(maxAlpha, Math.max(minAlpha, minAlpha + (maxAlpha - minAlpha) * factor))
  // return rounded to 3 decimals for nicer CSS values
  return Math.round(alpha * 1000) / 1000
}
