<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClienteStore } from '@/stores/clienteStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useClienteStore()

function logout() { auth.logout(); router.push('/login') }

const navLinks = [
  { to: '/cliente/dashboard', label: 'Inicio',    icon: 'home'      },
  { to: '/cliente/membresia', label: 'Membresía', icon: 'membresia' },
  { to: '/cliente/pagos',     label: 'Mis Pagos', icon: 'pagos'     },
  { to: '/cliente/perfil',    label: 'Mi Perfil', icon: 'perfil'    },
]

const hoy = new Date()

const diasRestantes = computed(() => {
  if (!store.membresia) return 0
  return Math.max(0, Math.ceil((new Date(store.membresia.fechaFin) - hoy) / 86_400_000))
})

const infoPersonal = computed(() => {
  if (!store.cliente) return []
  const c = store.cliente
  return [
    { label: 'Nombre completo:', value: c.nombreCompleto },
    { label: 'Cédula:',          value: c.documentoIdentidad },
    { label: 'Teléfono:',        value: c.telefono },
    { label: 'Email:',           value: c.email },
    { label: 'Registrado desde:', value: formatFecha(c.fechaRegistro) },
  ]
})

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(() => store.fetchAll(auth.usuario))
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-blue-50 to-slate-100">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" variant="blue" @logout="logout" />

    <main class="flex-1 w-full max-w-4xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <!-- Bienvenida -->
      <div class="text-center mb-8 pb-6 border-b border-slate-200">
        <img src="/icons/pesa.svg" alt="🏋️" class="w-14 h-14 mx-auto mb-3 icon-slate" />
        <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight flex items-center justify-center gap-2">
          ¡Bienvenido, {{ store.cliente?.nombreCompleto?.split(' ')[0] ?? auth.usuario }}!
          <img src="/icons/musculo.svg" alt="💪" class="w-8 h-8 icon-slate" />
        </h1>
        <p class="text-slate-500 text-sm mt-1 font-medium">Gestiona tu membresía y revisa tu información personal</p>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="grid grid-cols-1 md:grid-cols-2 gap-5 mb-6">
        <div v-for="i in 2" :key="i" class="h-48 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <!-- Sin cliente asociado -->
      <div v-else-if="!store.cliente" class="bg-red-50 border-l-4 border-red-400 rounded-xl p-5 mb-6">
        <h3 class="text-red-800 font-bold text-base mb-1">⚠️ Información importante</h3>
        <p class="text-red-700 text-sm">No se encontró información de cliente para tu usuario. Contacta con la administración.</p>
      </div>

      <template v-else>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mb-6">

          <!-- Información personal -->
          <div class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-lg hover:shadow-blue-100 transition-all duration-300 p-6">
            <h2 class="text-lg font-bold text-slate-800 mb-4 pb-3 border-b-2 border-blue-500 flex items-center gap-2">
              <img src="/icons/usuario.svg" class="w-5 h-5 icon-slate" alt="" /> Información Personal
            </h2>
            <div class="space-y-3">
              <div v-for="row in infoPersonal" :key="row.label" class="flex flex-col sm:flex-row sm:items-center gap-0.5 sm:gap-3 text-sm">
                <span class="font-bold text-slate-500 sm:min-w-[130px]">{{ row.label }}</span>
                <span class="text-slate-800 font-medium">{{ row.value }}</span>
              </div>
            </div>
            <router-link
              to="/cliente/perfil"
              class="mt-5 inline-flex items-center gap-1.5 text-blue-600 hover:text-blue-800 text-sm font-semibold transition-colors"
            >
              Editar perfil →
            </router-link>
          </div>

          <!-- Membresía -->
          <div class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-lg hover:shadow-blue-100 transition-all duration-300 p-6">
            <h2 class="text-lg font-bold text-slate-800 mb-4 pb-3 border-b-2 border-blue-500">
              🎫 Mi Membresía
            </h2>

            <template v-if="!store.membresia">
              <p class="text-red-500 font-bold text-sm mb-2 flex items-center gap-1.5">
                <svg class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                No tienes una membresía activa
              </p>
              <p class="text-slate-500 text-sm mb-4">Visita recepción para adquirir una membresía y disfrutar de nuestros servicios.</p>
              <router-link
                to="/cliente/membresia"
                class="inline-flex items-center justify-center bg-emerald-500 hover:bg-emerald-600 text-white font-bold text-sm px-4 py-2.5 rounded-xl transition-all shadow-sm"
              >
                Ver opciones de membresía
              </router-link>
            </template>

            <template v-else>
              <div class="space-y-3 text-sm">
                <div class="flex flex-col sm:flex-row sm:items-center gap-0.5 sm:gap-3">
                  <span class="font-bold text-slate-500 sm:min-w-[130px]">Tipo:</span>
                  <span class="text-slate-800 font-semibold">{{ store.membresia.plan?.nombrePlan }}</span>
                </div>
                <div class="flex flex-col sm:flex-row sm:items-center gap-0.5 sm:gap-3">
                  <span class="font-bold text-slate-500 sm:min-w-[130px]">Inicio:</span>
                  <span class="text-slate-800 font-medium">{{ formatFecha(store.membresia.fechaInicio) }}</span>
                </div>
                <div class="flex flex-col sm:flex-row sm:items-center gap-0.5 sm:gap-3">
                  <span class="font-bold text-slate-500 sm:min-w-[130px]">Vencimiento:</span>
                  <span class="text-slate-800 font-medium">{{ formatFecha(store.membresia.fechaFin) }}</span>
                </div>
                <div class="flex flex-col sm:flex-row sm:items-center gap-0.5 sm:gap-3">
                  <span class="font-bold text-slate-500 sm:min-w-[130px]">Estado:</span>
                  <span
                    class="inline-block px-2.5 py-0.5 rounded-full text-xs font-bold"
                    :class="store.membresia.estado === 'ACTIVA' ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'"
                  >
                    {{ store.membresia.estado === 'ACTIVA' ? 'ACTIVA' : 'VENCIDA' }}
                  </span>
                </div>
              </div>

              <div v-if="store.membresia.estado === 'ACTIVA' && diasRestantes <= 7 && diasRestantes >= 0"
                class="mt-4 bg-amber-50 border-l-4 border-amber-400 rounded-lg px-4 py-3 text-sm text-amber-800 font-semibold flex items-center gap-2">
                <img src="/icons/reloj.svg" class="w-4 h-4 shrink-0 icon-amber" alt="" />
                Tu membresía vence en <strong>{{ diasRestantes }} día{{ diasRestantes !== 1 ? 's' : '' }}</strong>
              </div>
              <div v-else-if="store.membresia.estado === 'ACTIVA'"
                class="mt-3 text-sm text-slate-500">
                Días restantes: <strong class="text-emerald-600">{{ diasRestantes }}</strong>
              </div>
              <div v-else class="mt-4 bg-red-50 border-l-4 border-red-400 rounded-lg px-4 py-3 text-sm text-red-800 font-semibold flex items-center gap-2">
                <svg class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                Tu membresía está vencida. Contacta recepción para renovarla.
              </div>

              <router-link
                to="/cliente/membresia"
                class="mt-4 inline-flex items-center gap-1.5 text-blue-600 hover:text-blue-800 text-sm font-semibold transition-colors"
              >
                Ver detalles →
              </router-link>
            </template>
          </div>
        </div>

        <!-- Accesos rápidos -->
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <router-link to="/cliente/membresia"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
            <img src="/icons/boleto.svg" class="w-8 h-8 icon-slate" alt="" />
            <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Mi Membresía</p>
            <p class="text-xs text-slate-400">Detalles y estado de tu plan</p>
          </router-link>
          <router-link to="/cliente/pagos"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
            <img src="/icons/tarjeta.svg" class="w-8 h-8 icon-slate" alt="" />
            <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Mis Pagos</p>
            <p class="text-xs text-slate-400">Historial de transacciones</p>
          </router-link>
          <router-link to="/cliente/perfil"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
            <img src="/icons/usuario.svg" class="w-8 h-8 icon-slate" alt="" />
            <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Mi Perfil</p>
            <p class="text-xs text-slate-400">Actualiza tus datos personales</p>
          </router-link>
        </div>
      </template>

    </main>

    <Footer />
  </div>
</template>

<style scoped>
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
.icon-amber { filter: brightness(0) saturate(100%) invert(55%) sepia(60%) saturate(600%) hue-rotate(10deg) opacity(0.9); }
html.dark .icon-slate,
html.dark .icon-amber { filter: brightness(0) invert(1) opacity(0.8); }
</style>
