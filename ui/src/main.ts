import './assets/global.css'
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';

import App from './App.vue'
import router from './router'

import { VueCsvImportPlugin } from 'vue-csv-import';

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(VueCsvImportPlugin);
app.use(PrimeVue, {
  theme: {
    preset: Aura
  }
});

// Initialize theme (if user previously selected one)
import { useThemeStore } from './stores/theme'
const themeStore = useThemeStore()
themeStore.init()

app.mount('#app')
