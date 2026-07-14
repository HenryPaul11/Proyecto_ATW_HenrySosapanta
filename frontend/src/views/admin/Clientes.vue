<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'
import { httpClient, type Cliente } from '@/services/api'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const message          = ref('')
const messageType      = ref('')
const clienteAEliminar = ref<Cliente | null>(null)
const tablaPaginada    = ref<{ reload: () => void } | null>(null)

const columnas = ['Nombre', 'Cédula', 'Teléfono', 'Email', 'Acciones']

function confirmarEliminar(c: Cliente) { clienteAEliminar.value = c }

async function eliminarCliente() {
  const c = clienteAEliminar.value
  if (!c) return
  try {
    await httpClient.delete(`/clientes/${c.id}`)
    message.value     = `Cliente ${c.nombre} ${c.apellido} eliminado correctamente.`
    messageType.value = 'success'
  } catch (err) {
    message.value     = err.error || 'No se pudo eliminar el cliente.'
    messageType.value = 'error'
  }
  clienteAEliminar.value = null
  if (tablaPaginada.value) {
    tablaPaginada.value.reload()
  }
  setTimeout(() => { message.value = '' }, 4000)
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-8">
        Listado de Clientes
      </h1>

      <!-- Feedback -->
      <Transition name="fade">
        <div
          v-if="message"
          class="flex items-center gap-3 px-4 py-3 rounded-xl mb-6 text-sm font-medium border"
          :class="messageType === 'success'
            ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
            : 'bg-red-50 text-red-800 border-red-200'"
        >
          <span>{{ messageType === 'success' ? '✅' : '❌' }}</span>
          <span>{{ message }}</span>
        </div>
      </Transition>

      <!-- Acciones -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
        <router-link
          to="/clientes/registrar"
          class="inline-flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all duration-200 hover:-translate-y-0.5 w-full sm:w-auto"
        >
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Registrar Cliente
        </router-link>
      </div>

      <!-- Tabla Paginada -->
      <div class="mb-8">
        <PaginatedTable
          ref="tablaPaginada"
          endpoint="/clientes/paginado"
          :columns="columnas"
          endpoint-key="clientes"
        >
          <template #default="{ items }">
            <tr
              v-for="(c, i) in items"
              :key="c.id"
              class="border-b border-slate-100 transition-colors duration-150 hover:bg-blue-50"
              :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'"
            >
              <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ c.nombre }} {{ c.apellido }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ c.cedula }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ c.telefono }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ c.email }}</td>
              <td class="px-4 py-3 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <router-link
                    :to="{ name: 'EditarCliente', params: { id: c.id } }"
                    class="bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm"
                  >
                    Editar
                  </router-link>
                  <button
                    @click="confirmarEliminar(c)"
                    class="bg-red-500 hover:bg-red-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm cursor-pointer"
                  >
                    Eliminar
                  </button>
                </div>
              </td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

    </main>

    <!-- Modal confirmación eliminar -->
    <Transition name="fade">
      <div v-if="clienteAEliminar" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
          <h3 class="text-lg font-bold text-slate-800 mb-2">¿Eliminar cliente?</h3>
          <p class="text-sm text-slate-500 mb-5">
            Se eliminará a <strong>{{ clienteAEliminar.nombre }} {{ clienteAEliminar.apellido }}</strong> y todas sus membresías. Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-3">
            <button
              @click="eliminarCliente"
              class="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer"
            >
              Sí, eliminar
            </button>
            <button
              @click="clienteAEliminar = null"
              class="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer"
            >
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

