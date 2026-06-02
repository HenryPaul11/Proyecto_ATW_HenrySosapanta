<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClienteStore } from '@/stores/clienteStore'
import ClientNavbar from '@/components/client/ClientNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useClienteStore()

function logout() { auth.logout(); router.push('/login') }

const hoy = new Date()

const diasRestantes = computed(() => {
  if (!store.membresia) return 0
  return Math.max(0, Math.ceil((new Date(store.membresia.fecha_fin) - hoy) / 86_400_000))
})

const porcentajeUsado = computed(() => {
  if (!store.membresia) return 0
  const inicio = new Date(store.membresia.fecha_inicio).getTime()
  const fin    = new Date(store.membresia.fecha_fin).getTime()
  const total  = fin - inicio
  const usado  = hoy.getTime() - inicio
  return Math.min(100, Math.max(0, Math.round((usado / total) * 100)))
})

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(async () => {
  if (!store.cliente) await store.fetchByUsuario(auth.usuario)
  if (store.cliente) await store.fetchMembresia(store.cliente.id)
})
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-blue-50 to-slate-100">
    <ClientNavbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-3xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight mb-8">
        🎫 Mi Membresía
      </h1>

      <!-- Loading -->
      <div v-if="store.loading" class="h-48 bg-slate-200 animate-pulse rounded-2xl" />

      <!-- Sin membresía -->
      <template v-else-if="!store.membresia">
        <div class="bg-white rounded-2xl border-2 border-dashed border-slate-300 p-10 text-center">
          <div class="text-5xl mb-4">😕</div>
          <h2 class="text-lg font-bold text-slate-700 mb-2">No tienes membresía activa</h2>
          <p class="text-slate-500 text-sm mb-6">Visita la recepción del gimnasio para adquirir un plan y comenzar a entrenar.</p>
          <div class="bg-blue-50 rounded-xl p-4 text-sm text-blue-800 font-medium">
            📞 Contáctanos: <strong>(02) 123-4567</strong> · Lun–Vie 5:00–22:00
          </div>
        </div>
      </template>

      <!-- Con membresía -->
      <template v-else>
        <!-- Estado principal -->
        <div class="rounded-2xl p-6 mb-5 border-2"
          :class="store.membresia.estado === 'activa' ? 'bg-emerald-50 border-emerald-300' : 'bg-red-50 border-red-300'">
          <div class="flex items-center justify-between flex-wrap gap-3">
            <div>
              <p class="text-xs font-bold uppercase tracking-widest mb-1"
                :class="store.membresia.estado === 'activa' ? 'text-emerald-600' : 'text-red-600'">
                Estado de membresía
              </p>
              <h2 class="text-2xl font-black"
                :class="store.membresia.estado === 'activa' ? 'text-emerald-800' : 'text-red-800'">
                {{ store.membresia.tipo_membresia }}
              </h2>
              <p class="text-sm mt-1"
                :class="store.membresia.estado === 'activa' ? 'text-emerald-700' : 'text-red-700'">
                {{ store.membresia.descripcion }}
              </p>
            </div>
            <span class="px-4 py-2 rounded-full font-black text-sm uppercase tracking-wide"
              :class="store.membresia.estado === 'activa' ? 'bg-emerald-500 text-white' : 'bg-red-500 text-white'">
              {{ store.membresia.estado === 'activa' ? '✅ ACTIVA' : '❌ VENCIDA' }}
            </span>
          </div>
        </div>

        <!-- Detalles -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-5">
          <h3 class="text-base font-bold text-slate-700 mb-4">Detalles del Plan</h3>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="bg-slate-50 rounded-xl p-4">
              <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Precio</p>
              <p class="text-2xl font-black text-emerald-600">${{ store.membresia.precio.toFixed(2) }}</p>
              <p class="text-xs text-slate-400 mt-0.5">por período</p>
            </div>
            <div class="bg-slate-50 rounded-xl p-4">
              <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Días restantes</p>
              <p class="text-2xl font-black" :class="diasRestantes <= 7 ? 'text-amber-500' : 'text-blue-600'">
                {{ store.membresia.estado === 'activa' ? diasRestantes : '—' }}
              </p>
              <p class="text-xs text-slate-400 mt-0.5">{{ store.membresia.estado === 'activa' ? 'días' : 'membresía vencida' }}</p>
            </div>
            <div class="bg-slate-50 rounded-xl p-4">
              <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Fecha de inicio</p>
              <p class="text-base font-bold text-slate-800">{{ formatFecha(store.membresia.fecha_inicio) }}</p>
            </div>
            <div class="bg-slate-50 rounded-xl p-4">
              <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Fecha de vencimiento</p>
              <p class="text-base font-bold text-slate-800">{{ formatFecha(store.membresia.fecha_fin) }}</p>
            </div>
          </div>
        </div>

        <!-- Alertas -->
        <div v-if="store.membresia.estado === 'activa' && diasRestantes <= 7"
          class="bg-amber-50 border-l-4 border-amber-400 rounded-xl px-5 py-4 mb-5 text-sm text-amber-800 font-semibold">
          ⏳ Tu membresía vence en <strong>{{ diasRestantes }} día{{ diasRestantes !== 1 ? 's' : '' }}</strong>. Acércate a recepción para renovarla.
        </div>
        <div v-else-if="store.membresia.estado === 'vencida'"
          class="bg-red-50 border-l-4 border-red-400 rounded-xl px-5 py-4 mb-5 text-sm text-red-800 font-semibold">
          ❌ Tu membresía está vencida. Visita recepción para renovarla y seguir disfrutando del gimnasio.
        </div>

        <!-- Barra de progreso -->
        <div v-if="store.membresia.estado === 'activa'" class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <div class="flex justify-between text-xs font-semibold text-slate-500 mb-2">
            <span>{{ formatFecha(store.membresia.fecha_inicio) }}</span>
            <span>{{ porcentajeUsado }}% usado</span>
            <span>{{ formatFecha(store.membresia.fecha_fin) }}</span>
          </div>
          <div class="w-full bg-slate-100 rounded-full h-3 overflow-hidden">
            <div class="h-3 rounded-full transition-all duration-700"
              :class="porcentajeUsado >= 85 ? 'bg-red-400' : porcentajeUsado >= 60 ? 'bg-amber-400' : 'bg-emerald-500'"
              :style="{ width: porcentajeUsado + '%' }" />
          </div>
          <p class="text-xs text-slate-400 mt-2 text-center">Progreso de tu membresía actual</p>
        </div>
      </template>

    </main>

    <Footer />
  </div>
</template>
