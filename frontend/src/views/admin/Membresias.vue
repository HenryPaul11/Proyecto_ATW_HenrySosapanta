<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const hoy      = new Date()
const fechaHoy = hoy.toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })

function diasRestantes(fin: string) { return Math.max(0, Math.ceil((new Date(fin).getTime() - hoy.getTime()) / 86_400_000)) }
function formatDate(d: string)      { if (!d) return '—'; return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' }) }

const colSinMembresia = ['Nombre', 'Cédula', 'Teléfono', 'Email', 'Acciones']
const colActivas      = ['Cliente', 'Cédula', 'Tipo', 'F. Inicio', 'F. Fin', 'Días', 'Estado']
const colVencidas     = ['Cliente', 'Cédula', 'Tipo', 'F. Inicio', 'F. Fin', 'Estado', 'Acción']

const filtroSucursal = computed(() =>
  auth.esSucursal ? { sucursalId: auth.sucursalId } : {}
)
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-6">
        Gestión de Membresías
      </h1>

      <!-- Alerta informativa -->
      <div class="bg-white border-l-4 border-blue-500 text-blue-800 rounded-xl px-4 py-3 mb-8 text-sm font-medium shadow-sm">
        <strong class="font-bold">Nota:</strong> Los estados se actualizan automáticamente según la fecha actual.
        <small class="block mt-1 text-xs opacity-75">Fecha actual del sistema: {{ fechaHoy }}</small>
      </div>

      <!-- Sección 1: Clientes Sin Membresía Activa -->
      <div class="mb-10">
        <h2 class="text-lg sm:text-xl font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span class="w-3 h-3 rounded-full bg-amber-500"></span>
          Clientes Sin Membresía Activa
        </h2>
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
                {{ c.nombre }} {{ c.apellido }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.cedula }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.telefono }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ c.email }}</td>
              <td class="px-4 py-3.5">
                <router-link
                  to="/membresias/asignar"
                  class="bg-emerald-500 hover:bg-emerald-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Asignar Membresía
                </router-link>
              </td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

      <!-- Sección 2: Membresías Activas -->
      <div class="mb-10">
        <h2 class="text-lg sm:text-xl font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
          Membresías Activas
        </h2>
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
                {{ m.clienteNombre }} {{ m.clienteApellido }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ m.clienteCedula }}</td>
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-700">{{ m.tipoMembresiaNombre }}</td>
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

      <!-- Sección 3: Membresías Vencidas -->
      <div class="mb-10">
        <h2 class="text-lg sm:text-xl font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span class="w-3 h-3 rounded-full bg-red-500"></span>
          Membresías Vencidas
        </h2>
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
                {{ m.clienteNombre }} {{ m.clienteApellido }}
              </td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ m.clienteCedula }}</td>
              <td class="px-4 py-3.5 text-sm font-semibold text-slate-700">{{ m.tipoMembresiaNombre }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaInicio) }}</td>
              <td class="px-4 py-3.5 text-sm text-slate-500">{{ formatDate(m.fechaFin) }}</td>
              <td class="px-4 py-3.5">
                <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-red-100 text-red-800">
                  Vencida
                </span>
              </td>
              <td class="px-4 py-3.5">
                <router-link
                  :to="{ name: 'RenovarMembresia', params: { clienteId: m.clienteId } }"
                  class="bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Renovar Membresía
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
