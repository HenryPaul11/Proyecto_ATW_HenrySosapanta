<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClienteStore } from '@/stores/clienteStore'
import ClientNavbar from '@/components/client/ClientNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useClienteStore()

function logout() { auth.logout(); router.push('/login') }

const totalPagado = computed(() => store.historialPagos.reduce((s, p) => s + p.monto, 0))
const columnas    = ['#', 'Membresía', 'Monto', 'Método', 'Fecha Pago', 'Vigencia']

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(async () => {
  if (!store.cliente) await store.fetchByUsuario(auth.usuario)
  if (store.cliente) await store.fetchHistorialPagos(store.cliente.id)
})
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-blue-50 to-slate-100">
    <ClientNavbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-4xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight mb-8 flex items-center gap-2">
        <img src="/icons/tarjeta.svg" class="w-6 h-6 icon-slate inline-block mr-1" alt="" />Mis Pagos
      </h1>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-emerald-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Total pagado</p>
          <p class="text-3xl font-black text-emerald-600">${{ totalPagado.toFixed(2) }}</p>
          <p class="text-xs text-slate-400 mt-1">Acumulado histórico</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-blue-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Transacciones</p>
          <p class="text-3xl font-black text-blue-600">{{ store.historialPagos.length }}</p>
          <p class="text-xs text-slate-400 mt-1">Pagos registrados</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-purple-500">
          <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Último pago</p>
          <p class="text-base font-black text-purple-600">
            {{ store.historialPagos.length ? formatFecha(store.historialPagos[0].fechaPago) : '—' }}
          </p>
          <p class="text-xs text-slate-400 mt-1">Fecha más reciente</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="h-32 bg-slate-200 animate-pulse rounded-2xl" />

      <!-- Sin pagos -->
      <div v-else-if="store.historialPagos.length === 0"
        class="bg-white rounded-2xl border-2 border-dashed border-slate-300 p-10 text-center">
        <div class="text-4xl mb-3">
          <img src="/icons/boleto.svg" class="w-12 h-12 mx-auto mb-3 icon-slate" alt="" />
        </div>
        <p class="text-slate-600 font-semibold">No tienes pagos registrados aún.</p>
        <p class="text-slate-400 text-sm mt-1">Tus transacciones aparecerán aquí.</p>
      </div>

      <!-- Tabla -->
      <template v-else>
        <p class="text-xs text-slate-400 text-right mb-1 sm:hidden">← desliza para ver más →</p>
        <div class="overflow-x-auto bg-white rounded-2xl shadow-sm">
          <table class="w-full border-collapse" style="min-width: 560px">
            <thead class="bg-gradient-to-r from-blue-500 to-blue-700">
              <tr>
                <th v-for="col in columnas" :key="col"
                  class="px-4 py-3 text-left text-white font-bold text-xs uppercase tracking-wider whitespace-nowrap">
                  {{ col }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(pago, i) in store.historialPagos" :key="pago.id"
                class="border-b border-slate-100 transition-colors hover:bg-blue-50"
                :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
              >
                <td class="px-4 py-3 text-sm font-bold text-slate-700 whitespace-nowrap">#{{ pago.id }}</td>
                <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ pago.membresia?.plan?.nombrePlan }}</td>
                <td class="px-4 py-3 whitespace-nowrap">
                  <span class="text-emerald-600 font-bold">${{ pago.monto.toFixed(2) }}</span>
                </td>
                <td class="px-4 py-3 whitespace-nowrap">
                  <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold"
                    :class="{
                      'bg-emerald-100 text-emerald-800': pago.metodoPago?.toLowerCase() === 'efectivo',
                      'bg-blue-100 text-blue-800':       pago.metodoPago?.toLowerCase() === 'tarjeta',
                      'bg-amber-100 text-amber-800':     pago.metodoPago?.toLowerCase() === 'transferencia',
                    }">
                    {{ pago.metodoPago?.charAt(0).toUpperCase() + pago.metodoPago?.slice(1) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(pago.fechaPago) }}</td>
                <td class="px-4 py-3 text-xs text-slate-500 whitespace-nowrap">
                  {{ formatFecha(pago.membresia?.fechaInicio) }} – {{ formatFecha(pago.membresia?.fechaFin) }}
                </td>
              </tr>
            </tbody>
          </table>
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
