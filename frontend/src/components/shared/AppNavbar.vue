<template>
  <header class="sticky top-0 z-50 bg-white/95 dark:bg-slate-900/95 backdrop-blur border-b border-slate-200 dark:border-slate-700 shadow-sm">
    <div class="max-w-[1200px] mx-auto px-4 sm:px-8 h-16 flex items-center justify-between gap-4">

      <!-- Logo -->
      <div class="flex items-center gap-2.5 shrink-0">
        <div class="w-9 h-9 rounded-xl flex items-center justify-center shadow-sm"
          :class="colors.logoBg">
          <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 9h12M6 15h12"/>
          </svg>
        </div>
        <span class="font-black text-lg tracking-tight text-slate-800 dark:text-slate-100 hidden sm:block">
          Power<span :class="colors.brandText">Fit</span>
        </span>
      </div>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-1">
        <router-link
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="flex items-center gap-1.5 text-slate-500 dark:text-slate-400 font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200"
          :class="colors.navHover"
          active-class="bg-blue-50 dark:bg-blue-900/40 text-blue-600 dark:text-blue-400"
        >
          <svg v-if="link.icon === 'home'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <svg v-else-if="link.icon === 'clientes'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <svg v-else-if="link.icon === 'membresias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
          </svg>
          <svg v-else-if="link.icon === 'membresia'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/>
          </svg>
          <svg v-else-if="link.icon === 'pagos'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <svg v-else-if="link.icon === 'auditorias'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          <img v-else-if="link.icon === 'equipos'" src="/icons/pesa.svg" alt="" class="w-4 h-4 object-contain dark-icon" />
          <svg v-else-if="link.icon === 'sesiones'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
          <svg v-else-if="link.icon === 'perfil'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
          {{ link.label }}
        </router-link>
      </nav>

      <!-- Right: usuario + dark mode + logout -->
      <div class="hidden md:flex items-center gap-3">
        <div class="flex items-center gap-2 rounded-xl px-3 py-1.5" :class="colors.userBgDark">
          <div class="w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-bold" :class="colors.avatarBg">{{ inicial }}</div>
          <span class="font-semibold text-sm" :class="colors.userTextDark">{{ usuario }}</span>
          <span v-if="badge" class="text-xs font-bold px-1.5 py-0.5 rounded-full" :class="colors.badgeBgDark">{{ badge }}</span>
        </div>

        <button @click="theme.toggle()"
          class="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors cursor-pointer"
          :title="theme.dark ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'">
          <svg v-if="theme.dark" class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <svg v-else class="w-5 h-5 text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>

        <button @click="$emit('logout')"
          class="flex items-center gap-1.5 bg-red-50 hover:bg-red-500 text-red-500 hover:text-white font-semibold text-sm px-3.5 py-2 rounded-lg transition-all duration-200 cursor-pointer border border-red-200 hover:border-red-500 dark:bg-red-900/30 dark:text-red-400 dark:border-red-800 dark:hover:bg-red-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
          </svg>
          Salir
        </button>
      </div>

      <!-- Mobile -->
      <div class="flex items-center gap-2 md:hidden">
        <button @click="theme.toggle()" class="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors cursor-pointer">
          <svg v-if="theme.dark" class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <svg v-else class="w-5 h-5 text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>
        <div class="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold" :class="colors.avatarBg">{{ inicial }}</div>
        <button @click="menuAbierto = !menuAbierto" class="p-2 rounded-lg text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors cursor-pointer">
          <svg v-if="!menuAbierto" class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16"/>
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Mobile menu -->
    <Transition name="slide-down">
      <div v-if="menuAbierto" class="md:hidden border-t border-slate-100 dark:border-slate-700 bg-white dark:bg-slate-900 px-4 py-3 flex flex-col gap-1">
        <router-link
          v-for="link in links" :key="link.to" :to="link.to"
          class="flex items-center gap-2.5 text-slate-600 dark:text-slate-300 font-semibold text-sm px-4 py-3 rounded-xl transition-all"
          :class="colors.mobileHover"
          @click="menuAbierto = false"
        >{{ link.label }}</router-link>
        <div class="border-t border-slate-100 dark:border-slate-700 mt-2 pt-2">
          <button @click="handleLogout"
            class="w-full flex items-center gap-2.5 text-red-500 dark:text-red-400 font-semibold text-sm px-4 py-3 rounded-xl hover:bg-red-50 dark:hover:bg-red-900/30 transition-all cursor-pointer">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
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

const props = defineProps({
  usuario: String,
  links: { type: Array, required: true },
  badge: { type: String, default: '' },
  variant: { type: String, default: 'blue' },
})
const emit = defineEmits(['logout'])

const menuAbierto = ref(false)
const inicial     = computed(() => props.usuario?.charAt(0).toUpperCase() ?? '?')
const theme       = useThemeStore()

const colorMap = {
  blue: {
    logoBg: 'bg-gradient-to-br from-blue-500 to-blue-700',
    brandText: 'text-blue-600 dark:text-blue-400',
    navHover: 'hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/30',
    userBgDark: 'bg-blue-50 dark:bg-blue-900/40',
    userTextDark: 'text-blue-700 dark:text-blue-300',
    avatarBg: 'bg-blue-600',
    badgeBgDark: 'bg-blue-200 dark:bg-blue-800 text-blue-800 dark:text-blue-200',
    mobileHover: 'hover:bg-blue-50 dark:hover:bg-blue-900/30 hover:text-blue-600 dark:hover:text-blue-400',
  },
  emerald: {
    logoBg: 'bg-gradient-to-br from-emerald-500 to-emerald-700',
    brandText: 'text-emerald-600 dark:text-emerald-400',
    navHover: 'hover:text-emerald-600 dark:hover:text-emerald-400 hover:bg-emerald-50 dark:hover:bg-emerald-900/30',
    userBgDark: 'bg-emerald-50 dark:bg-emerald-900/40',
    userTextDark: 'text-emerald-700 dark:text-emerald-300',
    avatarBg: 'bg-emerald-600',
    badgeBgDark: 'bg-emerald-200 dark:bg-emerald-800 text-emerald-800 dark:text-emerald-200',
    mobileHover: 'hover:bg-emerald-50 dark:hover:bg-emerald-900/30 hover:text-emerald-600 dark:hover:text-emerald-400',
  },
}

const colors = computed(() => colorMap[props.variant] || colorMap.blue)

function handleLogout() { menuAbierto.value = false; emit('logout') }
</script>

<style scoped>
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from,   .slide-down-leave-to     { opacity: 0; transform: translateY(-6px); }
.dark-icon { filter: brightness(0) saturate(100%) opacity(0.6); }
html.dark .dark-icon { filter: brightness(0) invert(1) opacity(0.8); }
</style>
