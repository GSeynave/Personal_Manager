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

// Initialize auth store
import { useAuthStore } from './stores/auth'
import FirebaseService from './services/FirebaseService'
import { setupAxiosInterceptor } from './services/axiosInterceptor'

// Initialize Firebase immediately
FirebaseService.initFirebase()

const authStore = useAuthStore()
authStore.initialize()

// Setup axios interceptor to add auth tokens to requests
setupAxiosInterceptor()

app.mount('#app')
