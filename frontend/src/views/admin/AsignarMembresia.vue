<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { httpClient } from '@/services/api'
import AppNavbar        from '@/components/shared/AppNavbar.vue'
import Footer          from '@/components/shared/Footer.vue'
import ClienteInfoCard from '@/components/membresias/ClienteInfoCard.vue'
import MembresiaOption from '@/components/membresias/MembresiaOption.vue'

const router = useRouter()
const route  = useRoute()
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

const clienteEncontrado = ref(null)
const tipoSeleccionado  = ref(null)
const tiposMembresia    = ref([])
const message           = ref('')
const messageType       = ref('')
const loadingAsignar    = ref(false)
const loadingTipos      = ref(false)

const busquedaCliente   = ref('')
const resultadosClientes = ref([])
const buscandoCliente   = ref(false)
const mostrarDropdown   = ref(false)
let debounceTimer = null

async function fetchTiposMembresia() {
  if (tiposMembresia.value.length > 0) return
  loadingTipos.value = true
  try {
    const res = await httpClient.get('/tipos-membresia')
    tiposMembresia.value = res.data ?? []
  } catch {
    tiposMembresia.value = []
  } finally {
    loadingTipos.value = false
  }
}

onMounted(async () => {
  await fetchTiposMembresia()
  const clienteId = route.query.clienteId
  if (clienteId) {
    try {
      const res = await httpClient.get(`/clientes/${clienteId}`)
      seleccionarCliente(res.data)
    } catch {
      // si falla, queda en modo búsqueda
    }
  }
})

function clienteNombre(c) {
  return c.nombreCompleto || `${c.nombre ?? ''} ${c.apellido ?? ''}`.trim() || 'Sin nombre'
}

watch(busquedaCliente, (val) => {
  if (debounceTimer) clearTimeout(debounceTimer)
  if (!val || val.trim().length < 1) {
    resultadosClientes.value = []
    mostrarDropdown.value = false
    return
  }
  debounceTimer = setTimeout(async () => {
    buscandoCliente.value = true
    try {
      const params = { page: 0, size: 8, q: val.trim() }
      if (auth.esSucursal) params.sucursalId = auth.sucursalId
      const res = await httpClient.get('/clientes/paginado', { params })
      const page = res.data
      resultadosClientes.value = page?.content ?? page ?? []
      mostrarDropdown.value = resultadosClientes.value.length > 0
    } catch {
      resultadosClientes.value = []
    } finally {
      buscandoCliente.value = false
    }
  }, 300)
})

function seleccionarCliente(c) {
  clienteEncontrado.value = c
  busquedaCliente.value = clienteNombre(c)
  mostrarDropdown.value = false
  tipoSeleccionado.value = null
}

function cerrarDropdown(e) {
  if (!e.target.closest('.busqueda-cliente')) {
    mostrarDropdown.value = false
  }
}

async function asignarMembresia() {
  if (!tipoSeleccionado.value || !clienteEncontrado.value) return
  loadingAsignar.value = true
  try {
    await httpClient.post('/membresias/asignar', {
      clienteId:        clienteEncontrado.value.id,
      tipoMembresiaId:  tipoSeleccionado.value,
    })
    message.value = '¡Membresía asignada exitosamente!'
    messageType.value = 'success'
    clienteEncontrado.value = null
    busquedaCliente.value = ''
    tipoSeleccionado.value = null
  } catch (err) {
    message.value = err?.error || 'No se pudo asignar la membresía.'
    messageType.value = 'error'
  } finally { loadingAsignar.value = false }
}

function resetBusqueda() {
  clienteEncontrado.value = null
  busquedaCliente.value = ''
  tipoSeleccionado.value = null
  message.value = ''
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50" @click="cerrarDropdown">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl bg-white rounded-2xl shadow-sm border border-slate-200 p-6 sm:p-8 md:p-10">

        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 text-center mb-6 tracking-tight">
          Asignar Membresía
        </h1>

        <Transition name="fade">
          <div
            v-if="message"
            class="flex items-start gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border"
            :class="messageType === 'success'
              ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
              : 'bg-red-50 text-red-800 border-red-200'"
          >
            <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            <span>{{ message }}</span>
          </div>
        </Transition>

        <!-- PASO 1 -->
        <template v-if="!clienteEncontrado">
          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4 flex items-center gap-2">
              <svg class="w-4 h-4 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              Seleccionar Cliente
            </h2>
            <div class="relative busqueda-cliente">
              <label for="cliente" class="block text-sm font-semibold text-slate-700 mb-2">Buscar por nombre o cédula</label>
              <div class="relative">
                <input
                  id="cliente"
                  v-model="busquedaCliente"
                  type="text"
                  placeholder="Escriba el nombre o cédula del cliente..."
                  autofocus
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
                  @focus="mostrarDropdown = resultadosClientes.length > 0"
                />
                <svg v-if="buscandoCliente" class="absolute right-3 top-3 w-4 h-4 text-blue-500 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10" stroke-opacity="0.25"/><path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"/></svg>
              </div>
              <Transition name="fade">
                <div v-if="mostrarDropdown && resultadosClientes.length > 0"
                  class="absolute z-20 w-full mt-1 bg-white border border-slate-200 rounded-xl shadow-lg max-h-60 overflow-y-auto">
                  <button
                    v-for="c in resultadosClientes"
                    :key="c.id"
                    @click="seleccionarCliente(c)"
                    class="w-full text-left px-4 py-3 hover:bg-blue-50 transition-colors border-b border-slate-100 last:border-b-0 cursor-pointer"
                  >
                    <p class="text-sm font-semibold text-slate-800">{{ clienteNombre(c) }}</p>
                    <p class="text-xs text-slate-400">{{ c.documentoIdentidad || c.cedula || '—' }} · {{ c.email || '—' }}</p>
                  </button>
                </div>
              </Transition>
            </div>
          </div>
        </template>

        <!-- PASO 2 -->
        <template v-else>
          <ClienteInfoCard :cliente="clienteEncontrado" />

          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4">Seleccionar Tipo de Membresía</h2>

            <div v-if="loadingTipos" class="text-center py-6 text-slate-400 text-sm animate-pulse">
              Cargando tipos de membresía…
            </div>
            <div v-else-if="!tiposMembresia.length" class="text-center py-6 text-slate-400 text-sm">
              No hay planes de membresía disponibles.
            </div>
            <div v-else class="flex flex-col gap-3 mb-5">
              <MembresiaOption
                v-for="tipo in tiposMembresia"
                :key="tipo.id"
                :tipo="tipo"
                :selected="tipoSeleccionado === tipo.id"
                @select="tipoSeleccionado = $event"
              />
            </div>

            <div class="flex flex-col sm:flex-row gap-3">
              <button @click="asignarMembresia" :disabled="!tipoSeleccionado || loadingAsignar"
                class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer">
                {{ loadingAsignar ? 'Asignando…' : 'Asignar Membresía' }}
              </button>
              <button @click="resetBusqueda"
                class="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer">
                Seleccionar Otro Cliente
              </button>
            </div>
          </div>
        </template>

        <div class="mt-5 text-center">
          <router-link to="/pagos" class="text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            Ir a Registrar Pago →
          </router-link>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
