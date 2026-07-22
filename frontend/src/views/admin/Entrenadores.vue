<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'
import { httpClient } from '@/services/api'

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

const tableRef        = ref<InstanceType<typeof PaginatedTable> | null>(null)
const message         = ref('')
const messageType     = ref('')
const entrenadorEliminar = ref<any>(null)

const columnas = ['Nombre', 'Cédula', 'Teléfono', 'Email', 'Especialidad', 'Horario', 'Acciones']

async function eliminar() {
  if (!entrenadorEliminar.value) return
  try {
    await httpClient.delete(`/entrenadores/${entrenadorEliminar.value.id}`)
    message.value     = '✓ Entrenador eliminado.'
    messageType.value = 'success'
    tableRef.value?.reload()
  } catch (err: any) {
    message.value     = err?.error || 'No se pudo eliminar.'
    messageType.value = 'error'
  } finally {
    entrenadorEliminar.value = null
    setTimeout(() => message.value = '', 4000)
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

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

      <!-- Acciones -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
        <router-link to="/entrenadores/registrar"
          class="inline-flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all hover:-translate-y-0.5 w-full sm:w-auto">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          Registrar Entrenador
        </router-link>
      </div>

      <!-- Tabla paginada -->
      <PaginatedTable
        ref="tableRef"
        endpoint="/entrenadores"
        :columns="columnas"
        endpointKey="usuarios"
      >
        <template #default="{ items }">
          <tr v-for="(e, i) in items" :key="e.id"
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
        </template>
      </PaginatedTable>

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
