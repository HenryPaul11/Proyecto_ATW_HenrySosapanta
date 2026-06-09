<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useEntrenadorStore } from '@/stores/entrenadorStore'
import TrainerNavbar from '@/components/trainer/TrainerNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useEntrenadorStore()

function logout() { auth.logout(); router.push('/login') }

const busqueda     = ref('')
const filtroActivo = ref('todos')
const filtros = [
  { label: 'Todos',    value: 'todos'   },
  { label: 'Activos',  value: 'activa'  },
  { label: 'Vencidos', value: 'vencida' },
]

const clientesActivos  = computed(() => store.clientes.filter(c => c.estado_membresia === 'activa'))
const clientesVencidos = computed(() => store.clientes.filter(c => c.estado_membresia === 'vencida'))

const clientesFiltrados = computed(() => {
  let lista = store.clientes
  if (filtroActivo.value !== 'todos') lista = lista.filter(c => c.estado_membresia === filtroActivo.value)
  const q = busqueda.value.trim().toLowerCase()
  if (q) lista = lista.filter(c => `${c.nombre} ${c.apellido}`.toLowerCase().includes(q) || c.cedula.includes(q))
  return lista
})

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(() => store.fetchAll(auth.usuario))
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-emerald-50 to-slate-100">
    <TrainerNavbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight mb-8 flex items-center gap-2">
        <img src="/icons/grupo.svg" class="w-8 h-8 icon-slate" alt="" />
        Mis Clientes
      </h1>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-emerald-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Total asignados</p>
          <p class="text-2xl font-black text-emerald-600">{{ store.clientes.length }}</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-blue-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Membresía activa</p>
          <p class="text-2xl font-black text-blue-600">{{ clientesActivos.length }}</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-red-400">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Membresía vencida</p>
          <p class="text-2xl font-black text-red-500">{{ clientesVencidos.length }}</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="i in 3" :key="i" class="h-36 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <template v-else>
        <!-- Búsqueda + filtros -->
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
          <input v-model="busqueda" type="text" placeholder="Buscar por nombre o cédula…"
            class="px-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all w-full sm:w-72" />
          <div class="flex gap-2">
            <button v-for="f in filtros" :key="f.value" @click="filtroActivo = f.value"
              class="px-3 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer"
              :class="filtroActivo === f.value
                ? 'bg-emerald-500 text-white shadow-sm'
                : 'bg-white text-slate-500 border border-slate-200 hover:border-emerald-300'">
              {{ f.label }}
            </button>
          </div>
        </div>

        <p class="text-sm text-slate-400 font-medium mb-3 px-1">
          Mostrando <strong class="text-slate-600">{{ clientesFiltrados.length }}</strong> clientes
        </p>

        <div v-if="clientesFiltrados.length === 0"
          class="bg-white rounded-2xl border-2 border-dashed border-slate-300 p-10 text-center">
          <img src="/icons/grupo.svg" class="w-12 h-12 mx-auto mb-3 icon-slate" alt="" />
          <p class="text-slate-500 font-semibold">No se encontraron clientes.</p>
        </div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="c in clientesFiltrados" :key="c.id"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-emerald-400 hover:shadow-md transition-all duration-200 p-5">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-11 h-11 rounded-xl bg-gradient-to-br from-emerald-400 to-emerald-600 flex items-center justify-center text-white font-black text-base shrink-0">
                {{ c.nombre.charAt(0) }}
              </div>
              <div>
                <p class="font-bold text-slate-800 text-sm">{{ c.nombre }} {{ c.apellido }}</p>
                <p class="text-xs text-slate-400">{{ c.cedula }}</p>
              </div>
            </div>
            <div class="space-y-2 text-xs mb-4">
              <div class="flex justify-between">
                <span class="text-slate-400 font-semibold">Plan</span>
                <span class="text-slate-700 font-bold">{{ c.plan }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-slate-400 font-semibold">Membresía</span>
                <span class="px-2 py-0.5 rounded-full font-bold"
                  :class="c.estado_membresia === 'activa' ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'">
                  {{ c.estado_membresia === 'activa' ? 'Activa' : 'Vencida' }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-400 font-semibold">Próxima sesión</span>
                <span class="text-slate-700 font-medium">{{ c.proxima_sesion ? formatFecha(c.proxima_sesion) : '—' }}</span>
              </div>
            </div>
            <div v-if="c.estado_membresia === 'vencida'"
              class="bg-red-50 rounded-lg px-3 py-2 text-xs text-red-700 font-semibold flex items-center gap-1.5">
              <svg class="w-3.5 h-3.5 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
              Membresía vencida. No puede entrenar.
            </div>
          </div>
        </div>
      </template>

    </main>
    <Footer />
  </div>
</template>

<style scoped>
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
html.dark .icon-slate { filter: brightness(0) invert(1) opacity(0.8); }
</style>
