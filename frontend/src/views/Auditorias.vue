<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-extrabold text-slate-800 tracking-tight text-center mb-8">
        Historial de Auditorías
      </h1>

      <!-- Filtros -->
      <div class="bg-white rounded-2xl shadow-sm p-4 sm:p-5 mb-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <!-- Usuario -->
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Usuario</label>
            <input
              v-model="filtroUsuario"
              type="text"
              placeholder="Filtrar por usuario…"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
            />
          </div>
          <!-- Tabla -->
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Tabla</label>
            <select
              v-model="filtroTabla"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white"
            >
              <option value="">Todas</option>
              <option v-for="t in tablasUnicas" :key="t" :value="t">{{ t }}</option>
            </select>
          </div>
          <!-- Acción -->
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Acción</label>
            <select
              v-model="filtroAccion"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white"
            >
              <option value="">Todas</option>
              <option value="INSERT">INSERT</option>
              <option value="UPDATE">UPDATE</option>
              <option value="DELETE">DELETE</option>
            </select>
          </div>
          <!-- Limpiar -->
          <div class="flex items-end">
            <button
              @click="limpiarFiltros"
              class="w-full px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-600 font-semibold text-sm rounded-xl transition-all cursor-pointer"
            >
              Limpiar filtros
            </button>
          </div>
        </div>
      </div>

      <!-- Contador -->
      <p class="text-sm text-slate-400 font-medium mb-3 px-1">
        Mostrando <strong class="text-slate-600">{{ registrosFiltrados.length }}</strong>
        de {{ auditorias.length }} registros
      </p>

      <!-- Tabla -->
      <div class="w-full max-w-full mb-8">
        <p class="text-xs text-slate-400 text-right mb-1 sm:hidden">← desliza para ver más →</p>
        <div class="overflow-x-auto bg-white rounded-2xl shadow-sm">
          <table class="w-full border-collapse" style="min-width: 680px">
            <thead class="bg-gradient-to-r from-blue-500 to-blue-700">
              <tr>
                <th v-for="col in columnas" :key="col"
                  class="px-4 py-3 text-left text-white font-bold text-xs uppercase tracking-wider whitespace-nowrap">
                  {{ col }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td :colspan="columnas.length" class="text-center py-14 text-slate-400 text-sm">
                  <span class="animate-pulse">Cargando auditorías…</span>
                </td>
              </tr>
              <tr v-else-if="registrosFiltrados.length === 0">
                <td :colspan="columnas.length" class="text-center py-14 text-slate-400 text-sm font-medium">
                  No se encontraron registros de auditoría.
                </td>
              </tr>
              <tr
                v-else
                v-for="(row, i) in registrosFiltrados"
                :key="i"
                class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50"
                :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
              >
                <td class="px-4 py-3 text-sm font-semibold text-slate-700 whitespace-nowrap">{{ row.tabla_afectada }}</td>
                <td class="px-4 py-3 whitespace-nowrap"><AccionBadge :accion="row.accion" /></td>
                <td class="px-4 py-3 text-sm text-slate-600 whitespace-nowrap">{{ row.usuario }}</td>
                <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(row.fecha_hora) }}</td>
                <td class="px-4 py-3 align-top"><DatosAuditoria :datos="row.datos_anteriores" /></td>
                <td class="px-4 py-3 align-top"><DatosAuditoria :datos="row.datos_nuevos" /></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import Navbar         from '../components/shared/Navbar.vue'
import Footer         from '../components/shared/Footer.vue'
import AccionBadge    from '../components/auditoria/AccionBadge.vue'
import DatosAuditoria from '../components/auditoria/DatosAuditoria.vue'

const router = useRouter()
const auth   = useAuthStore()
function logout() { auth.logout(); router.push('/login') }

const auditorias    = ref([])
const loading       = ref(false)
const filtroUsuario = ref('')
const filtroTabla   = ref('')
const filtroAccion  = ref('')

const columnas = ['Tabla', 'Acción', 'Usuario', 'Fecha y Hora', 'Datos Anteriores', 'Datos Nuevos']

const mockAuditorias = [
  { tabla_afectada: 'clientes',  accion: 'INSERT', usuario: 'admin', fecha_hora: '2026-05-19 10:32:00', datos_anteriores: '', datos_nuevos: '{"nombre":"Juan","apellido":"Pérez","cedula":"1234567890","email":"juan@mail.com"}' },
  { tabla_afectada: 'membresias',accion: 'INSERT', usuario: 'admin', fecha_hora: '2026-05-19 10:35:00', datos_anteriores: '', datos_nuevos: '{"cliente_id":"1","tipo_membresia_id":"2","fecha_inicio":"2026-05-19","fecha_fin":"2026-06-18","estado":"activa"}' },
  { tabla_afectada: 'clientes',  accion: 'UPDATE', usuario: 'admin', fecha_hora: '2026-05-18 14:20:00', datos_anteriores: '{"nombre":"María","apellido":"López","telefono":"0991000000"}', datos_nuevos: '{"nombre":"María","apellido":"López","telefono":"0999999999"}' },
  { tabla_afectada: 'membresias',accion: 'UPDATE', usuario: 'admin', fecha_hora: '2026-05-17 09:10:00', datos_anteriores: 'Estado: activa, Fecha fin: 2026-05-01', datos_nuevos: 'Estado: vencida, Fecha fin: 2026-05-01' },
  { tabla_afectada: 'pagos',     accion: 'INSERT', usuario: 'admin', fecha_hora: '2026-05-16 16:45:00', datos_anteriores: '', datos_nuevos: '{"cliente_id":"3","monto":"40.00","metodo":"efectivo","fecha":"2026-05-16"}' },
  { tabla_afectada: 'clientes',  accion: 'DELETE', usuario: 'admin', fecha_hora: '2026-05-15 11:00:00', datos_anteriores: '{"nombre":"Carlos","apellido":"Ruiz","cedula":"1122334455"}', datos_nuevos: '' },
]

onMounted(async () => {
  loading.value = true
  await new Promise(r => setTimeout(r, 400))
  auditorias.value = mockAuditorias
  loading.value = false
})

const tablasUnicas = computed(() => [...new Set(auditorias.value.map(a => a.tabla_afectada))].sort())

const registrosFiltrados = computed(() =>
  auditorias.value.filter(row => {
    const matchU = !filtroUsuario.value || row.usuario.toLowerCase().includes(filtroUsuario.value.toLowerCase())
    const matchT = !filtroTabla.value  || row.tabla_afectada === filtroTabla.value
    const matchA = !filtroAccion.value || row.accion.toUpperCase() === filtroAccion.value
    return matchU && matchT && matchA
  }),
)

function formatFecha(str) {
  if (!str) return '—'
  return new Date(str).toLocaleString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}
function limpiarFiltros() { filtroUsuario.value = ''; filtroTabla.value = ''; filtroAccion.value = '' }
</script>
