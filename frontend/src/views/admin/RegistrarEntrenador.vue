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
  nombre: '', apellido: '', cedula: '', telefono: '', email: '',
  especialidad: '', horario: '', fechaIngreso: '',
  contrasena: '',
})

function validar(): boolean {
  errores.value = {}
  const { nombre, apellido, cedula, email, contrasena } = form.value

  if (!nombre.trim()) errores.value.nombre = 'El nombre es obligatorio.'
  if (!apellido.trim()) errores.value.apellido = 'El apellido es obligatorio.'
  if (!cedula.trim()) errores.value.cedula = 'La cédula es obligatoria.'
  else if (!/^\d{10}$/.test(cedula.trim())) errores.value.cedula = 'Exactamente 10 dígitos.'
  if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) errores.value.email = 'Correo inválido.'
  if (!contrasena) errores.value.contrasena = 'La contraseña es obligatoria.'
  else if (contrasena.length < 6) errores.value.contrasena = 'Mínimo 6 caracteres.'

  return Object.keys(errores.value).length === 0
}

async function registrar() {
  message.value = ''
  if (!validar()) return
  loading.value = true
  try {
    const sucursalId = auth.sucursalId ?? auth.sucursalActivaId ?? 1

    await httpClient.post('/entrenadores', {
      nombreCompleto:     `${form.value.nombre.trim()} ${form.value.apellido.trim()}`,
      documentoIdentidad: form.value.cedula.trim(),
      email:              form.value.email.trim() || null,
      telefono:           form.value.telefono.trim() || null,
      especialidad:       form.value.especialidad.trim() || null,
      horario:            form.value.horario.trim() || null,
      fechaIngreso:       form.value.fechaIngreso || null,
      contrasena:         form.value.contrasena,
      sucursalId,
    })

    message.value     = '¡Entrenador y usuario registrados exitosamente!'
    messageType.value = 'success'
    form.value = { nombre: '', apellido: '', cedula: '', telefono: '', email: '', especialidad: '', horario: '', fechaIngreso: '', contrasena: '' }
    errores.value = {}
    setTimeout(() => router.push('/entrenadores'), 1500)
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
          Registrar Entrenador
        </h1>

        <Transition name="fade">
          <div v-if="message"
            class="flex items-start gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border"
            :class="messageType === 'success' ? 'bg-emerald-50 text-emerald-800 border-emerald-200' : 'bg-red-50 text-red-800 border-red-200'">
            <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            <span>{{ message }}</span>
          </div>
        </Transition>

        <form @submit.prevent="registrar" class="flex flex-col gap-4">

          <!-- Nombre / Apellido -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Nombre <span class="text-red-500">*</span></label>
              <input v-model="form.nombre" type="text" placeholder="Ej: Carlos"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.nombre ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.nombre" class="text-red-500 text-xs mt-1">{{ errores.nombre }}</p>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Apellido <span class="text-red-500">*</span></label>
              <input v-model="form.apellido" type="text" placeholder="Ej: Mendoza"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.apellido ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.apellido" class="text-red-500 text-xs mt-1">{{ errores.apellido }}</p>
            </div>
          </div>

          <!-- Cédula -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1.5">Cédula <span class="text-red-500">*</span></label>
            <input v-model="form.cedula" type="text" placeholder="Ej: 0501234567"
              class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
              :class="errores.cedula ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
            <p v-if="errores.cedula" class="text-red-500 text-xs mt-1">{{ errores.cedula }}</p>
          </div>

          <!-- Teléfono / Email -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Teléfono</label>
              <input v-model="form.telefono" type="text" placeholder="Ej: 0981234567"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Email</label>
              <input v-model="form.email" type="text" placeholder="Ej: carlos@powerfit.com"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.email ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.email" class="text-red-500 text-xs mt-1">{{ errores.email }}</p>
            </div>
          </div>

          <!-- Especialidad / Horario -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Especialidad</label>
              <input v-model="form.especialidad" type="text" placeholder="Ej: Musculación y Fuerza"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Horario</label>
              <input v-model="form.horario" type="text" placeholder="Ej: Lun-Vie 06:00–14:00"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>
          </div>

          <!-- Fecha ingreso -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1.5">Fecha de Ingreso</label>
            <input v-model="form.fechaIngreso" type="date"
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
          </div>

          <!-- Acceso al sistema -->
          <div class="border-t border-slate-200 pt-4 mt-1">
            <p class="text-sm font-bold text-slate-600 mb-3">Acceso al sistema</p>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Contraseña <span class="text-red-500">*</span></label>
              <input v-model="form.contrasena" type="password" placeholder="Mínimo 6 caracteres"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.contrasena ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.contrasena" class="text-red-500 text-xs mt-1">{{ errores.contrasena }}</p>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 mt-2">
            <button type="submit" :disabled="loading"
              class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
              {{ loading ? 'Registrando…' : 'Registrar Entrenador' }}
            </button>
            <router-link to="/entrenadores"
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
