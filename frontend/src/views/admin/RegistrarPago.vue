<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClientesStore } from '@/stores/clientesStore'
import { usePagosStore } from '@/stores/pagosStore'
import Navbar        from '@/components/admin/AdminNavbar.vue'
import Footer        from '@/components/shared/Footer.vue'
import StepIndicator from '@/components/pagos/StepIndicator.vue'
import MembresiaCard from '@/components/pagos/MembresiaCard.vue'

const router        = useRouter()
const auth          = useAuthStore()
const clientesStore = useClientesStore()
const pagosStore    = usePagosStore()

function logout() { auth.logout(); router.push('/login') }

const pasos      = ['Buscar Cliente', 'Registrar Pago', 'Confirmación']
const pasoActual = ref(0)

const cedulaBusqueda        = ref('')
const clienteEncontrado     = ref(null)
const membresiasDisponibles = ref([])
const membresiaSeleccionada = ref(null)
const monto                 = ref('')
const metodoPago            = ref('')
const message               = ref('')
const messageType           = ref('')
const pagoIdConfirmado      = ref(null)
const confirmacion          = ref({})

const camposCliente = computed(() => {
  if (!clienteEncontrado.value) return []
  const c = clienteEncontrado.value
  return [
    { label: 'Nombre',   value: `${c.nombre} ${c.apellido}` },
    { label: 'Cédula',   value: c.cedula   },
    { label: 'Teléfono', value: c.telefono },
    { label: 'Email',    value: c.email    },
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

const puedeRegistrar = computed(() =>
  membresiaSeleccionada.value && monto.value && metodoPago.value && membresiasDisponibles.value.length > 0,
)

async function buscarCliente() {
  message.value = ''
  const cedula = cedulaBusqueda.value.trim()
  if (!cedula) { message.value = 'Por favor ingrese una cédula.'; messageType.value = 'error'; return }

  const cliente = await clientesStore.buscarPorCedula(cedula)

  if (!cliente) { message.value = 'Cliente no encontrado. Verifique la cédula.'; messageType.value = 'error'; return }

  clienteEncontrado.value = cliente
  await clientesStore.fetchMembresiasPorCliente(cliente.id)
  membresiasDisponibles.value = clientesStore.membresiasPorCliente[cliente.id] ?? []
  membresiaSeleccionada.value = null
  monto.value = ''
  metodoPago.value = ''
  message.value = ''
  pasoActual.value = 1

  if (membresiasDisponibles.value.length === 0) {
    message.value = 'El cliente no tiene membresías activas.'
    messageType.value = 'warning'
  }
}

function seleccionarMembresia(id, precio) {
  membresiaSeleccionada.value = id
  monto.value = Number(precio).toFixed(2)
}

async function registrarPago() {
  if (!puedeRegistrar.value) return
  const c    = clienteEncontrado.value
  const memb = membresiasDisponibles.value.find(m => m.id === membresiaSeleccionada.value)
  const ml   = { efectivo: '💵 Efectivo', tarjeta: '💳 Tarjeta', transferencia: '🏦 Transferencia' }

  const nuevo = await pagosStore.registrarPago({
    cliente_nombre:    c.nombre,
    cliente_apellido:  c.apellido,
    cedula:            c.cedula,
    tipo_membresia:    memb?.tipo_membresia ?? '',
    monto:             Number(monto.value),
    metodo_pago:       metodoPago.value,
    fecha_pago:        new Date().toISOString(),
    fecha_inicio:      memb?.fecha_inicio ?? '',
    fecha_fin:         memb?.fecha_fin ?? '',
    estado_membresia:  memb?.estado ?? 'activa',
  })

  pagoIdConfirmado.value = nuevo.id
  confirmacion.value = {
    cliente:   `${c.nombre} ${c.apellido}`,
    membresia: memb?.tipo_membresia ?? '—',
    monto:     `$${Number(monto.value).toFixed(2)}`,
    metodo:    ml[metodoPago.value] ?? metodoPago.value,
    fecha:     new Date().toLocaleString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }),
  }
  pasoActual.value = 2
}

function resetBusqueda() {
  clienteEncontrado.value = null
  membresiasDisponibles.value = []
  membresiaSeleccionada.value = null
  cedulaBusqueda.value = ''
  monto.value = ''
  metodoPago.value = ''
  message.value = ''
  pasoActual.value = 0
}

function nuevoPago() { resetBusqueda(); pagoIdConfirmado.value = null; confirmacion.value = {} }
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

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
            <span>{{ { success:'✅', error:'❌', warning:'⚠️' }[messageType] }}</span>
            <span>{{ message }}</span>
          </div>
        </Transition>

        <!-- PASO 1 -->
        <template v-if="pasoActual === 0">
          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4 flex items-center gap-2">
              <svg class="w-4 h-4 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              Buscar Cliente por Cédula
            </h2>
            <label for="cedula" class="block text-sm font-semibold text-slate-700 mb-2">Cédula del Cliente</label>
            <input
              id="cedula"
              v-model="cedulaBusqueda"
              type="text"
              placeholder="Ingrese la cédula del cliente"
              autofocus
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all mb-4"
              @keyup.enter="buscarCliente"
            />
            <div class="flex flex-col sm:flex-row gap-3">
              <button
                @click="buscarCliente"
                :disabled="clientesStore.loading"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer"
              >
                {{ clientesStore.loading ? 'Buscando…' : 'Buscar Cliente' }}
              </button>
              <router-link
                to="/clientes"
                class="flex-1 text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all"
              >
                👥 Ver todos los clientes
              </router-link>
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
                ⚠️ El cliente no tiene membresías activas. Asigne una membresía primero.
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
                <option value="efectivo">💵 Efectivo</option>
                <option value="tarjeta">💳 Tarjeta de Crédito/Débito</option>
                <option value="transferencia">🏦 Transferencia Bancaria</option>
              </select>
            </div>

            <div class="flex flex-col sm:flex-row gap-3">
              <button
                @click="registrarPago"
                :disabled="!puedeRegistrar || pagosStore.loading"
                class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer"
              >
                {{ pagosStore.loading ? 'Registrando…' : 'Registrar Pago' }}
              </button>
              <button
                @click="resetBusqueda"
                class="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer"
              >
                Buscar Otro Cliente
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
                ➕ Registrar Otro Pago
              </button>
              <router-link
                to="/pagos"
                class="flex-1 sm:flex-none text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-6 py-3 rounded-xl transition-all"
              >
                📋 Ver Todos los Pagos
              </router-link>
            </div>
          </div>
        </template>

        <div v-if="pasoActual < 2" class="mt-5 text-center">
          <router-link to="/pagos" class="text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            📋 Ver Todos los Pagos →
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
