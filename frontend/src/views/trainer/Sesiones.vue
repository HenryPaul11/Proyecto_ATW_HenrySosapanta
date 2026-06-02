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
  { label: 'Todas',       value: 'todos'      },
  { label: 'Pendientes',  value: 'pendiente'  },
  { label: 'Completadas', value: 'completada' },
]
const columnas = ['Cliente', 'Fecha', 'Hora', 'Tipo', 'Estado', 'Notas']

const sesionesCompletadas = computed(() => store.sesiones.filter(s => s.estado === 'completada'))
const sesionesPendientes  = computed(() => store.sesiones.filter(s => s.estado === 'pendiente'))

const sesionesFiltradas = computed(() => {
  let lista = store.sesiones
  if (filtroActivo.value !== 'todos') lista = lista.filter(s => s.estado === filtroActivo.value)
  const q = busqueda.value.trim().toLowerCase()
  if (q) lista = lista.filter(s => s.cliente_nombre.toLowerCase().includes(q))
  return lista.sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
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

      <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight mb-8">
        📅 Sesiones de Entrenamiento
      </h1>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-emerald-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Completadas</p>
          <p class="text-2xl font-black text-emerald-600">{{ sesionesCompletadas.length }}</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-amber-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Pendientes</p>
          <p class="text-2xl font-black text-amber-600">{{ sesionesPendientes.length }}</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-blue-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Total</p>
          <p class="text-2xl font-black text-blue-600">{{ store.sesiones.length }}</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="h-32 bg-slate-200 animate-pulse rounded-2xl" />

      <template v-else>
        <!-- Filtros -->
        <div class="flex flex-col sm:flex-row gap-3 mb-4">
          <input v-model="busqueda" type="text" placeholder="Buscar por cliente…"
            class="px-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all w-full sm:w-64" />
          <div class="flex gap-2 flex-wrap">
            <button v-for="f in filtros" :key="f.value" @click="filtroActivo = f.value"
              class="px-3 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer"
              :class="filtroActivo === f.value
                ? 'bg-emerald-500 text-white shadow-sm'
                : 'bg-white text-slate-500 border border-slate-200 hover:border-emerald-300'">
              {{ f.label }}
            </button>
          </div>
        </div>

        <div v-if="sesionesFiltradas.length === 0"
          class="bg-white rounded-2xl border-2 border-dashed border-slate-300 p-10 text-center">
          <div class="text-4xl mb-3">📭</div>
          <p class="text-slate-500 font-semibold">No se encontraron sesiones.</p>
        </div>

        <template v-else>
          <p class="text-xs text-slate-400 text-right mb-1 sm:hidden">← desliza para ver más →</p>
          <div class="overflow-x-auto bg-white rounded-2xl shadow-sm">
            <table class="w-full border-collapse" style="min-width: 560px">
              <thead class="bg-gradient-to-r from-emerald-500 to-emerald-700">
                <tr>
                  <th v-for="col in columnas" :key="col"
                    class="px-4 py-3 text-left text-white font-bold text-xs uppercase tracking-wider whitespace-nowrap">
                    {{ col }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(s, i) in sesionesFiltradas" :key="s.id"
                  class="border-b border-slate-100 transition-colors hover:bg-emerald-50"
                  :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'">
                  <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ s.cliente_nombre }}</td>
                  <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(s.fecha) }}</td>
                  <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ s.hora }}</td>
                  <td class="px-4 py-3 whitespace-nowrap">
                    <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-blue-100 text-blue-700">
                      {{ s.tipo }}
                    </span>
                  </td>
                  <td class="px-4 py-3 whitespace-nowrap">
                    <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold"
                      :class="s.estado === 'completada' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'">
                      {{ s.estado === 'completada' ? '✅ Completada' : '⏳ Pendiente' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-xs text-slate-400 max-w-[180px] truncate">{{ s.notas || '—' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
      </template>

    </main>
    <Footer />
  </div>
</template>
