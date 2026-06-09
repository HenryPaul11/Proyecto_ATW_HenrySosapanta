<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClientesStore } from '@/stores/clientesStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const route  = useRoute()
const auth   = useAuthStore()
const store  = useClientesStore()

function logout() { auth.logout(); router.push('/login') }

const tipoSeleccionado = ref(null)
const loading          = ref(false)
const message          = ref('')
const messageType      = ref('')

const clienteId = computed(() => Number(route.params.clienteId))
const cliente   = computed(() => store.clientes.find(c => c.id === clienteId.value) ?? null)

const membresiaActual = computed(() => {
  const lista = store.membresiasPorCliente[clienteId.value] ?? []
  if (!lista.length) return null
  return lista.sort((a, b) => new Date(b.fecha_fin) - new Date(a.fecha_fin))[0]
})

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
  if (!store.clientes.length) await store.fetchClientes()
  if (!cliente.value) { router.push('/membresias'); return }
  await Promise.all([
    store.fetchTiposMembresia(),
    store.fetchMembresiasPorCliente(clienteId.value),
  ])
  if (membresiaActual.value) {
    const tipoActual = store.tiposMembresia.find(t => t.nombre === membresiaActual.value.tipo_membresia)
    if (tipoActual) tipoSeleccionado.value = tipoActual.id
  }
})

async function renovar() {
  if (!tipoSeleccionado.value) return
  loading.value = true
  await new Promise(r => setTimeout(r, 500))

  const tipo = store.tiposMembresia.find(t => t.id === tipoSeleccionado.value)
  const hoy  = new Date()

  let fechaInicio = hoy
  if (membresiaActual.value?.estado === 'activa') {
    fechaInicio = new Date(membresiaActual.value.fecha_fin)
    fechaInicio.setDate(fechaInicio.getDate() + 1)
  }

  const fechaFin = new Date(fechaInicio)
  fechaFin.setDate(fechaFin.getDate() + tipo.duracion_dias)

  loading.value     = false
  message.value     = `✅ ¡Membresía renovada exitosamente!<br>Nueva vigencia: ${formatFecha(fechaInicio)} al ${formatFecha(fechaFin)}`
  messageType.value = 'success'
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-3xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-8 text-center">
        🔄 Renovar Membresía
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
              💰 Registrar Pago
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
        <h3 class="text-base font-bold text-slate-700 mb-3">📋 Membresía Actual</h3>
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

        <div v-if="store.loading" class="text-center py-8 text-slate-400 text-sm animate-pulse">
          Cargando tipos de membresía…
        </div>

        <div v-else class="flex flex-col gap-3 mb-5">
          <div
            v-for="tipo in store.tiposMembresia" :key="tipo.id"
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
                  ⏱️ Duración: {{ tipo.duracion_dias }} días
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
