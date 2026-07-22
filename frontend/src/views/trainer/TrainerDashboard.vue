<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useEntrenadorStore } from '@/stores/entrenadorStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useEntrenadorStore()

function logout() { auth.logout(); router.push('/login') }

const navLinks = [
  { to: '/entrenador/dashboard', label: 'Inicio',    icon: 'home'     },
  { to: '/entrenador/sesiones',  label: 'Sesiones',  icon: 'sesiones' },
  { to: '/entrenador/perfil',    label: 'Mi Perfil', icon: 'perfil'   },
]

const hoy      = new Date().toISOString().slice(0, 10)
const fechaHoy = new Date().toLocaleDateString('es-EC', { weekday: 'long', day: '2-digit', month: 'long' })

const sesionesHoy        = computed(() => store.sesiones.filter(s => s.fecha === hoy))
const sesionesPendientes = computed(() => store.sesiones.filter(s => s.estado === 'ACTIVO'))

onMounted(() => store.fetchAll(auth.usuario))
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-emerald-50 to-slate-100">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Entrenador" variant="emerald" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <!-- Header -->
      <div class="mb-8">
        <p class="text-emerald-600 font-semibold text-sm mb-1">Panel de Entrenador</p>
        <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight flex items-center gap-2">
          Bienvenido, {{ store.entrenador?.nombreCompleto?.split(' ')[0] ?? auth.usuario }}
          <img src="/icons/musculo.svg" alt="💪" class="w-8 h-8 svg-icon" />
        </h1>
        <p class="text-slate-400 text-sm mt-1">{{ store.entrenador?.especialidad }} · {{ store.entrenador?.horario }}</p>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
        <div v-for="i in 2" :key="i" class="h-24 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <!-- Sin entrenador asociado -->
      <div v-else-if="!store.entrenador" class="bg-red-50 border-l-4 border-red-400 rounded-xl p-5 mb-6">
        <p class="text-red-800 font-bold text-sm">⚠️ No se encontró información de entrenador para tu usuario. Contacta con administración.</p>
      </div>

      <template v-else>
        <!-- Stats -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
          <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-blue-500 overflow-hidden">
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1 truncate">Sesiones Hoy</p>
            <p class="text-2xl font-black text-blue-600 truncate">{{ sesionesHoy.length }}</p>
            <p class="text-xs text-slate-400 mt-1 truncate">{{ fechaHoy }}</p>
          </div>
          <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 border-t-4 border-t-amber-500 overflow-hidden">
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1 truncate">Sesiones Pendientes</p>
            <p class="text-2xl font-black text-amber-600 truncate">{{ sesionesPendientes.length }}</p>
            <p class="text-xs text-slate-400 mt-1">proximas sesiones</p>
          </div>
        </div>

        <!-- Sesiones de hoy -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 mb-5">
          <h2 class="text-base font-bold text-slate-700 mb-4 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-blue-500 inline-block"></span>
            Sesiones de Hoy
          </h2>
          <div v-if="sesionesHoy.length === 0" class="text-slate-400 text-sm text-center py-6">
            No hay sesiones programadas para hoy.
          </div>
          <div v-else class="flex flex-col gap-3">
            <div v-for="s in sesionesHoy" :key="s.id"
              class="flex items-center justify-between gap-3 p-3 rounded-xl border border-slate-100 hover:bg-slate-50 transition-colors">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center text-blue-600 font-black text-sm shrink-0">
                  {{ s.hora.slice(0, 5) }}
                </div>
                <div>
                  <p class="font-semibold text-slate-800 text-sm">{{ s.clienteNombre ?? 'Sin cliente' }}</p>
                  <p class="text-xs text-slate-400">{{ s.tipo }}</p>
                </div>
              </div>
              <!-- Badge con SVG en lugar de emoji -->
              <span class="inline-flex items-center gap-1.5 text-xs font-bold px-2.5 py-1 rounded-full"
                :class="s.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'">
                <svg v-if="s.estado === 'ACTIVO'" class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
                {{ s.estado === 'ACTIVO' ? 'Activo' : 'Inactivo' }}
              </span>
            </div>
          </div>
        </div>

        <!-- Accesos rápidos -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <router-link to="/entrenador/sesiones"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-emerald-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
            <img src="/icons/calendario.svg" class="w-8 h-8 icon-slate" alt="" />
            <p class="font-bold text-slate-800 text-sm group-hover:text-emerald-600 transition-colors">Sesiones</p>
            <p class="text-xs text-slate-400">Historial y próximas sesiones</p>
          </router-link>
          <router-link to="/entrenador/perfil"
            class="bg-white rounded-2xl border-2 border-slate-200 hover:border-emerald-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
            <img src="/icons/usuario.svg" class="w-8 h-8 icon-slate" alt="" />
            <p class="font-bold text-slate-800 text-sm group-hover:text-emerald-600 transition-colors">Mi Perfil</p>
            <p class="text-xs text-slate-400">Actualiza tu información</p>
          </router-link>
        </div>
      </template>

    </main>
    <Footer />
  </div>
</template>

<style scoped>
.svg-icon  { filter: brightness(0) saturate(100%) opacity(0.85); }
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
.icon-amber { filter: brightness(0) saturate(100%) invert(55%) sepia(60%) saturate(600%) hue-rotate(10deg) opacity(0.9); }
html.dark .svg-icon,
html.dark .icon-slate { filter: brightness(0) invert(1) opacity(0.8); }
</style>
