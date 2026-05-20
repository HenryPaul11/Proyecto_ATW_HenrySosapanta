<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl bg-white rounded-2xl shadow-sm border border-slate-200 p-6 sm:p-8 md:p-10">

        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 text-center mb-6 tracking-tight">
          Asignar Membresía
        </h1>

        <!-- Feedback -->
        <Transition name="fade">
          <div
            v-if="message"
            class="flex items-start gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border"
            :class="messageType === 'success'
              ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
              : 'bg-red-50 text-red-800 border-red-200'"
          >
            <span>{{ messageType === 'success' ? '✅' : '❌' }}</span>
            <span>{{ message }}</span>
          </div>
        </Transition>

        <!-- PASO 1 -->
        <template v-if="!clienteEncontrado">
          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4">Paso 1: Buscar Cliente por Cédula</h2>
            <label for="cedula" class="block text-sm font-semibold text-slate-700 mb-2">Cédula del Cliente</label>
            <input
              id="cedula"
              v-model="cedulaBusqueda"
              type="text"
              placeholder="Ingrese la cédula"
              autofocus
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all mb-4"
              @keyup.enter="buscarCliente"
            />
            <div class="flex flex-col sm:flex-row gap-3">
              <button
                @click="buscarCliente"
                :disabled="loading"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer"
              >
                {{ loading ? 'Buscando…' : 'Buscar Cliente' }}
              </button>
              <router-link
                to="/clientes"
                class="flex-1 text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all"
              >
                Ver todos los clientes
              </router-link>
            </div>
          </div>
        </template>

        <!-- PASO 2 -->
        <template v-else>
          <ClienteInfoCard :cliente="clienteEncontrado" />

          <div class="bg-slate-50 rounded-xl p-5">
            <h2 class="text-base font-bold text-slate-700 mb-4">Paso 2: Seleccionar Tipo de Membresía</h2>

            <div v-if="loadingTipos" class="text-center py-6 text-slate-400 text-sm animate-pulse">
              Cargando tipos de membresía…
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
              <button
                @click="asignarMembresia"
                :disabled="!tipoSeleccionado || loading"
                class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer"
              >
                {{ loading ? 'Asignando…' : 'Asignar Membresía' }}
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

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import Navbar          from '../components/shared/Navbar.vue'
import Footer          from '../components/shared/Footer.vue'
import ClienteInfoCard from '../components/membresias/ClienteInfoCard.vue'
import MembresiaOption from '../components/membresias/MembresiaOption.vue'

const router = useRouter()
const auth   = useAuthStore()
function logout() { auth.logout(); router.push('/login') }

const cedulaBusqueda    = ref('')
const clienteEncontrado = ref(null)
const tiposMembresia    = ref([])
const tipoSeleccionado  = ref(null)
const message           = ref('')
const messageType       = ref('')
const loading           = ref(false)
const loadingTipos      = ref(false)

const mockClientes = [
  { id:10, nombre:'Luis',  apellido:'Gómez', cedula:'1111111111', telefono:'0991111111', email:'luis@mail.com'  },
  { id:11, nombre:'Sara',  apellido:'Mora',  cedula:'2222222222', telefono:'0992222222', email:'sara@mail.com'  },
  { id:12, nombre:'Pedro', apellido:'Vega',  cedula:'3333333333', telefono:'0993333333', email:'pedro@mail.com' },
]
const mockTipos = [
  { id:1, nombre:'Básica',      descripcion:'Acceso a equipos de cardio y pesas.',    duracion_dias:30,  precio:25.00  },
  { id:2, nombre:'Mensual',     descripcion:'Acceso completo + clases grupales.',      duracion_dias:30,  precio:40.00  },
  { id:3, nombre:'Premium',     descripcion:'Acceso ilimitado + entrenador personal.', duracion_dias:30,  precio:70.00  },
  { id:4, nombre:'Trimestral',  descripcion:'Plan de 3 meses con descuento especial.', duracion_dias:90,  precio:100.00 },
  { id:5, nombre:'Anual',       descripcion:'Plan anual con el mejor precio por mes.', duracion_dias:365, precio:300.00 },
]

onMounted(async () => {
  loadingTipos.value = true
  await new Promise(r => setTimeout(r, 300))
  tiposMembresia.value = mockTipos
  loadingTipos.value = false
})

async function buscarCliente() {
  message.value = ''
  const cedula = cedulaBusqueda.value.trim()
  if (!cedula) { message.value = 'Por favor ingrese una cédula.'; messageType.value = 'error'; return }
  loading.value = true
  await new Promise(r => setTimeout(r, 400))
  const encontrado = mockClientes.find(c => c.cedula === cedula)
  loading.value = false
  if (encontrado) { clienteEncontrado.value = encontrado; tipoSeleccionado.value = null }
  else { message.value = 'Cliente no encontrado. Verifique la cédula.'; messageType.value = 'error' }
}

async function asignarMembresia() {
  if (!tipoSeleccionado.value || !clienteEncontrado.value) return
  loading.value = true
  await new Promise(r => setTimeout(r, 500))
  loading.value = false
  message.value = '¡Membresía asignada exitosamente! Ya puede registrar pagos para este cliente.'
  messageType.value = 'success'
  clienteEncontrado.value = null; cedulaBusqueda.value = ''; tipoSeleccionado.value = null
}

function resetBusqueda() {
  clienteEncontrado.value = null; cedulaBusqueda.value = ''; tipoSeleccionado.value = null; message.value = ''
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
