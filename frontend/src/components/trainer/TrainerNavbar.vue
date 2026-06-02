<template>
  <header class="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm">
    <div class="max-w-[1200px] mx-auto px-4 sm:px-8 h-16 flex items-center justify-between gap-4">

      <!-- Logo -->
      <div class="flex items-center gap-2.5 shrink-0">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-emerald-500 to-emerald-700 flex items-center justify-center shadow-sm">
          <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 9h12M6 15h12"/>
          </svg>
        </div>
        <span class="font-black text-lg tracking-tight text-slate-800 hidden sm:block">
          Power<span class="text-emerald-600">Fit</span>
        </span>
      </div>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-1">
        <router-link
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          class="flex items-center gap-1.5 text-slate-500 font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200 hover:text-emerald-600 hover:bg-emerald-50"
          active-class="text-emerald-600 bg-emerald-50"
        >
          <svg v-if="link.icon === 'home'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <svg v-else-if="link.icon === 'clientes'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <svg v-else-if="link.icon === 'sesiones'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
          <svg v-else-if="link.icon === 'perfil'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
          {{ link.label }}
        </router-link>
      </nav>

      <!-- Right -->
      <div class="hidden md:flex items-center gap-3">
        <div class="flex items-center gap-2 bg-emerald-50 rounded-xl px-3 py-1.5">
          <div class="w-6 h-6 rounded-full bg-emerald-600 flex items-center justify-center text-white text-xs font-bold">
            {{ inicial }}
          </div>
          <span class="text-emerald-700 font-semibold text-sm">{{ usuario }}</span>
          <span class="text-xs bg-emerald-200 text-emerald-800 font-bold px-1.5 py-0.5 rounded-full">Entrenador</span>
        </div>
        <button
          @click="$emit('logout')"
          class="flex items-center gap-1.5 bg-red-50 hover:bg-red-500 text-red-500 hover:text-white font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200 cursor-pointer border border-red-200 hover:border-red-500"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Salir
        </button>
      </div>

      <!-- Mobile -->
      <div class="flex items-center gap-2 md:hidden">
        <div class="w-8 h-8 rounded-full bg-emerald-600 flex items-center justify-center text-white text-xs font-bold">
          {{ inicial }}
        </div>
        <button @click="menuAbierto = !menuAbierto" class="p-2 rounded-lg text-slate-600 hover:bg-slate-100 transition-colors cursor-pointer">
          <svg v-if="!menuAbierto" class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <Transition name="slide-down">
      <div v-if="menuAbierto" class="md:hidden border-t border-slate-100 bg-white px-4 py-3 flex flex-col gap-1">
        <router-link
          v-for="link in navLinks" :key="link.to" :to="link.to"
          class="flex items-center gap-2.5 text-slate-600 font-semibold text-sm px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition-all"
          active-class="bg-emerald-50 text-emerald-600"
          @click="menuAbierto = false"
        >{{ link.label }}</router-link>
        <div class="border-t border-slate-100 mt-2 pt-2">
          <button @click="handleLogout" class="w-full flex items-center gap-2.5 text-red-500 font-semibold text-sm px-4 py-3 rounded-xl hover:bg-red-50 transition-all cursor-pointer">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            Cerrar sesión
          </button>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
const props = defineProps({ usuario: String })
const emit  = defineEmits(['logout'])
const menuAbierto = ref(false)
const inicial = computed(() => props.usuario?.charAt(0).toUpperCase() ?? '?')
const navLinks = [
  { to: '/entrenador/dashboard', label: 'Inicio',      icon: 'home'     },
  { to: '/entrenador/clientes',  label: 'Mis Clientes', icon: 'clientes' },
  { to: '/entrenador/sesiones',  label: 'Sesiones',    icon: 'sesiones' },
  { to: '/entrenador/perfil',    label: 'Mi Perfil',   icon: 'perfil'   },
]
function handleLogout() { menuAbierto.value = false; emit('logout') }
</script>

<style scoped>
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from,  .slide-down-leave-to      { opacity: 0; transform: translateY(-6px); }
</style>
