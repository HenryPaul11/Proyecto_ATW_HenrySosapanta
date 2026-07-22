<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import { httpClient } from '@/services/api'

const router = useRouter()
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

const loading     = ref(false)
const message     = ref('')
const messageType = ref('')
const errores     = ref<Record<string, string>>({})

const form = ref({
  nombre: '', apellido: '', cedula: '', telefono: '', email: '', genero: '',
  contrasena: '',
})

function validar(): boolean {
  errores.value = {}
  const { nombre, apellido, cedula, email, contrasena } = form.value

  if (!nombre.trim()) errores.value.nombre = 'El nombre es obligatorio.'
  else if (nombre.trim().length < 2) errores.value.nombre = 'Mínimo 2 caracteres.'

  if (!apellido.trim()) errores.value.apellido = 'El apellido es obligatorio.'
  else if (apellido.trim().length < 2) errores.value.apellido = 'Mínimo 2 caracteres.'

  if (!cedula.trim()) errores.value.cedula = 'La cédula es obligatoria.'
  else if (!/^\d{10}$/.test(cedula.trim())) errores.value.cedula = 'Exactamente 10 dígitos numéricos.'

  if (form.value.telefono.trim() && !/^\d{7,15}$/.test(form.value.telefono.trim()))
    errores.value.telefono = 'Entre 7 y 15 dígitos.'

  if (!email.trim()) errores.value.email = 'El correo es obligatorio.'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) errores.value.email = 'Correo inválido.'

  if (!contrasena.trim()) errores.value.contrasena = 'La contraseña es obligatoria.'
  else if (contrasena.length < 6) errores.value.contrasena = 'Mínimo 6 caracteres.'

  return Object.keys(errores.value).length === 0
}

async function registrar() {
  message.value = ''
  if (!validar()) return
  loading.value = true
  try {
    const sucursalId = auth.sucursalId ?? auth.sucursalMatrizId ?? 1

    await httpClient.post('/clientes', {
      nombreCompleto:     `${form.value.nombre.trim()} ${form.value.apellido.trim()}`,
      documentoIdentidad: form.value.cedula.trim(),
      email:              form.value.email.trim(),
      telefono:           form.value.telefono.trim() || null,
      genero:             form.value.genero || null,
      contrasena:         form.value.contrasena,
      sucursalId,
    })

    message.value     = '¡Cliente y usuario registrados exitosamente!'
    messageType.value = 'success'
    form.value = { nombre: '', apellido: '', cedula: '', telefono: '', email: '', genero: '', contrasena: '' }
    errores.value = {}
    setTimeout(() => router.push('/clientes'), 1500)
  } catch (err: any) {
    message.value     = err?.error || 'No se pudo registrar. Verifica los datos.'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

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
            <input id="email" v-model="form.email" type="text" placeholder="Ej: juan@powerfit.com"
              class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
              :class="errores.email ? 'border-red-400 focus:border-red-500 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
            <p v-if="errores.email" class="text-red-500 text-xs mt-1 flex items-center gap-1">
              <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              {{ errores.email }}
            </p>
          </div>

          <div>
            <label for="genero" class="block text-sm font-semibold text-slate-700 mb-1.5">Género</label>
            <select id="genero" v-model="form.genero"
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white">
              <option value="">Seleccionar…</option>
              <option value="Masculino">Masculino</option>
              <option value="Femenino">Femenino</option>
              <option value="Otro">Otro</option>
            </select>
          </div>

          <p class="text-xs text-slate-400 -mt-1">Los campos marcados con <span class="text-red-500 font-bold">*</span> son obligatorios.</p>

          <!-- Contraseña acceso -->
          <div class="border-t border-slate-200 pt-4 mt-1">
            <p class="text-sm font-bold text-slate-600 mb-3">Acceso al sistema</p>
            <div>
              <label for="contrasena" class="block text-sm font-semibold text-slate-700 mb-1.5">
                Contraseña <span class="text-red-500">*</span>
              </label>
              <input id="contrasena" v-model="form.contrasena" type="password" placeholder="Mínimo 6 caracteres"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.contrasena ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.contrasena" class="text-red-500 text-xs mt-1">{{ errores.contrasena }}</p>
            </div>
          </div>

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
