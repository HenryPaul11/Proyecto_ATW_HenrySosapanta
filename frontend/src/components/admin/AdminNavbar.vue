<template>
  <header class="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm">
    <div class="max-w-[1200px] mx-auto px-4 sm:px-8 h-16 flex items-center justify-between gap-4">

      <!-- Logo -->
      <div class="flex items-center gap-2.5 shrink-0">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center shadow-sm">
          <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 9h12M6 15h12"/>
          </svg>
        </div>
        <span class="font-black text-lg tracking-tight text-slate-800 hidden sm:block">
          Power<span class="text-blue-600">Fit</span>
        </span>
      </div>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-1">
        <router-link
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          class="flex items-center gap-1.5 text-slate-500 font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200 hover:text-blue-600 hover:bg-blue-50"
          active-class="text-blue-600 bg-blue-50"
        >
          <!-- home -->
          <svg v-if="link.icon === 'home'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <!-- clientes -->
          <svg v-else-if="link.icon === 'clientes'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <!-- membresias -->
          <svg v-else-if="link.icon === 'membresias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
          </svg>
          <!-- pagos -->
          <svg v-else-if="link.icon === 'pagos'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <!-- auditorias -->
          <svg v-else-if="link.icon === 'auditorias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          <!-- equipos — pesa -->
          <img v-else-if="link.icon === 'equipos'" src="/icons/pesa.svg" alt="Equipos" class="w-4 h-4 object-contain dark-icon" />
          {{ link.label }}
        </router-link>
      </nav>

      <!-- Right: usuario + dark mode + logout -->
      <div class="hidden md:flex items-center gap-3">
        <div class="flex items-center gap-2 bg-blue-50 rounded-xl px-3 py-1.5">
          <div class="w-6 h-6 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold">
            {{ inicial }}
          </div>
          <span class="text-blue-700 font-semibold text-sm">{{ usuario }}</span>
          <span class="text-xs bg-blue-200 text-blue-800 font-bold px-1.5 py-0.5 rounded-full">Admin</span>
        </div>
        <button
          @click="theme.toggle()"
          class="p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors cursor-pointer"
          :title="theme.dark ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
          aria-label="Toggle dark mode"
        >
          <!-- Sol (modo claro) -->
          <svg v-if="theme.dark" class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1"  x2="12" y2="3"/>
            <line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22"  x2="5.64" y2="5.64"/>
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1"  y1="12" x2="3"  y2="12"/>
            <line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <!-- Luna (modo oscuro) -->
          <svg v-else class="w-5 h-5 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>
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

      <!-- Mobile hamburger -->
      <div class="flex items-center gap-2 md:hidden">
        <button
          @click="theme.toggle()"
          class="p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors cursor-pointer"
        >
          <svg v-if="theme.dark" class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1"  x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <svg v-else class="w-5 h-5 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>
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
        >{{ link.label }}</router-link>
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
import { useThemeStore } from '@/stores/themeStore'

const props = defineProps({ usuario: String })
const emit  = defineEmits(['logout'])

const menuAbierto = ref(false)
const inicial = computed(() => props.usuario?.charAt(0).toUpperCase() ?? '?')
const theme = useThemeStore()

const navLinks = [
  { to: '/dashboard',   label: 'Inicio',       icon: 'home'       },
  { to: '/clientes',    label: 'Clientes',      icon: 'clientes'   },
  { to: '/membresias',  label: 'Membresías',    icon: 'membresias' },
  { to: '/equipos',     label: 'Equipos',       icon: 'equipos'    },
  { to: '/pagos',       label: 'Pagos',         icon: 'pagos'      },
  { to: '/auditorias',  label: 'Auditorías',    icon: 'auditorias' },
]

function handleLogout() { menuAbierto.value = false; emit('logout') }
</script>

<style scoped>
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from,  .slide-down-leave-to      { opacity: 0; transform: translateY(-6px); }

/* Ícono pesa negro — se adapta al color del navbar */
.dark-icon { filter: brightness(0) saturate(100%) opacity(0.6); }
html.dark .dark-icon { filter: brightness(0) invert(1) opacity(0.8); }
</style>
