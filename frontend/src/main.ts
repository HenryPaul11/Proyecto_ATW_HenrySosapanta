import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Importar estilos globales
import './assets/main.css'

const app = createApp(App)

// Registrar plugins
app.use(createPinia()) // Pinia para estado global
app.use(router) // Router para navegación

// Montar la aplicación
app.mount('#app')
