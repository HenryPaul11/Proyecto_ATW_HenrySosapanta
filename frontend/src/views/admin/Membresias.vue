<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const navLinks = computed(() => {
  const links = [
    { to: '/dashboard',    label: 'Inicio',       icon: 'home'       },
    { to: '/clientes',     label: 'Clientes',      icon: 'clientes'   },
    { to: '/entrenadores', label: 'Entrenadores',  icon: 'clientes'   },
    { to: '/membresias',   label: 'Membresías',    icon: 'membresias' },
    { to: '/equipos',      label: 'Equipos',       icon: 'equipos'    },
    { to: '/pagos',        label: 'Pagos',         icon: 'pagos'      },
    { to: '/auditorias',   label: 'Auditorías',    icon: 'auditorias' },
  ]
  if (!auth.esSucursal) {
    links.splice(6, 0, { to: '/sucursales', label: 'Sucursales', icon: 'home' })
  }
  return links
})

const hoy      = new Date()
const fechaHoy = hoy.toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })

function diasRestantes(fin: string) { return Math.max(0, Math.ceil((new Date(fin).getTime() - hoy.getTime()) / 86_400_000)) }
function formatDate(d: string)      { if (!d) return '—'; return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' }) }

const colSinMembresia = ['Nombre', 'Cedula', 'Telefono', 'Email', 'Acciones']
const colActivas      = ['Cliente', 'Cedula', 'Tipo', 'F. Inicio', 'F. Fin', 'Dias', 'Estado']
const colVencidas     = ['Cliente', 'Cedula', 'Tipo', 'F. Inicio', 'F. Fin', 'Estado', 'Accion']

const filtroSucursal = computed(() =>
  auth.esSucursal ? { sucursalId: auth.sucursalId } : {}
)

const tabActiva = ref('sin_membresia')
const tabs = [
  { key: 'sin_membresia', label: 'Sin Membresia', dot: 'bg-amber-500', activeBg: 'bg-amber-500 text-white border-amber-500' },
  { key: 'activas',      label: 'Activas',        dot: 'bg-emerald-500', activeBg: 'bg-emerald-500 text-white border-emerald-500' },
  { key: 'vencidas',     label: 'Vencidas',       dot: 'bg-red-500', activeBg: 'bg-red-500 text-white border-red-500' },
]
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-6">
        Gestión de Membresías
      </h1>

      <!-- Alerta informativa -->
      <div class="bg-white border-l-4 border-blue-500 text-blue-800 rounded-xl px-4 py-3 mb-6 text-sm font-medium shadow-sm">
        <strong class="font-bold">Nota:</strong> Los estados se actualizan automaticamente segun la fecha actual.
        <small class="block mt-1 text-xs opacity-75">Fecha actual del sistema: {{ fechaHoy }}</small>
      </div>

      <!-- Tabs -->
      <div class="flex gap-2 mb-6 flex-wrap">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="tabActiva = tab.key"
          class="flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-bold transition-all duration-200 cursor-pointer border-2"
          :class="tabActiva === tab.key
            ? tab.activeBg + ' shadow-md'
            : 'bg-white text-slate-600 border-slate-200 hover:border-slate-300'"
        >
          <span class="w-2.5 h-2.5 rounded-full" :class="tab.dot"></span>
          {{ tab.label }}
        </button>
      </div>

      <!-- Sin Membresia -->
      <div v-show="tabActiva === 'sin_membresia'" class="mb-4">
        <PaginatedTable
          endpoint="/clientes/sin-membresia"
          :filtros="filtroSucursal"
          :columns="colSinMembresia"
          endpoint-key="clientes"
        >
          <template #default="{ items }">
            <tr
              v-for="c in items"
              :key="c.id"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-amber-50/50 bg-white"
            >
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-800">
                {{ c.nombreCompleto || `${c.nombre ?? ''} ${c.apellido ?? ''}`.trim() }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.documentoIdentidad || c.cedula }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.telefono }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.email }}</td>
              <td class="px-4 py-3.5">
                <router-link
                  :to="{ path: '/membresias/asignar', query: { clienteId: c.id } }"
                  class="bg-emerald-500 hover:bg-emerald-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Asignar Membresia
                </router-link>
              </td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

      <!-- Activas -->
      <div v-show="tabActiva === 'activas'" class="mb-4">
        <PaginatedTable
          endpoint="/membresias"
          :filtros="{ estado: 'activa', ...filtroSucursal }"
          :columns="colActivas"
          endpoint-key="membresias"
        >
          <template #default="{ items }">
            <tr
              v-for="m in items"
              :key="m.id"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50 bg-white"
            >
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-800">
                {{ m.cliente?.nombreCompleto || `${m.cliente?.nombre ?? ''} ${m.cliente?.apellido ?? ''}`.trim() || `${m.clienteNombre ?? ''} ${m.clienteApellido ?? ''}`.trim() || 'Sin cliente' }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">
                {{ m.cliente?.documentoIdentidad || m.clienteCedula || 'Sin cedula' }}
              </td>
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-700">
                {{ m.plan?.nombrePlan || m.tipoMembresiaNombre }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaInicio) }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaFin) }}</td>
              <td class="px-4 py-3.5">
                <span
                  class="inline-block px-2.5 py-1 rounded-full text-xs font-bold"
                  :class="diasRestantes(m.fechaFin) <= 7 ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'"
                >
                  {{ diasRestantes(m.fechaFin) }}d
                </span>
              </td>
              <td class="px-4 py-3.5">
                <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                  Activa
                </span>
              </td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

      <!-- Vencidas -->
      <div v-show="tabActiva === 'vencidas'" class="mb-4">
        <PaginatedTable
          endpoint="/membresias"
          :filtros="{ estado: 'vencida', ...filtroSucursal }"
          :columns="colVencidas"
          endpoint-key="membresias"
        >
          <template #default="{ items }">
            <tr
              v-for="m in items"
              :key="m.id"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-red-50/30 bg-white"
            >
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-800">
                {{ m.cliente?.nombreCompleto || `${m.cliente?.nombre ?? ''} ${m.cliente?.apellido ?? ''}`.trim() || `${m.clienteNombre ?? ''} ${m.clienteApellido ?? ''}`.trim() || 'Sin cliente' }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">
                {{ m.cliente?.documentoIdentidad || m.clienteCedula || 'Sin cedula' }}
              </td>
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-700">
                {{ m.plan?.nombrePlan || m.tipoMembresiaNombre }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaInicio) }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaFin) }}</td>
              <td class="px-4 py-3.5">
                <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-red-100 text-red-800">
                  Vencida
                </span>
              </td>
              <td class="px-4 py-3.5">
                <router-link
                  :to="{ name: 'RenovarMembresia', params: { clienteId: m.cliente?.id } }"
                  class="bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Renovar Membresia
                </router-link>
              </td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

    </main>

    <Footer />
  </div>
</template>

<style scoped>
.membresia-table-body tr {
  background: white;
  transition: background 0.15s ease;
}
.membresia-table-body tr:nth-child(even) { background: #f8fafc; }
.membresia-table-body tr:hover           { background: #eff6ff; }
.membresia-table-body tr:last-child td   { border-bottom: none; }
.membresia-table-body td {
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
  white-space: nowrap;
}
</style>
