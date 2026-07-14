<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { httpClient } from '@/services/api'
import Navbar          from '@/components/admin/AdminNavbar.vue'
import Footer          from '@/components/shared/Footer.vue'
import ClienteInfoCard from '@/components/membresias/ClienteInfoCard.vue'
import MembresiaOption from '@/components/membresias/MembresiaOption.vue'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const cedulaBusqueda    = ref('')
const clienteEncontrado = ref(null)
const tipoSeleccionado  = ref(null)
const tiposMembresia    = ref([])
const message           = ref('')
const messageType       = ref('')
const loadingBuscar     = ref(false)
const loadingAsignar    = ref(false)

onMounted(async () => {
  try {
    const res = await httpClient.get('/tipos-membresia')
    tiposMembresia.value = res.data ?? []
  } catch { tiposMembresia.value = [] }
})

async function buscarCliente() {
  message.value = ''
  const cedula = cedulaBusqueda.value.trim()
  if (!cedula) { message.value = 'Por favor ingrese una cédula.'; messageType.value = 'error'; return }
  loadingBuscar.value = true
  try {
    const res = await httpClient.get(`/clientes/cedula/${cedula}`)
    clienteEncontrado.value = res.data
    tipoSeleccionado.value = null
  } catch {
    message.value = 'Cliente no encontrado. Verifique la cédula.'
    messageType.value = 'error'
  } finally { loadingBuscar.value = false }
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
    cedulaBusqueda.value = ''
    tipoSeleccionado.value = null
  } catch (err) {
    message.value = err?.error || 'No se pudo asignar la membresía.'
    messageType.value = 'error'
  } finally { loadingAsignar.value = false }
}

function resetBusqueda() {
  clienteEncontrado.value = null
  cedulaBusqueda.value = ''
  tipoSeleccionado.value = null
  message.value = ''
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

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
              id="cedula" v-model="cedulaBusqueda" type="text" placeholder="Ingrese la cédula" autofocus
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all mb-4"
              @keyup.enter="buscarCliente"
            />
            <div class="flex flex-col sm:flex-row gap-3">
              <button @click="buscarCliente" :disabled="loadingBuscar"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer">
                {{ loadingBuscar ? 'Buscando…' : 'Buscar Cliente' }}
              </button>
              <router-link to="/clientes"
                class="flex-1 text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all">
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

            <div v-if="!tiposMembresia.length" class="text-center py-6 text-slate-400 text-sm animate-pulse">
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
              <button @click="asignarMembresia" :disabled="!tipoSeleccionado || loadingAsignar"
                class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer">
                {{ loadingAsignar ? 'Asignando…' : 'Asignar Membresía' }}
              </button>
              <button @click="resetBusqueda"
                class="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm px-5 py-3 rounded-xl transition-all cursor-pointer">
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

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
