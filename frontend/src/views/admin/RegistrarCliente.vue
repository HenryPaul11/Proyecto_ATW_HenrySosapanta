<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useClientesStore } from '@/stores/clientesStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useClientesStore()

function logout() { auth.logout(); router.push('/login') }

const loading     = ref(false)
const message     = ref('')
const messageType = ref('')
const errores     = ref<Record<string, string>>({})

const form = ref({ nombre: '', apellido: '', cedula: '', telefono: '', email: '' })

onMounted(() => { if (!store.clientes.length) store.fetchClientes() })

// ── Validaciones ──────────────────────────────────────────────
function validar(): boolean {
  errores.value = {}
  const { nombre, apellido, cedula, telefono, email } = form.value

  if (!nombre.trim())
    errores.value.nombre = 'El nombre es obligatorio.'
  else if (nombre.trim().length < 2)
    errores.value.nombre = 'Mínimo 2 caracteres.'

  if (!apellido.trim())
    errores.value.apellido = 'El apellido es obligatorio.'
  else if (apellido.trim().length < 2)
    errores.value.apellido = 'Mínimo 2 caracteres.'

  if (!cedula.trim())
    errores.value.cedula = 'La cédula es obligatoria.'
  else if (!/^\d{10}$/.test(cedula.trim()))
    errores.value.cedula = 'La cédula debe tener exactamente 10 dígitos numéricos.'
  else if (store.clientes.find(c => c.cedula === cedula.trim()))
    errores.value.cedula = 'Esta cédula ya está registrada.'

  if (telefono.trim() && !/^\d{7,15}$/.test(telefono.trim()))
    errores.value.telefono = 'El teléfono debe tener entre 7 y 15 dígitos.'

  if (!email.trim())
    errores.value.email = 'El correo electrónico es obligatorio.'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim()))
    errores.value.email = 'Ingresa un correo electrónico válido (ej: juan@mail.com).'

  return Object.keys(errores.value).length === 0
}

async function registrar() {
  message.value = ''
  if (!validar()) return

  const { nombre, apellido, cedula, telefono, email } = form.value

  loading.value = true
  await new Promise(r => setTimeout(r, 400))

  const nuevoId = store.clientes.length ? Math.max(...store.clientes.map(c => c.id)) + 1 : 1
  store.clientes.push({
    id:       nuevoId,
    nombre:   nombre.trim(),
    apellido: apellido.trim(),
    cedula:   cedula.trim(),
    telefono: telefono.trim(),
    email:    email.trim(),
  })

  loading.value     = false
  message.value     = '¡Cliente registrado exitosamente!'
  messageType.value = 'success'
  form.value        = { nombre: '', apellido: '', cedula: '', telefono: '', email: '' }
  errores.value     = {}

  setTimeout(() => router.push('/clientes'), 1500)
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl bg-white rounded-2xl shadow-sm border border-slate-200 p-6 sm:p-8 md:p-10">

        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 text-center mb-6 tracking-tight">
          Registrar Cliente
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

        <form @submit.prevent="registrar" class="flex flex-col gap-4">

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label for="nombre" class="block text-sm font-semibold text-slate-700 mb-1.5">
                Nombre <span class="text-red-500">*</span>
              </label>
              <input id="nombre" v-model="form.nombre" type="text" placeholder="Ej: Juan"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.nombre ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.nombre" class="text-red-500 text-xs mt-1 flex items-center gap-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                {{ errores.nombre }}
              </p>
            </div>
            <div>
              <label for="apellido" class="block text-sm font-semibold text-slate-700 mb-1.5">
                Apellido <span class="text-red-500">*</span>
              </label>
              <input id="apellido" v-model="form.apellido" type="text" placeholder="Ej: Pérez"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.apellido ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.apellido" class="text-red-500 text-xs mt-1 flex items-center gap-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                {{ errores.apellido }}
              </p>
            </div>
          </div>

          <div>
            <label for="cedula" class="block text-sm font-semibold text-slate-700 mb-1.5">
              Cédula <span class="text-red-500">*</span>
            </label>
            <input id="cedula" v-model="form.cedula" type="text" placeholder="Ej: 1234567890"
              class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
              :class="errores.cedula ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
            <p v-if="errores.cedula" class="text-red-500 text-xs mt-1 flex items-center gap-1">
              <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              {{ errores.cedula }}
            </p>
            <p v-else class="text-slate-400 text-xs mt-1">10 dígitos numéricos.</p>
          </div>

          <div>
            <label for="telefono" class="block text-sm font-semibold text-slate-700 mb-1.5">Teléfono</label>
            <input id="telefono" v-model="form.telefono" type="text" placeholder="Ej: 0991234567"
              class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
              :class="errores.telefono ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
            <p v-if="errores.telefono" class="text-red-500 text-xs mt-1 flex items-center gap-1">
              <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              {{ errores.telefono }}
            </p>
          </div>

          <div>
            <label for="email" class="block text-sm font-semibold text-slate-700 mb-1.5">
              Email <span class="text-red-500">*</span>
            </label>
            <input id="email" v-model="form.email" type="text" placeholder="Ej: juan@mail.com"
              class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
              :class="errores.email ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
            <p v-if="errores.email" class="text-red-500 text-xs mt-1 flex items-center gap-1">
              <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              {{ errores.email }}
            </p>
          </div>

          <p class="text-xs text-slate-400 -mt-1">Los campos marcados con <span class="text-red-500 font-bold">*</span> son obligatorios.</p>

          <div class="flex flex-col sm:flex-row gap-3 mt-2">
            <button type="submit" :disabled="loading"
              class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
              {{ loading ? 'Registrando…' : 'Registrar Cliente' }}
            </button>
            <router-link to="/clientes"
              class="flex-1 text-center bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm py-3 rounded-xl transition-all">
              Cancelar
            </router-link>
          </div>

        </form>
      </div>
    </main>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
