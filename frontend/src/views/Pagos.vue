<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-8">
        Registro de Pagos
      </h1>

      <!-- Estadísticas -->
      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
        <PagoStatCard title="Total Ingresos"   :value="totalIngresos.toFixed(2)" prefix="$" subtitle="Acumulado total"           color="green"  />
        <PagoStatCard title="Total Pagos"      :value="pagos.length"             subtitle="Transacciones registradas"            color="blue"   />
        <PagoStatCard title="Promedio / Pago"  :value="promedio"                 prefix="$" subtitle="Monto promedio"            color="orange" />
        <PagoStatCard title="Métodos de Pago"  subtitle="Efectivo · Tarjeta · Transferencia" color="purple" :large="false">
          <div class="flex flex-wrap gap-2 mt-1">
            <span class="inline-flex items-center gap-1 bg-emerald-100 text-emerald-700 text-xs font-bold px-2.5 py-1 rounded-full">💵 {{ conteoMetodo('efectivo') }}</span>
            <span class="inline-flex items-center gap-1 bg-blue-100 text-blue-700 text-xs font-bold px-2.5 py-1 rounded-full">💳 {{ conteoMetodo('tarjeta') }}</span>
            <span class="inline-flex items-center gap-1 bg-amber-100 text-amber-700 text-xs font-bold px-2.5 py-1 rounded-full">🏦 {{ conteoMetodo('transferencia') }}</span>
          </div>
        </PagoStatCard>
      </div>

      <!-- Botón + búsqueda -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
        <router-link
          to="/pagos/registrar"
          class="inline-flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all duration-200 hover:-translate-y-0.5 w-full sm:w-auto"
        >
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Registrar Nuevo Pago
        </router-link>
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por cliente o cédula…"
          class="px-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all w-full sm:w-72"
        />
      </div>

      <!-- Contador -->
      <p class="text-sm text-slate-400 font-medium mb-3 px-1">
        Mostrando <strong class="text-slate-600">{{ pagosFiltrados.length }}</strong> de {{ pagos.length }} pagos
      </p>

      <!-- Tabla -->
      <div class="w-full max-w-full mb-8">
        <p class="text-xs text-slate-400 text-right mb-1 sm:hidden">← desliza para ver más →</p>
        <div class="overflow-x-auto bg-white rounded-2xl shadow-sm">
          <table class="w-full border-collapse" style="min-width: 720px">
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
                <td :colspan="columnas.length" class="text-center py-16 text-slate-400 text-sm">
                  <span class="animate-pulse">Cargando pagos…</span>
                </td>
              </tr>
              <tr v-else-if="pagosFiltrados.length === 0">
                <td :colspan="columnas.length" class="text-center py-16">
                  <p class="text-slate-500 font-semibold">No hay pagos registrados</p>
                  <p class="text-slate-400 text-sm mt-1">Comienza registrando el primer pago</p>
                </td>
              </tr>
              <tr
                v-else
                v-for="(pago, i) in pagosFiltrados"
                :key="pago.id"
                class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50"
                :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
              >
                <td class="px-4 py-3 text-sm font-bold text-slate-700 whitespace-nowrap">#{{ pago.id }}</td>
                <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ pago.cliente_nombre }} {{ pago.cliente_apellido }}</td>
                <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ pago.cedula }}</td>
                <td class="px-4 py-3 text-sm font-semibold text-slate-700 whitespace-nowrap">{{ pago.tipo_membresia }}</td>
                <td class="px-4 py-3 whitespace-nowrap">
                  <span class="text-emerald-600 font-bold">${{ Number(pago.monto).toFixed(2) }}</span>
                </td>
                <td class="px-4 py-3 whitespace-nowrap"><MetodoBadge :metodo="pago.metodo_pago" /></td>
                <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(pago.fecha_pago) }}</td>
                <td class="px-4 py-3 text-xs text-slate-500 whitespace-nowrap">{{ formatFechaCorta(pago.fecha_inicio) }} – {{ formatFechaCorta(pago.fecha_fin) }}</td>
                <td class="px-4 py-3 whitespace-nowrap"><EstadoBadge :estado="pago.estado_membresia" /></td>
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
import Navbar       from '../components/shared/Navbar.vue'
import Footer       from '../components/shared/Footer.vue'
import PagoStatCard from '../components/pagos/PagoStatCard.vue'
import MetodoBadge  from '../components/pagos/MetodoBadge.vue'
import EstadoBadge  from '../components/pagos/EstadoBadge.vue'

const router = useRouter()
const auth   = useAuthStore()
function logout() { auth.logout(); router.push('/login') }

const pagos    = ref([])
const loading  = ref(false)
const busqueda = ref('')

const columnas = ['ID', 'Cliente', 'Cédula', 'Membresía', 'Monto', 'Método', 'Fecha Pago', 'Vigencia', 'Estado']

const mockPagos = [
  { id:1, cliente_nombre:'Juan',   cliente_apellido:'Pérez',  cedula:'1234567890', tipo_membresia:'Premium',    monto:70.00,  metodo_pago:'efectivo',      fecha_pago:'2026-05-19 10:30:00', fecha_inicio:'2026-05-19', fecha_fin:'2026-06-18', estado_membresia:'activa'  },
  { id:2, cliente_nombre:'María',  cliente_apellido:'López',  cedula:'0987654321', tipo_membresia:'Básica',     monto:25.00,  metodo_pago:'tarjeta',       fecha_pago:'2026-05-18 14:00:00', fecha_inicio:'2026-05-18', fecha_fin:'2026-06-17', estado_membresia:'activa'  },
  { id:3, cliente_nombre:'Carlos', cliente_apellido:'Ruiz',   cedula:'1122334455', tipo_membresia:'Mensual',    monto:40.00,  metodo_pago:'transferencia', fecha_pago:'2026-04-01 09:15:00', fecha_inicio:'2026-04-01', fecha_fin:'2026-05-01', estado_membresia:'vencida' },
  { id:4, cliente_nombre:'Ana',    cliente_apellido:'Torres', cedula:'5566778899', tipo_membresia:'Trimestral', monto:100.00, metodo_pago:'tarjeta',       fecha_pago:'2026-03-01 11:00:00', fecha_inicio:'2026-03-01', fecha_fin:'2026-06-01', estado_membresia:'activa'  },
  { id:5, cliente_nombre:'Luis',   cliente_apellido:'Gómez',  cedula:'1111111111', tipo_membresia:'Básica',     monto:25.00,  metodo_pago:'efectivo',      fecha_pago:'2026-02-15 08:45:00', fecha_inicio:'2026-02-15', fecha_fin:'2026-03-15', estado_membresia:'vencida' },
]

onMounted(async () => {
  loading.value = true
  await new Promise(r => setTimeout(r, 350))
  pagos.value   = mockPagos
  loading.value = false
})

const totalIngresos = computed(() => pagos.value.reduce((s, p) => s + Number(p.monto), 0))
const promedio      = computed(() => pagos.value.length ? (totalIngresos.value / pagos.value.length).toFixed(2) : '0.00')
function conteoMetodo(m) { return pagos.value.filter(p => p.metodo_pago === m).length }

const pagosFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  if (!q) return pagos.value
  return pagos.value.filter(p =>
    `${p.cliente_nombre} ${p.cliente_apellido}`.toLowerCase().includes(q) || p.cedula.includes(q),
  )
})

function formatFecha(str)      { if (!str) return '—'; return new Date(str).toLocaleString('es-EC',   { day:'2-digit', month:'2-digit', year:'numeric', hour:'2-digit', minute:'2-digit' }) }
function formatFechaCorta(str) { if (!str) return '—'; return new Date(str).toLocaleDateString('es-EC', { day:'2-digit', month:'2-digit', year:'numeric' }) }
</script>
