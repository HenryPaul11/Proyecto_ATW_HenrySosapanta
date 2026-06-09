<script setup>
import { ref, onMounted } from 'vue'
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

const cliente     = ref(null)
const form        = ref({ nombre: '', apellido: '', cedula: '', telefono: '', email: '' })
const loading     = ref(false)
const message     = ref('')
const messageType = ref('')

onMounted(async () => {
  if (!store.clientes.length) await store.fetchClientes()
  const id    = Number(route.params.id)
  const found = store.clientes.find(c => c.id === id)
  if (found) { cliente.value = found; form.value = { ...found } }
})

async function guardar() {
  message.value = ''
  loading.value = true
  await new Promise(r => setTimeout(r, 400))

  const idx = store.clientes.findIndex(c => c.id === cliente.value.id)
  if (idx !== -1) {
    Object.assign(store.clientes[idx], { ...form.value })
    cliente.value = { ...store.clientes[idx] }
  }

  loading.value     = false
  message.value     = '¡Los cambios se guardaron correctamente!'
  messageType.value = 'success'
  setTimeout(() => router.push('/clientes'), 1500)
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 flex flex-col items-center px-4 py-8 md:py-10 fade-in">
      <div class="w-full max-w-xl bg-white rounded-2xl shadow-sm border border-slate-200 p-6 sm:p-8 md:p-10">

        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 text-center mb-6 tracking-tight">
          Editar Cliente
        </h1>

        <div v-if="!cliente" class="text-center py-10">
          <p class="text-slate-500 font-semibold mb-4">Cliente no encontrado.</p>
          <router-link to="/clientes" class="text-blue-500 hover:text-blue-700 text-sm font-semibold underline underline-offset-2">
            ← Volver a Clientes
          </router-link>
        </div>

        <template v-else>
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

          <form @submit.prevent="guardar" class="flex flex-col gap-4">

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="nombre" class="block text-sm font-semibold text-slate-700 mb-1.5">Nombre</label>
                <input id="nombre" v-model="form.nombre" type="text" required
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
              <div>
                <label for="apellido" class="block text-sm font-semibold text-slate-700 mb-1.5">Apellido</label>
                <input id="apellido" v-model="form.apellido" type="text" required
                  class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
              </div>
            </div>

            <div>
              <label for="cedula" class="block text-sm font-semibold text-slate-700 mb-1.5">Cédula</label>
              <input id="cedula" v-model="form.cedula" type="text" required
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div>
              <label for="telefono" class="block text-sm font-semibold text-slate-700 mb-1.5">Teléfono</label>
              <input id="telefono" v-model="form.telefono" type="text"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div>
              <label for="email" class="block text-sm font-semibold text-slate-700 mb-1.5">Email</label>
              <input id="email" v-model="form.email" type="email"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-slate-50 focus:bg-white" />
            </div>

            <div class="flex flex-col sm:flex-row gap-3 mt-2">
              <button type="submit" :disabled="loading"
                class="flex-1 bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 text-white font-bold text-sm py-3 rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
                {{ loading ? 'Guardando…' : 'Guardar Cambios' }}
              </button>
              <router-link to="/clientes"
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
