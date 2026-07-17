<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useAdminStore } from '@/stores/adminStore'
import Navbar       from '@/components/admin/AdminNavbar.vue'
import Footer       from '@/components/shared/Footer.vue'
import PagoStatCard from '@/components/pagos/PagoStatCard.vue'
import MetodoBadge  from '@/components/pagos/MetodoBadge.vue'
import EstadoBadge  from '@/components/pagos/EstadoBadge.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'

const router = useRouter()
const auth   = useAuthStore()
const admin  = useAdminStore()

function logout() { auth.logout(); router.push('/login') }

const columnas = ['ID', 'Cliente', 'Cédula', 'Membresía', 'Monto', 'Método', 'Fecha Pago', 'Vigencia', 'Estado']

const totalIngresos = computed(() => admin.stats?.ingresos ?? 0)
const totalPagos    = computed(() => admin.stats?.pagosTotales ?? 0)
const promedio      = computed(() => (totalIngresos.value / Math.max(1, totalPagos.value)).toFixed(2))

const filtroSucursal = computed(() =>
  auth.esSucursal ? { sucursalId: auth.sucursalId } : {}
)

function formatFecha(str: string)      { if (!str) return '—'; return new Date(str).toLocaleString('es-EC',   { day:'2-digit', month:'2-digit', year:'numeric', hour:'2-digit', minute:'2-digit' }) }
function formatFechaCorta(str: string) { if (!str) return '—'; return new Date(str).toLocaleDateString('es-EC', { day:'2-digit', month:'2-digit', year:'numeric' }) }

onMounted(() => admin.fetchStats())
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-8">
        Registro de Pagos
      </h1>

      <!-- Estadísticas -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <PagoStatCard title="Total Ingresos"  :value="totalIngresos.toFixed(2)" prefix="$" subtitle="Acumulado total"            color="green"  />
        <PagoStatCard title="Total Transacciones"     :value="totalPagos"       subtitle="Transacciones registradas"             color="blue"   />
        <PagoStatCard title="Promedio / Pago" :value="promedio"                 prefix="$" subtitle="Monto promedio"             color="orange" />
      </div>

      <!-- Botón -->
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
      </div>

      <!-- Tabla Paginada -->
      <div class="mb-8">
        <PaginatedTable
          endpoint="/pagos"
          :filtros="filtroSucursal"
          :columns="columnas"
          endpoint-key="pagos"
        >
          <template #default="{ items }">
            <tr
              v-for="(pago, i) in items"
              :key="pago.id"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50 bg-white"
              :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
            >
              <td class="px-4 py-3 text-sm font-bold text-slate-700 whitespace-nowrap">#{{ pago.id }}</td>
              <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ pago.clienteNombre }} {{ pago.clienteApellido }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ pago.cedula }}</td>
              <td class="px-4 py-3 text-sm font-semibold text-slate-700 whitespace-nowrap">{{ pago.tipoMembresia }}</td>
              <td class="px-4 py-3 whitespace-nowrap">
                <span class="text-emerald-600 font-bold">${{ Number(pago.monto).toFixed(2) }}</span>
              </td>
              <td class="px-4 py-3 whitespace-nowrap"><MetodoBadge :metodo="pago.metodoPago" /></td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(pago.fechaPago) }}</td>
              <td class="px-4 py-3 text-xs text-slate-500 whitespace-nowrap">{{ formatFechaCorta(pago.fechaInicio) }} – {{ formatFechaCorta(pago.fechaFin) }}</td>
              <td class="px-4 py-3 whitespace-nowrap"><EstadoBadge :estado="pago.estadoMembresia" /></td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

    </main>

    <Footer />
  </div>
</template>

