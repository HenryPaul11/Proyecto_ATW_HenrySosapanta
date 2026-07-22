<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { httpClient } from '@/services/api'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

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

const tipoSeleccionado = ref(null)
const loading          = ref(false)
const message          = ref('')
const messageType      = ref('')
const cliente          = ref(null)
const membresiaActual  = ref(null)
const tiposMembresia   = ref([])

const clienteId = computed(() => Number(route.params.clienteId))

const clienteRows = computed(() => {
  if (!cliente.value) return []
  const c = cliente.value
  return [
    { label: 'Nombre',   value: `<strong>${c.nombre} ${c.apellido}</strong>` },
    { label: 'Cédula',   value: c.cedula   },
    { label: 'Teléfono', value: c.telefono },
    { label: 'Email',    value: c.email    },
  ]
})

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(async () => {
  try {
    const [clienteRes, tiposRes, membresiaRes] = await Promise.all([
      httpClient.get(`/clientes/${clienteId.value}`),
      httpClient.get('/tipos-membresia'),
      httpClient.get(`/membresias/cliente/${clienteId.value}`).catch(() => ({ data: [] })),
    ])
    cliente.value       = clienteRes.data
    tiposMembresia.value = tiposRes.data ?? []

    const lista = membresiaRes.data ?? []
    if (lista.length) {
      membresiaActual.value = lista.sort((a, b) => new Date(b.fecha_fin) - new Date(a.fecha_fin))[0]
      const tipoActual = tiposMembresia.value.find(t => t.nombre === membresiaActual.value.tipo_membresia)
      if (tipoActual) tipoSeleccionado.value = tipoActual.id
    }
  } catch {
    router.push('/membresias')
  }
})

async function renovar() {
  if (!tipoSeleccionado.value) return
  loading.value = true
  try {
    await httpClient.put(`/membresias/${membresiaActual.value?.id ?? clienteId.value}/renovar`, {
      clienteId:       clienteId.value,
      tipoMembresiaId: tipoSeleccionado.value,
    })
    const tipo = tiposMembresia.value.find(t => t.id === tipoSeleccionado.value)
    message.value     = `¡Membresía renovada exitosamente! Tipo: ${tipo?.nombre ?? ''}`
    messageType.value = 'success'
  } catch (err) {
    message.value     = err?.error || 'No se pudo renovar la membresía.'
    messageType.value = 'error'
  } finally { loading.value = false }
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 w-full max-w-3xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-8 text-center flex items-center justify-center gap-2">
        <svg class="w-7 h-7 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
        Renovar Membresia
      </h1>

      <Transition name="fade">
        <div
          v-if="message"
          class="flex flex-col gap-3 px-5 py-4 rounded-xl mb-6 text-sm font-medium border"
          :class="messageType === 'success'
            ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
            : 'bg-red-50 text-red-800 border-red-200'"
        >
          <span v-html="message" />
          <div v-if="messageType === 'success'" class="flex flex-col sm:flex-row gap-2 mt-1">
            <router-link to="/membresias"
              class="inline-flex items-center justify-center bg-blue-500 hover:bg-blue-600 text-white font-semibold text-xs px-4 py-2 rounded-lg transition-all">
              ← Volver a Membresías
            </router-link>
            <router-link to="/pagos/registrar"
              class="inline-flex items-center justify-center bg-emerald-500 hover:bg-emerald-600 text-white font-semibold text-xs px-4 py-2 rounded-lg transition-all">
              <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
              Registrar Pago
            </router-link>
          </div>
        </div>
      </Transition>

      <!-- Info del cliente -->
      <div class="bg-white rounded-2xl shadow-sm border-t-4 border-blue-500 p-5 sm:p-6 mb-5">
        <h2 class="text-base font-bold text-slate-700 mb-4 flex items-center gap-2"><img src="/icons/usuario.svg" class="w-6 h-6 icon-slate inline-block mr-1" alt="" /> Información del Cliente</h2>
        <div class="divide-y divide-slate-100">
          <div v-for="row in clienteRows" :key="row.label" class="flex justify-between py-2.5 text-sm">
            <span class="font-semibold text-slate-500">{{ row.label }}</span>
            <span class="font-medium text-slate-800" v-html="row.value" />
          </div>
        </div>
      </div>

      <!-- Membresía actual -->
      <div class="bg-white rounded-2xl shadow-sm border-t-4 border-sky-400 p-5 sm:p-6 mb-5">
        <h3 class="text-base font-bold text-slate-700 mb-3 flex items-center gap-2">
          <svg class="w-5 h-5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><rect x="8" y="2" width="8" height="4" rx="1" ry="1"/></svg>
          Membresia Actual
        </h3>
        <template v-if="membresiaActual">
          <div class="text-sm text-slate-600 space-y-1.5">
            <p><span class="font-semibold">Tipo:</span> {{ membresiaActual.tipo_membresia }}</p>
            <p>
              <span class="font-semibold">Vigencia:</span>
              {{ formatFecha(membresiaActual.fecha_inicio) }} – {{ formatFecha(membresiaActual.fecha_fin) }}
            </p>
            <p class="flex items-center gap-2">
              <span class="font-semibold">Estado:</span>
              <span class="inline-block px-2.5 py-0.5 rounded-full text-xs font-bold"
                :class="membresiaActual.estado === 'activa' ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'">
                {{ membresiaActual.estado === 'activa' ? 'Activa' : 'Vencida' }}
              </span>
            </p>
            <p class="text-xs mt-2 font-medium"
              :class="membresiaActual.estado === 'activa' ? 'text-emerald-700' : 'text-red-700'">
              <template v-if="membresiaActual.estado === 'activa'">
                <svg class="w-3.5 h-3.5 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>La renovación comenzará el día siguiente al vencimiento actual.
              </template>
              <template v-else>
                <svg class="w-4 h-4 shrink-0 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>Esta membresía está vencida. La renovación comenzará desde hoy.
              </template>
            </p>
          </div>
        </template>
        <p v-else class="text-sm text-slate-500 flex items-center gap-1">
          <svg class="w-4 h-4 shrink-0 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>Este cliente no tiene membresía registrada. Se creará una nueva desde hoy.
        </p>
      </div>

      <!-- Selección de tipo -->
      <div class="bg-slate-50 rounded-2xl border border-slate-200 p-5 sm:p-6">
        <h2 class="text-base font-bold text-slate-700 mb-4 flex items-center gap-2"><img src="/icons/boleto.svg" class="w-6 h-6 icon-slate inline-block mr-1" alt="" /> Seleccione el Tipo de Membresía</h2>

        <div v-if="!tiposMembresia.length" class="text-center py-8 text-slate-400 text-sm animate-pulse">
          Cargando tipos de membresía…
        </div>

        <div v-else class="flex flex-col gap-3 mb-5">
          <div
            v-for="tipo in tiposMembresia" :key="tipo.id"
            @click="tipoSeleccionado = tipo.id"
            class="bg-white rounded-xl border-2 p-4 cursor-pointer transition-all duration-200 hover:-translate-y-0.5"
            :class="tipoSeleccionado === tipo.id
              ? 'border-blue-500 shadow-md shadow-blue-100 bg-blue-50'
              : 'border-slate-200 hover:border-blue-300 hover:shadow-sm'"
          >
            <div class="flex items-start justify-between gap-3">
              <div>
                <p class="font-bold text-slate-800 text-base">{{ tipo.nombre }}</p>
                <p class="text-xs text-slate-500 mt-1 leading-relaxed">
                  {{ tipo.descripcion }}<br />
                  Duracion: {{ tipo.duracion_dias }} dias
                </p>
              </div>
              <div class="shrink-0 text-right">
                <span class="text-emerald-600 font-bold text-lg">${{ tipo.precio.toFixed(2) }}</span>
                <div v-if="tipoSeleccionado === tipo.id"
                  class="mt-1 w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center ml-auto">
                  <svg class="w-3 h-3 text-white" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="flex flex-col sm:flex-row gap-3">
          <button @click="renovar" :disabled="!tipoSeleccionado || loading"
            class="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
            <template v-if="loading">Renovando…</template>
            <template v-else>
              <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>Renovar Membresía
            </template>
          </button>
          <router-link to="/membresias"
            class="flex-1 text-center bg-slate-200 hover:bg-slate-300 text-slate-700 font-semibold text-sm py-3 rounded-xl transition-all inline-flex items-center justify-center gap-1.5">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>Cancelar
          </router-link>
        </div>
      </div>

    </main>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
html.dark .icon-slate { filter: brightness(0) invert(1) opacity(0.8); }
</style>
