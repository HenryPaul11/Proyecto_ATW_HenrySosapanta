<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import { httpClient } from '@/services/api'

const router = useRouter()
const auth   = useAuthStore()
function logout() { auth.logout(); router.push('/login') }

const sucursales      = ref<any[]>([])
const loading         = ref(false)
const message         = ref('')
const messageType     = ref('')
const mostrarForm     = ref(false)
const sucursalEditar  = ref<any>(null)
const sucursalEliminar = ref<any>(null)
const loadingGuardar  = ref(false)

const formVacio = () => ({ nombre: '', direccion: '', telefono: '', ciudad: '', fechaApertura: '' })
const form      = ref(formVacio())
const errores   = ref<Record<string, string>>({})

async function fetchSucursales() {
  loading.value = true
  try {
    const res = await httpClient.get('/sucursales')
    sucursales.value = res.data ?? []
  } catch { sucursales.value = [] }
  finally { loading.value = false }
}

function validar(): boolean {
  errores.value = {}
  if (!form.value.nombre.trim()) errores.value.nombre = 'El nombre es obligatorio.'
  if (!form.value.direccion.trim()) errores.value.direccion = 'La dirección es obligatoria.'
  return Object.keys(errores.value).length === 0
}

async function guardar() {
  if (!validar()) return
  loadingGuardar.value = true
  try {
    const body = {
      nombre:        form.value.nombre.trim(),
      direccion:     form.value.direccion.trim(),
      telefono:      form.value.telefono.trim() || null,
      ciudad:        form.value.ciudad.trim() || null,
      fechaApertura: form.value.fechaApertura || null,
      activo:        true,
    }
    if (sucursalEditar.value) {
      const res = await httpClient.put(`/sucursales/${sucursalEditar.value.id}`, body)
      const idx = sucursales.value.findIndex(s => s.id === sucursalEditar.value.id)
      if (idx !== -1) sucursales.value[idx] = res.data
      message.value = '✓ Sucursal actualizada.'
    } else {
      const res = await httpClient.post('/sucursales', body)
      sucursales.value.push(res.data)
      message.value = '✓ Sucursal registrada.'
    }
    messageType.value = 'success'
    form.value = formVacio()
    errores.value = {}
    mostrarForm.value = false
    sucursalEditar.value = null
    setTimeout(() => message.value = '', 4000)
  } catch (err: any) {
    message.value     = err?.error || 'No se pudo guardar la sucursal.'
    messageType.value = 'error'
  } finally { loadingGuardar.value = false }
}

function abrirEditar(s: any) {
  sucursalEditar.value = s
  form.value = {
    nombre:        s.nombre ?? '',
    direccion:     s.direccion ?? '',
    telefono:      s.telefono ?? '',
    ciudad:        s.ciudad ?? '',
    fechaApertura: s.fechaApertura ?? '',
  }
  mostrarForm.value = true
}

function abrirNuevo() {
  sucursalEditar.value = null
  form.value = formVacio()
  errores.value = {}
  mostrarForm.value = true
}

async function confirmarEliminar() {
  if (!sucursalEliminar.value) return
  try {
    await httpClient.delete(`/sucursales/${sucursalEliminar.value.id}`)
    sucursales.value = sucursales.value.filter(s => s.id !== sucursalEliminar.value.id)
    message.value     = '✓ Sucursal eliminada.'
    messageType.value = 'success'
    setTimeout(() => message.value = '', 4000)
  } catch (err: any) {
    message.value     = err?.error || 'No se pudo eliminar.'
    messageType.value = 'error'
  } finally { sucursalEliminar.value = null }
}

onMounted(fetchSucursales)
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-8 fade-in">

      <!-- Cabecera -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 tracking-tight">Sucursales</h1>
          <p class="text-slate-400 text-sm mt-1">Gestión de sedes del gimnasio PowerFit</p>
        </div>
        <button @click="abrirNuevo"
          class="inline-flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all hover:-translate-y-0.5 cursor-pointer w-full sm:w-auto">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          Nueva Sucursal
        </button>
      </div>

      <!-- Feedback -->
      <Transition name="fade">
        <div v-if="message"
          class="flex items-center gap-3 px-4 py-3 rounded-xl mb-6 text-sm font-medium border"
          :class="messageType === 'success' ? 'bg-emerald-50 text-emerald-800 border-emerald-200' : 'bg-red-50 text-red-800 border-red-200'">
          <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
          <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          {{ message }}
        </div>
      </Transition>

      <!-- Formulario nuevo/editar -->
      <Transition name="slide-down">
        <div v-if="mostrarForm" class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-8">
          <h2 class="text-base font-bold text-slate-700 mb-5">
            {{ sucursalEditar ? 'Editar Sucursal' : 'Registrar Nueva Sucursal' }}
          </h2>
          <form @submit.prevent="guardar" class="grid grid-cols-1 sm:grid-cols-2 gap-4">

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Nombre <span class="text-red-500">*</span></label>
              <input v-model="form.nombre" type="text" placeholder="Ej: Sede Norte"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.nombre ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.nombre" class="text-red-500 text-xs mt-1">{{ errores.nombre }}</p>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Ciudad</label>
              <input v-model="form.ciudad" type="text" placeholder="Ej: Quito"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div class="sm:col-span-2">
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Dirección <span class="text-red-500">*</span></label>
              <input v-model="form.direccion" type="text" placeholder="Ej: Av. 6 de Diciembre N23-45"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.direccion ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.direccion" class="text-red-500 text-xs mt-1">{{ errores.direccion }}</p>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Teléfono</label>
              <input v-model="form.telefono" type="text" placeholder="Ej: 022345678"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Fecha de Apertura</label>
              <input v-model="form.fechaApertura" type="date"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div class="sm:col-span-2 flex justify-end gap-3">
              <button type="button" @click="mostrarForm = false; sucursalEditar = null"
                class="px-5 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm rounded-xl transition-all cursor-pointer">
                Cancelar
              </button>
              <button type="submit" :disabled="loadingGuardar"
                class="px-5 py-2.5 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
                {{ loadingGuardar ? 'Guardando…' : sucursalEditar ? 'Guardar Cambios' : 'Registrar Sucursal' }}
              </button>
            </div>

          </form>
        </div>
      </Transition>

      <!-- Grid de sucursales -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <div v-for="i in 3" :key="i" class="h-36 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <div v-else-if="sucursales.length === 0" class="text-center py-20 text-slate-400">
        <svg class="w-12 h-12 mx-auto mb-3 opacity-30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
        <p class="font-semibold text-slate-500">No hay sucursales registradas</p>
        <p class="text-sm mt-1">La sede actual es la <strong>Matriz</strong></p>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5 mb-8">
        <div v-for="s in sucursales" :key="s.id"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5">
          <!-- Badge matriz/sucursal -->
          <div class="flex items-start justify-between mb-3">
            <span class="text-xs font-bold px-2.5 py-1 rounded-full"
              :class="s.id === 1 ? 'bg-blue-100 text-blue-700' : 'bg-slate-100 text-slate-600'">
              {{ s.id === 1 ? 'Matriz' : 'Sucursal' }}
            </span>
            <span class="text-xs font-bold px-2.5 py-1 rounded-full"
              :class="s.activo !== false ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'">
              {{ s.activo !== false ? 'Activa' : 'Inactiva' }}
            </span>
          </div>
          <h3 class="font-bold text-slate-800 text-base mb-1">{{ s.nombre }}</h3>
          <p class="text-slate-500 text-sm mb-0.5">
            <svg class="w-3.5 h-3.5 inline-block mr-1 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            {{ s.ciudad ? `${s.ciudad} — ` : '' }}{{ s.direccion }}
          </p>
          <p v-if="s.telefono" class="text-slate-400 text-xs mb-3">{{ s.telefono }}</p>
          <p v-if="s.fechaApertura" class="text-slate-400 text-xs mb-3">Apertura: {{ s.fechaApertura }}</p>
          <div class="flex gap-2 pt-3 border-t border-slate-100">
            <button @click="abrirEditar(s)"
              class="flex-1 bg-blue-50 hover:bg-blue-100 text-blue-600 font-semibold text-xs py-2 rounded-lg transition-all cursor-pointer">
              Editar
            </button>
            <button @click="sucursalEliminar = s"
              class="flex-1 bg-red-50 hover:bg-red-100 text-red-600 font-semibold text-xs py-2 rounded-lg transition-all cursor-pointer">
              Eliminar
            </button>
          </div>
        </div>
      </div>

    </main>

    <!-- Modal eliminar -->
    <Transition name="fade">
      <div v-if="sucursalEliminar" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
          <h3 class="text-lg font-bold text-slate-800 mb-2">¿Eliminar sucursal?</h3>
          <p class="text-sm text-slate-500 mb-5">
            Se desactivará <strong>{{ sucursalEliminar?.nombre }}</strong>. Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-3">
            <button @click="confirmarEliminar"
              class="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Sí, eliminar
            </button>
            <button @click="sucursalEliminar = null"
              class="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; }
.slide-down-enter-from,   .slide-down-leave-to     { opacity: 0; transform: translateY(-10px); }
</style>
