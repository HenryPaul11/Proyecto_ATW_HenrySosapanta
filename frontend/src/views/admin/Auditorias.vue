<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar       from '@/components/shared/AppNavbar.vue'
import Footer         from '@/components/shared/Footer.vue'
import AccionBadge    from '@/components/auditoria/AccionBadge.vue'
import DatosAuditoria from '@/components/auditoria/DatosAuditoria.vue'
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

const filtroUsuario = ref('')
const filtroTabla   = ref('')
const filtroAccion  = ref('')

const columnas = ['Tabla', 'Acción', 'Usuario', 'Fecha y Hora', 'Datos Anteriores', 'Datos Nuevos']

// Tablas comunes para filtrar
const tablasUnicas = ['cliente', 'membresia', 'pago', 'usuario', 'entrenador', 'equipo', 'sesion']

const filtrosExternos = computed(() => ({
  usuario: filtroUsuario.value.trim() || undefined,
  tabla: filtroTabla.value || undefined,
  accion: filtroAccion.value || undefined,
}))

function formatFecha(str: string) {
  if (!str) return '—'
  return new Date(str).toLocaleString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function limpiarFiltros() { filtroUsuario.value = ''; filtroTabla.value = ''; filtroAccion.value = '' }
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-extrabold text-slate-800 tracking-tight text-center mb-8">
        Historial de Auditorías
      </h1>

      <!-- Filtros -->
      <div class="bg-white rounded-2xl shadow-sm p-4 sm:p-5 mb-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Usuario</label>
            <input v-model="filtroUsuario" type="text" placeholder="Filtrar por usuario…"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all" />
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Tabla</label>
            <select v-model="filtroTabla"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white">
              <option value="">Todas</option>
              <option v-for="t in tablasUnicas" :key="t" :value="t">{{ t }}</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Acción</label>
            <select v-model="filtroAccion"
              class="w-full px-3 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white">
              <option value="">Todas</option>
              <option value="INSERT">INSERT</option>
              <option value="UPDATE">UPDATE</option>
              <option value="DELETE">DELETE</option>
            </select>
          </div>
          <div class="flex items-end">
            <button @click="limpiarFiltros"
              class="w-full px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-600 font-semibold text-sm rounded-xl transition-all cursor-pointer">
              Limpiar filtros
            </button>
          </div>
        </div>
      </div>

      <!-- Tabla Paginada -->
      <div class="mb-8">
        <PaginatedTable
          endpoint="/auditorias"
          :filtros="filtrosExternos"
          :columns="columnas"
          endpoint-key="auditorias"
        >
          <template #default="{ items }">
            <tr
              v-for="(row, i) in items" :key="i"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50 bg-white"
              :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
            >
              <td class="px-4 py-3 text-sm font-semibold text-slate-700 whitespace-nowrap">{{ row.tablaAfectada || row.tabla_afectada }}</td>
              <td class="px-4 py-3 whitespace-nowrap"><AccionBadge :accion="row.accion" /></td>
              <td class="px-4 py-3 text-sm text-slate-600 whitespace-nowrap">{{ row.usuario?.nombreCompleto || row.usuario || 'Sistema' }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(row.fechaHora || row.fecha_hora) }}</td>
              <td class="px-4 py-3 align-top"><DatosAuditoria :datos="row.datosAnteriores || row.datos_anteriores" /></td>
              <td class="px-4 py-3 align-top"><DatosAuditoria :datos="row.datosNuevos || row.datos_nuevos" /></td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

    </main>

    <Footer />
  </div>
</template>

