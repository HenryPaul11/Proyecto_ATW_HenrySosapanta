<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useEntrenadorStore } from '@/stores/entrenadorStore'
import TrainerNavbar from '@/components/trainer/TrainerNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useEntrenadorStore()

function logout() { auth.logout(); router.push('/login') }

const loading     = ref(false)
const message     = ref('')
const messageType = ref('')
const form        = ref({ nombre: '', apellido: '', telefono: '', email: '' })

const inicial = computed(() => store.entrenador?.nombre?.charAt(0).toUpperCase() ?? '?')

function formatFecha(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(async () => {
  if (!store.entrenador) await store.fetchByUsuario(auth.usuario)
  if (store.entrenador) form.value = { ...store.entrenador }
})

async function guardar() {
  message.value = ''
  loading.value = true
  await new Promise(r => setTimeout(r, 400))

  if (store.entrenador) {
    store.entrenador.nombre   = form.value.nombre
    store.entrenador.apellido = form.value.apellido
    store.entrenador.telefono = form.value.telefono
    store.entrenador.email    = form.value.email
  }

  loading.value     = false
  message.value     = '¡Tu información se actualizó correctamente!'
  messageType.value = 'success'
  setTimeout(() => { message.value = '' }, 4000)
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-emerald-50 to-slate-100">
    <TrainerNavbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl">

        <!-- Avatar + nombre -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-5 flex items-center gap-5">
          <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-emerald-500 to-emerald-700 flex items-center justify-center text-white text-2xl font-black shrink-0">
            {{ inicial }}
          </div>
          <div>
            <h1 class="text-xl font-black text-slate-800">{{ store.entrenador?.nombre }} {{ store.entrenador?.apellido }}</h1>
            <p class="text-sm text-slate-400 font-medium mt-0.5">{{ store.entrenador?.email }}</p>
            <span class="inline-block mt-1.5 bg-emerald-100 text-emerald-700 text-xs font-bold px-2.5 py-0.5 rounded-full">
              Entrenador · {{ store.entrenador?.especialidad }}
            </span>
          </div>
        </div>

        <div v-if="store.loading" class="h-48 bg-slate-200 animate-pulse rounded-2xl" />

        <div v-else-if="!store.entrenador"
          class="bg-red-50 border-l-4 border-red-400 rounded-xl p-5 text-sm text-red-800 font-medium">
          No se encontró información de entrenador. Contacta con administración.
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

          <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 sm:p-8 mb-5">
            <h2 class="text-base font-bold text-slate-700 mb-5">Editar información personal</h2>
            <form @submit.prevent="guardar" class="flex flex-col gap-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-semibold text-slate-700 mb-1.5">Nombre</label>
                  <input v-model="form.nombre" type="text" required
                    class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all bg-slate-50 focus:bg-white" />
                </div>
                <div>
                  <label class="block text-sm font-semibold text-slate-700 mb-1.5">Apellido</label>
                  <input v-model="form.apellido" type="text" required
                    class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all bg-slate-50 focus:bg-white" />
                </div>
              </div>

              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Teléfono</label>
                <input v-model="form.telefono" type="text"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all bg-slate-50 focus:bg-white" />
              </div>

              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1.5">Email</label>
                <input v-model="form.email" type="email"
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 transition-all bg-slate-50 focus:bg-white" />
              </div>

              <button type="submit" :disabled="loading"
                class="w-full bg-gradient-to-r from-emerald-500 to-emerald-700 hover:from-emerald-600 hover:to-emerald-800 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer mt-2">
                {{ loading ? 'Guardando…' : 'Guardar Cambios' }}
              </button>
            </form>
          </div>

          <!-- Info profesional (solo lectura) -->
          <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
            <h2 class="text-base font-bold text-slate-700 mb-4">Información profesional</h2>
            <div class="space-y-3 text-sm divide-y divide-slate-100">
              <div class="flex justify-between py-2">
                <span class="font-semibold text-slate-500">Especialidad</span>
                <span class="text-slate-800 font-medium">{{ store.entrenador.especialidad }}</span>
              </div>
              <div class="flex justify-between py-2">
                <span class="font-semibold text-slate-500">Horario</span>
                <span class="text-slate-800 font-medium">{{ store.entrenador.horario }}</span>
              </div>
              <div class="flex justify-between py-2">
                <span class="font-semibold text-slate-500">Fecha de ingreso</span>
                <span class="text-slate-800 font-medium">{{ formatFecha(store.entrenador.fecha_ingreso) }}</span>
              </div>
              <div class="flex justify-between py-2">
                <span class="font-semibold text-slate-500">Rol</span>
                <span class="bg-emerald-100 text-emerald-700 text-xs font-bold px-2.5 py-0.5 rounded-full">Entrenador</span>
              </div>
            </div>
          </div>
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
