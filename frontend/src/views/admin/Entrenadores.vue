<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import { httpClient } from '@/services/api'

const router = useRouter()
const auth   = useAuthStore()
function logout() { auth.logout(); router.push('/login') }

const entrenadores      = ref<any[]>([])
const loading           = ref(false)
const message           = ref('')
const messageType       = ref('')
const busqueda          = ref('')
const entrenadorEliminar = ref<any>(null)

async function fetchEntrenadores() {
  loading.value = true
  try {
    const res = await httpClient.get('/entrenadores')
    entrenadores.value = res.data ?? []
  } catch { entrenadores.value = [] }
  finally { loading.value = false }
}

const entrenadorFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  if (!q) return entrenadores.value
  return entrenadores.value.filter(e => {
    const nom = (e.nombreCompleto || `${e.nombre ?? ''} ${e.apellido ?? ''}`).toLowerCase()
    const doc = (e.documentoIdentidad || e.cedula || '')
    return nom.includes(q) || doc.includes(q)
  })
})

async function eliminar() {
  if (!entrenadorEliminar.value) return
  try {
    await httpClient.delete(`/entrenadores/${entrenadorEliminar.value.id}`)
    const nom = entrenadorEliminar.value.nombreCompleto || `${entrenadorEliminar.value.nombre ?? ''} ${entrenadorEliminar.value.apellido ?? ''}`.trim()
    entrenadores.value = entrenadores.value.filter(e => e.id !== entrenadorEliminar.value.id)
    message.value     = `Entrenador ${nom} eliminado.`
    messageType.value = 'success'
  } catch (err: any) {
    message.value     = err?.error || 'No se pudo eliminar.'
    messageType.value = 'error'
  } finally {
    entrenadorEliminar.value = null
    setTimeout(() => message.value = '', 4000)
  }
}

import { computed } from 'vue'
onMounted(fetchEntrenadores)
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 fade-in">

      <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 tracking-tight mb-8">
        Entrenadores
      </h1>

      <!-- Feedback -->
      <Transition name="fade">
        <div v-if="message"
          class="flex items-center gap-3 px-4 py-3 rounded-xl mb-6 text-sm font-medium border"
          :class="messageType === 'success' ? 'bg-emerald-50 text-emerald-800 border-emerald-200' : 'bg-red-50 text-red-800 border-red-200'">
          <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
          <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          {{ message }}
        </div>
      </Transition>

      <!-- Acciones + búsqueda -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
        <router-link to="/entrenadores/registrar"
          class="inline-flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all hover:-translate-y-0.5 w-full sm:w-auto">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          Registrar Entrenador
        </router-link>
        <input v-model="busqueda" type="text" placeholder="Buscar por nombre o cédula…"
          class="px-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all w-full sm:w-72" />
      </div>

      <p class="text-sm text-slate-400 font-medium mb-3">
        Mostrando <strong class="text-slate-600">{{ entrenadorFiltrados.length }}</strong> de {{ entrenadores.length }} entrenadores
      </p>

      <!-- Tabla -->
      <div class="overflow-x-auto bg-white rounded-2xl shadow-sm mb-8">
        <table class="w-full border-collapse" style="min-width:640px">
          <thead class="bg-gradient-to-r from-blue-500 to-blue-700">
            <tr>
              <th v-for="col in ['Nombre', 'Cédula', 'Teléfono', 'Email', 'Especialidad', 'Horario', 'Acciones']" :key="col"
                class="px-4 py-3 text-left text-white font-bold text-xs uppercase tracking-wider whitespace-nowrap">{{ col }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="text-center py-16 text-slate-400 text-sm">
                <div class="flex flex-col items-center gap-2">
                  <div class="w-7 h-7 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                  <span>Cargando entrenadores…</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="entrenadorFiltrados.length === 0">
              <td colspan="7" class="text-center py-16 text-slate-400">
                <p class="font-semibold text-slate-600">No se encontraron entrenadores</p>
              </td>
            </tr>
            <tr v-else v-for="(e, i) in entrenadorFiltrados" :key="e.id"
              class="border-b border-slate-100 hover:bg-blue-50 transition-colors"
              :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'">
              <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ e.nombreCompleto || `${e.nombre ?? ''} ${e.apellido ?? ''}`.trim() }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ e.documentoIdentidad || e.cedula }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ e.telefono || '—' }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ e.email || '—' }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ e.especialidad || '—' }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ e.horario || '—' }}</td>
              <td class="px-4 py-3 whitespace-nowrap">
                <div class="flex gap-2">
                  <router-link :to="`/entrenadores/${e.id}/editar`"
                    class="bg-blue-50 hover:bg-blue-100 text-blue-600 font-semibold text-xs px-3 py-1.5 rounded-lg transition-all cursor-pointer">
                    Editar
                  </router-link>
                  <button @click="entrenadorEliminar = e"
                    class="bg-red-500 hover:bg-red-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all cursor-pointer">
                    Eliminar
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </main>

    <!-- Modal confirmar eliminar -->
    <Transition name="fade">
      <div v-if="entrenadorEliminar" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
          <h3 class="text-lg font-bold text-slate-800 mb-2">¿Eliminar entrenador?</h3>
          <p class="text-sm text-slate-500 mb-5">
            Se eliminará a <strong>{{ entrenadorEliminar.nombreCompleto || `${entrenadorEliminar.nombre ?? ''} ${entrenadorEliminar.apellido ?? ''}`.trim() }}</strong>. Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-3">
            <button @click="eliminar"
              class="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Sí, eliminar
            </button>
            <button @click="entrenadorEliminar = null"
              class="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
