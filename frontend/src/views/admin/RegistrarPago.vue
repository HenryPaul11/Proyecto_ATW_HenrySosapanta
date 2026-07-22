<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { httpClient } from '@/services/api'
import AppNavbar      from '@/components/shared/AppNavbar.vue'
import Footer        from '@/components/shared/Footer.vue'
import StepIndicator from '@/components/pagos/StepIndicator.vue'
import MembresiaCard from '@/components/pagos/MembresiaCard.vue'

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

const pasos      = ['Buscar Cliente', 'Registrar Pago', 'Confirmación']
const pasoActual = ref(0)

const clienteEncontrado     = ref(null)
const membresiasDisponibles = ref([])
const membresiaSeleccionada = ref(null)
const monto                 = ref('')
const metodoPago            = ref('')
const message               = ref('')
const messageType           = ref('')
const pagoIdConfirmado      = ref(null)
const confirmacion          = ref({})
const loadingPago           = ref(false)

const busquedaCliente    = ref('')
const resultadosClientes = ref([])
const buscandoCliente    = ref(false)
const mostrarDropdown    = ref(false)
let debounceTimer = null

const camposCliente = computed(() => {
  if (!clienteEncontrado.value) return []
  const c = clienteEncontrado.value
  const nombre = c.nombreCompleto || [c.nombre, c.apellido].filter(Boolean).join(' ') || ''
  const cedula = c.documentoIdentidad || c.cedula || ''
  return [
    { label: 'Nombre',   value: nombre },
    { label: 'Cédula',   value: cedula },
    { label: 'Teléfono', value: c.telefono || '' },
    { label: 'Email',    value: c.email    || '' },
  ]
})

const resumenConfirmacion = computed(() => {
  const c = confirmacion.value
  return [
    { label: 'Cliente',   value: c.cliente   ?? '—' },
    { label: 'Membresía', value: c.membresia ?? '—' },
    { label: 'Monto',     value: c.monto     ?? '—' },
    { label: 'Método',    value: c.metodo    ?? '—' },
    { label: 'Fecha',     value: c.fecha     ?? '—' },
  ]
})

const puedeRegistrar = computed(() => {
  const montoNum = parseFloat(monto.value)
  return membresiaSeleccionada.value && monto.value && montoNum > 0 && metodoPago.value && membresiasDisponibles.value.length > 0
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

async function seleccionarCliente(c) {
  clienteEncontrado.value = c
  busquedaCliente.value = clienteNombre(c)
  mostrarDropdown.value = false
  message.value = ''

  const [tiposRes, membRes] = await Promise.all([
    httpClient.get('/tipos-membresia'),
    httpClient.get(`/membresias/cliente/${c.id}`).catch(() => ({ data: [] })),
  ])
  const tipos = tiposRes.data ?? []
  const todasMembresias = membRes.data ?? []

  membresiasDisponibles.value = todasMembresias
    .filter(m => (m.estado || '').toUpperCase() === 'ACTIVA')
    .map(m => {
      const tipo = tipos.find(t => t.id === (m.plan?.id ?? m.tipoMembresiaId))
      return {
        ...m,
        precio: tipo?.precio ?? m.plan?.precio ?? 0,
        tipoMembresiaNombre: m.plan?.nombrePlan ?? tipo?.nombre ?? m.tipoMembresiaNombre ?? '—',
      }
    })

  membresiaSeleccionada.value = null
  monto.value = ''
  metodoPago.value = ''

  if (membresiasDisponibles.value.length === 0) {
    message.value = 'El cliente no tiene membresías activas.'
    messageType.value = 'warning'
  }
}

function continuarAPago() {
  pasoActual.value = 1
}

function cerrarDropdown(e) {
  if (!e.target.closest('.busqueda-cliente')) {
    mostrarDropdown.value = false
  }
}

function seleccionarMembresia(id, precio) {
  membresiaSeleccionada.value = id
  const memb = membresiasDisponibles.value.find(m => m.id === id)
  monto.value = Number(memb?.precio ?? precio ?? 0).toFixed(2)
}

async function registrarPago() {
  if (!puedeRegistrar.value) return
  const montoNum = parseFloat(monto.value)
  if (isNaN(montoNum) || montoNum <= 0) {
    message.value = 'El monto debe ser mayor a $0.00.'
    messageType.value = 'error'
    return
  }
  const c    = clienteEncontrado.value
  const memb = membresiasDisponibles.value.find(m => m.id === membresiaSeleccionada.value)
  loadingPago.value = true
  try {
    const res = await httpClient.post('/pagos', {
      clienteId:     c.id,
      membresiaId:   membresiaSeleccionada.value,
      monto:         parseFloat(monto.value),
      metodoPago:    metodoPago.value,
    })
    const nuevo = res.data
    pagoIdConfirmado.value = nuevo?.id ?? '—'
    confirmacion.value = {
      cliente:   c.nombreCompleto || `${c.nombre ?? ''} ${c.apellido ?? ''}`.trim() || '—',
      membresia: memb?.plan?.nombrePlan ?? memb?.tipoMembresiaNombre ?? memb?.tipo_membresia ?? '—',
      monto:     `$${Number(monto.value).toFixed(2)}`,
      metodo:    metodoPago.value,
      fecha:     new Date().toLocaleString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }),
    }
    pasoActual.value = 2
  } catch (err) {
    message.value = err?.error || 'No se pudo registrar el pago.'
    messageType.value = 'error'
  } finally { loadingPago.value = false }
}

function resetBusqueda() {
  clienteEncontrado.value = null
  membresiasDisponibles.value = []
  membresiaSeleccionada.value = null
  busquedaCliente.value = ''
  monto.value = ''
  metodoPago.value = ''
  message.value = ''
  pasoActual.value = 0
}

function nuevoPago() { resetBusqueda(); pagoIdConfirmado.value = null; confirmacion.value = {} }
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50" @click="cerrarDropdown">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl bg-white rounded-2xl shadow-sm border border-slate-200 p-6 sm:p-8 md:p-10">

        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 text-center mb-6 tracking-tight">
          Registrar Nuevo Pago
        </h1>

        <StepIndicator :steps="pasos" :current="pasoActual" />

        <!-- Feedback -->
        <Transition name="fade">
          <div
            v-if="message"
            class="flex items-start gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border"
            :class="{
              'bg-emerald-50 text-emerald-800 border-emerald-200': messageType === 'success',
              'bg-red-50    text-red-800    border-red-200':       messageType === 'error',
              'bg-amber-50  text-amber-800  border-amber-200':     messageType === 'warning',
            }"
          >
            <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else-if="messageType === 'error'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            <span>{{ message }}</span>
          </div>
        </Transition>

        <!-- PASO 1 -->
        <template v-if="pasoActual === 0">
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

            <div v-if="clienteEncontrado" class="mt-4 flex flex-col sm:flex-row gap-3">
              <button
                @click="continuarAPago"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer"
              >
                Continuar
              </button>
              <button
                @click="resetBusqueda"
                class="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer"
              >
                Cancelar
              </button>
            </div>
          </div>
        </template>

        <!-- PASO 2 -->
        <template v-if="pasoActual === 1">
          <div class="bg-white border-l-4 border-blue-500 rounded-xl p-4 mb-5">
            <h3 class="text-blue-600 font-bold text-sm mb-3">Cliente Encontrado</h3>
            <div class="divide-y divide-slate-100">
              <div v-for="f in camposCliente" :key="f.label" class="flex justify-between py-2">
                <span class="text-slate-500 font-semibold text-xs sm:text-sm">{{ f.label }}</span>
                <span class="text-slate-800 font-medium text-xs sm:text-sm text-right ml-2">{{ f.value }}</span>
              </div>
            </div>
          </div>

          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4">Registrar Pago</h2>

            <div class="mb-4">
              <label class="block text-sm font-semibold text-slate-700 mb-3">Seleccione la Membresía</label>
              <div v-if="membresiasDisponibles.length === 0" class="text-amber-700 bg-amber-50 border border-amber-200 rounded-xl px-4 py-3 text-sm font-medium">
                <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                El cliente no tiene membresías activas. Asigne una membresía primero.
              </div>
              <div v-else class="flex flex-col gap-3">
                <MembresiaCard
                  v-for="m in membresiasDisponibles"
                  :key="m.id"
                  :membresia="m"
                  :selected="membresiaSeleccionada === m.id"
                  @select="seleccionarMembresia"
                />
              </div>
            </div>

            <div class="mb-4">
              <label for="monto" class="block text-sm font-semibold text-slate-700 mb-2">Monto a Pagar</label>
              <div class="relative">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold text-sm">$</span>
                <input
                  id="monto"
                  v-model="monto"
                  type="number"
                  readonly
                  placeholder="0.00"
                  class="w-full pl-8 pr-4 py-3 border-2 border-slate-200 rounded-xl text-sm bg-slate-100 text-slate-700 font-bold focus:outline-none cursor-not-allowed"
                />
              </div>
              <p class="text-xs text-slate-400 mt-1">Se completa automáticamente al seleccionar la membresía.</p>
            </div>

            <div class="mb-5">
              <label for="metodo" class="block text-sm font-semibold text-slate-700 mb-2">Método de Pago</label>
              <select
                id="metodo"
                v-model="metodoPago"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white"
              >
                <option value="">Seleccione un método</option>
                <option value="efectivo">Efectivo</option>
                <option value="tarjeta">Tarjeta de Crédito/Débito</option>
                <option value="transferencia">Transferencia Bancaria</option>
              </select>
            </div>

            <div class="flex flex-col sm:flex-row gap-3">
              <button
                @click="registrarPago"
                :disabled="!puedeRegistrar || loadingPago"
                class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer"
              >
                {{ loadingPago ? 'Registrando…' : 'Registrar Pago' }}
              </button>
              <button
                @click="resetBusqueda"
                class="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer"
              >
                Seleccionar Otro Cliente
              </button>
            </div>
          </div>
        </template>

        <!-- PASO 3 -->
        <template v-if="pasoActual === 2">
          <div class="text-center py-4">
            <div class="mb-4 inline-flex items-center justify-center w-16 h-16 rounded-full bg-emerald-100">
              <svg class="w-8 h-8 text-emerald-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </div>
            <h2 class="text-xl sm:text-2xl font-bold text-slate-800 mb-2">¡Pago Registrado!</h2>
            <p class="text-slate-500 text-sm mb-1">
              ID de pago: <strong class="text-blue-600">#{{ pagoIdConfirmado }}</strong>
            </p>
            <p class="text-slate-500 text-sm mb-6">
              Registrado para <strong class="text-slate-700">{{ confirmacion.cliente }}</strong>.
            </p>
            <div class="bg-slate-50 rounded-xl p-4 text-left mb-6 divide-y divide-slate-200">
              <div v-for="f in resumenConfirmacion" :key="f.label" class="flex justify-between py-2.5">
                <span class="text-slate-500 text-xs sm:text-sm font-semibold">{{ f.label }}</span>
                <span class="text-slate-800 text-xs sm:text-sm font-medium text-right ml-2">{{ f.value }}</span>
              </div>
            </div>
            <div class="flex flex-col sm:flex-row justify-center gap-3">
              <button
                @click="nuevoPago"
                class="flex-1 sm:flex-none bg-blue-500 hover:bg-blue-600 text-white font-semibold text-sm px-6 py-3 rounded-xl transition-all cursor-pointer"
              >
                <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Registrar Otro Pago
              </button>
              <router-link
                to="/pagos"
                class="flex-1 sm:flex-none text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-6 py-3 rounded-xl transition-all"
              >
                <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                Ver Todos los Pagos
              </router-link>
            </div>
          </div>
        </template>

        <div v-if="pasoActual < 2" class="mt-5 text-center">
          <router-link to="/pagos" class="text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
            Ver Todos los Pagos →
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
