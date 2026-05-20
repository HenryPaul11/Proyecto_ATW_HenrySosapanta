<template>
  <header class="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm">
    <div class="max-w-[1400px] mx-auto px-4 sm:px-8 h-16 flex items-center justify-between gap-4">

      <!-- Logo -->
      <router-link to="/dashboard" class="flex items-center gap-2.5 shrink-0 group">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center shadow-sm group-hover:shadow-blue-300 transition-shadow">
          <!-- dumbbell icon -->
          <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 9h12M6 15h12"/>
          </svg>
        </div>
        <span class="font-black text-lg tracking-tight text-slate-800 hidden sm:block">
          Power<span class="text-blue-600">Fit</span>
        </span>
      </router-link>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-1">
        <router-link
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          class="flex items-center gap-1.5 text-slate-500 font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200 hover:text-blue-600 hover:bg-blue-50"
          active-class="text-blue-600 bg-blue-50"
        >
          <!-- dashboard -->
          <svg v-if="link.icon === 'dashboard'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
          </svg>
          <!-- membresias -->
          <svg v-else-if="link.icon === 'membresias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/>
          </svg>
          <!-- pagos -->
          <svg v-else-if="link.icon === 'pagos'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <!-- auditorias -->
          <svg v-else-if="link.icon === 'auditorias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
          </svg>
          {{ link.label }}
        </router-link>
      </nav>

      <!-- Right side -->
      <div class="hidden md:flex items-center gap-3">
        <div class="flex items-center gap-2 bg-slate-100 rounded-xl px-3 py-1.5">
          <div class="w-6 h-6 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold">
            {{ inicial }}
          </div>
          <span class="text-slate-700 font-semibold text-sm">{{ usuario }}</span>
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

      <!-- Mobile: avatar + hamburger -->
      <div class="flex items-center gap-2 md:hidden">
        <div class="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold">
          {{ inicial }}
        </div>
        <button
          @click="menuAbierto = !menuAbierto"
          class="p-2 rounded-lg text-slate-600 hover:bg-slate-100 transition-colors cursor-pointer"
        >
          <svg v-if="!menuAbierto" class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Mobile menu -->
    <Transition name="slide-down">
      <div v-if="menuAbierto" class="md:hidden border-t border-slate-100 bg-white px-4 py-3 flex flex-col gap-1">
        <router-link
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          class="flex items-center gap-2.5 text-slate-600 font-semibold text-sm px-4 py-3 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition-all"
          active-class="bg-blue-50 text-blue-600"
          @click="menuAbierto = false"
        >
          <svg v-if="link.icon === 'dashboard'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
          </svg>
          <svg v-else-if="link.icon === 'membresias'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/>
          </svg>
          <svg v-else-if="link.icon === 'pagos'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <svg v-else-if="link.icon === 'auditorias'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
          </svg>
          {{ link.label }}
        </router-link>
        <div class="border-t border-slate-100 mt-2 pt-2">
          <button
            @click="handleLogout"
            class="w-full flex items-center gap-2.5 text-red-500 font-semibold text-sm px-4 py-3 rounded-xl hover:bg-red-50 transition-all cursor-pointer"
          >
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
  { to: '/dashboard',  label: 'Dashboard',  icon: 'dashboard'  },
  { to: '/membresias', label: 'Membresías', icon: 'membresias' },
  { to: '/pagos',      label: 'Pagos',      icon: 'pagos'      },
  { to: '/auditorias', label: 'Auditorías', icon: 'auditorias' },
]

function handleLogout() { menuAbierto.value = false; emit('logout') }
</script>

<style scoped>
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from,  .slide-down-leave-to      { opacity: 0; transform: translateY(-6px); }
</style>
