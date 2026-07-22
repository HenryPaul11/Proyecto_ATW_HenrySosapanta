<script setup>
import { computed, ref, onMounted } from 'vue'
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
const loadingCliente    = ref(false)

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
    loadingCliente.value = true
    try {
      const res = await httpClient.get(`/clientes/${clienteId}`)
      clienteEncontrado.value = res.data
    } catch {
      message.value = 'No se pudo cargar el cliente.'
      messageType.value = 'error'
    } finally {
      loadingCliente.value = false
    }
  }
})

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
    setTimeout(() => router.push('/membresias'), 1200)
  } catch (err) {
    message.value = err?.error || 'No se pudo asignar la membresía.'
    messageType.value = 'error'
  } finally { loadingAsignar.value = false }
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
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

        <div v-if="loadingCliente" class="text-center py-10 text-slate-400 text-sm animate-pulse">
          Cargando cliente…
        </div>

        <template v-else-if="clienteEncontrado">
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

            <button @click="asignarMembresia" :disabled="!tipoSeleccionado || loadingAsignar"
              class="w-full bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-sm px-5 py-3 rounded-xl transition-all disabled:opacity-60 cursor-pointer">
              {{ loadingAsignar ? 'Asignando…' : 'Asignar Membresía' }}
            </button>
          </div>
        </template>

        <div v-else class="text-center py-10 text-slate-400 text-sm">
          No se encontró el cliente. Vuelve a la tabla de membresías y selecciona uno.
        </div>

        <div class="mt-5 flex flex-col sm:flex-row gap-3 text-center">
          <router-link to="/membresias" class="flex-1 text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            ← Volver a Membresías
          </router-link>
          <router-link to="/pagos" class="flex-1 text-emerald-500 hover:text-emerald-700 text-sm font-semibold underline underline-offset-2">
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
