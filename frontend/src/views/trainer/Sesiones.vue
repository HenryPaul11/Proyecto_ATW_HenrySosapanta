<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'
import PaginatedTable from '@/components/shared/PaginatedTable.vue'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const navLinks = [
  { to: '/entrenador/dashboard', label: 'Inicio',    icon: 'home'     },
  { to: '/entrenador/sesiones',  label: 'Sesiones',  icon: 'sesiones' },
  { to: '/entrenador/perfil',    label: 'Mi Perfil', icon: 'perfil'   },
]

const filtroActivo = ref('todos')
const filtros = [
  { label: 'Todas',       value: 'todos'      },
  { label: 'Activas',     value: 'ACTIVO'     },
  { label: 'Inactivas',   value: 'INACTIVO'   },
]
const columnas = ['Cliente', 'Fecha', 'Hora', 'Tipo', 'Estado', 'Notas']

const filtrosExternos = computed(() => ({
  entrenadorEmail: auth.usuario || undefined,
  estado: filtroActivo.value !== 'todos' ? filtroActivo.value : undefined,
}))

function formatFecha(d: string) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-emerald-50 to-slate-100">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Entrenador" variant="emerald" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight mb-8 flex items-center gap-2">
        <img src="/icons/calendario.svg" class="w-8 h-8 icon-slate" alt="" />
        Sesiones de Entrenamiento
      </h1>

      <!-- Filtros -->
      <div class="flex flex-col sm:flex-row gap-3 mb-6 items-center justify-between bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
        <div class="flex gap-2 flex-wrap">
          <button v-for="f in filtros" :key="f.value" @click="filtroActivo = f.value"
            class="px-4.5 py-2.5 rounded-xl text-xs font-bold transition-all cursor-pointer shadow-2xs border"
            :class="filtroActivo === f.value
              ? 'bg-emerald-500 text-white border-emerald-500 shadow-sm'
              : 'bg-white text-slate-500 border-slate-200 hover:border-emerald-300'">
            {{ f.label }}
          </button>
        </div>
        <p class="text-xs text-slate-400 font-semibold uppercase">Sesiones asignadas</p>
      </div>

      <!-- Tabla Paginada -->
      <div class="mb-8">
        <PaginatedTable
          endpoint="/sesiones"
          :filtros="filtrosExternos"
          :columns="columnas"
          endpoint-key="sesiones"
        >
          <template #default="{ items }">
            <tr v-for="(s, i) in items" :key="s.id"
              class="border-b border-slate-100 transition-colors hover:bg-emerald-50 bg-white"
              :class="i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'">
              <td class="px-4 py-3 text-sm font-semibold text-slate-800 whitespace-nowrap">{{ s.clienteNombre ?? 'Sin cliente' }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ formatFecha(s.fecha) }}</td>
              <td class="px-4 py-3 text-sm text-slate-500 whitespace-nowrap">{{ s.hora }}</td>
              <td class="px-4 py-3 whitespace-nowrap">
                <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-blue-100 text-blue-700">
                  {{ s.tipo }}
                </span>
              </td>
              <td class="px-4 py-3 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold"
                  :class="s.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'">
                  <svg v-if="s.estado === 'ACTIVO'" class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  <svg v-else class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                  </svg>
                  {{ s.estado === 'ACTIVO' ? 'Activa' : 'Inactiva' }}
                </span>
              </td>
              <td class="px-4 py-3 text-xs text-slate-400 max-w-[180px] truncate">{{ s.notas || '—' }}</td>
            </tr>
          </template>
        </PaginatedTable>
      </div>

    </main>
    <Footer />
  </div>
</template>

<style scoped>
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
.icon-amber { filter: brightness(0) saturate(100%) invert(55%) sepia(60%) saturate(600%) hue-rotate(10deg) opacity(0.9); }
html.dark .icon-slate,
html.dark .icon-amber { filter: brightness(0) invert(1) opacity(0.8); }
</style>
