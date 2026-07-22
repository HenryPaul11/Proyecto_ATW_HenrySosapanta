<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import { httpClient } from '@/services/api'

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

const entrenador  = ref<any>(null)
const loading     = ref(false)
const message     = ref('')
const messageType = ref('')
const errores     = ref<Record<string, string>>({})

const form = ref({
  nombreCompleto: '', documentoIdentidad: '', telefono: '',
  email: '', especialidad: '', horario: '', fechaContratacion: '',
})

onMounted(async () => {
  try {
    const res = await httpClient.get(`/entrenadores/${route.params.id}`)
    entrenador.value = res.data
    form.value = {
      nombreCompleto:    res.data.nombreCompleto    ?? '',
      documentoIdentidad: res.data.documentoIdentidad ?? '',
      telefono:          res.data.telefono          ?? '',
      email:             res.data.email             ?? '',
      especialidad:      res.data.especialidad      ?? '',
      horario:           res.data.horario           ?? '',
      fechaContratacion: res.data.fechaContratacion ?? '',
    }
  } catch {
    router.push('/entrenadores')
  }
})

function validar(): boolean {
  errores.value = {}
  if (!form.value.nombreCompleto.trim()) errores.value.nombreCompleto = 'El nombre completo es obligatorio.'
  if (!form.value.documentoIdentidad.trim()) errores.value.documentoIdentidad = 'El documento es obligatorio.'
  return Object.keys(errores.value).length === 0
}

async function guardar() {
  message.value = ''
  if (!validar()) return
  loading.value = true
  try {
    await httpClient.put(`/entrenadores/${entrenador.value.id}`, {
      nombreCompleto:    form.value.nombreCompleto.trim(),
      documentoIdentidad: form.value.documentoIdentidad.trim(),
      telefono:          form.value.telefono.trim() || null,
      email:             form.value.email.trim() || null,
      especialidad:      form.value.especialidad.trim() || null,
      horario:           form.value.horario.trim() || null,
      fechaContratacion: form.value.fechaContratacion || null,
    })
    message.value     = '¡Los cambios se guardaron correctamente!'
    messageType.value = 'success'
    setTimeout(() => router.push('/entrenadores'), 1500)
  } catch (err: any) {
    message.value     = err?.error || 'No se pudieron guardar los cambios.'
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
          Editar Entrenador
        </h1>

        <div v-if="!entrenador" class="text-center py-10">
          <p class="text-slate-500 font-semibold mb-4">Entrenador no encontrado.</p>
          <router-link to="/entrenadores" class="text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            ← Volver a Entrenadores
          </router-link>
        </div>

        <template v-else>
          <Transition name="fade">
            <div v-if="message"
              class="flex items-start gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border"
              :class="messageType === 'success'
                ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                : 'bg-red-50 text-red-800 border-red-200'">
              <svg v-if="messageType === 'success'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
              <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              <span>{{ message }}</span>
            </div>
          </Transition>

          <form @submit.prevent="guardar" class="flex flex-col gap-4">

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Nombre completo <span class="text-red-500">*</span></label>
              <input v-model="form.nombreCompleto" type="text" required
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.nombreCompleto ? 'border-red-400' : 'border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-100'" />
              <p v-if="errores.nombreCompleto" class="text-red-500 text-xs mt-1">{{ errores.nombreCompleto }}</p>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Documento de identidad <span class="text-red-500">*</span></label>
              <input v-model="form.documentoIdentidad" type="text" required disabled
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm bg-slate-100 text-slate-400 cursor-not-allowed" />
              <p class="text-xs text-slate-400 mt-1">El documento no puede modificarse.</p>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Telefono</label>
                <input v-model="form.telefono" type="text"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Email</label>
                <input v-model="form.email" type="email"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Especialidad</label>
                <input v-model="form.especialidad" type="text"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Horario</label>
                <input v-model="form.horario" type="text" placeholder="Ej: Lun-Vie 06:00-14:00"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Fecha de contratacion</label>
              <input v-model="form.fechaContratacion" type="date"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div class="flex flex-col sm:flex-row gap-3 mt-2">
              <button type="submit" :disabled="loading"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
                {{ loading ? 'Guardando...' : 'Guardar Cambios' }}
              </button>
              <router-link to="/entrenadores"
                class="flex-1 text-center bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm py-3 rounded-xl transition-all">
                Cancelar
              </router-link>
            </div>

          </form>
        </template>

      </div>
    </main>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to      { opacity: 0; }
</style>
