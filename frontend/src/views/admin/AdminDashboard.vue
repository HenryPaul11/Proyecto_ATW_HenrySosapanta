<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useAdminStore } from '@/stores/adminStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const admin  = useAdminStore()

function logout() { auth.logout(); router.push('/login') }

onMounted(() => admin.fetchStats())
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-blue-50 to-slate-100">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <!-- Header -->
      <div class="mb-8">
        <p class="text-blue-600 font-semibold text-sm mb-1">Panel de Administración</p>
        <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight flex items-center gap-2">
          Bienvenido, {{ auth.usuario }}
          <img src="/icons/musculo.svg" alt="💪" class="w-8 h-8 icon-slate" />
        </h1>
        <p class="text-slate-400 text-sm mt-1">Resumen general del sistema</p>
      </div>

      <!-- Stats skeleton -->
      <div v-if="admin.loading" class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div v-for="i in 3" :key="i" class="h-24 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <!-- Stats -->
      <div v-else-if="admin.stats" class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-blue-500">
          <div class="flex items-center gap-2 mb-1">
            <img src="/icons/grupo.svg" class="w-5 h-5 icon-slate" alt="" />
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Clientes Registrados</p>
          </div>
          <p class="text-3xl font-black text-blue-600">{{ admin.stats.clientes }}</p>
          <p class="text-xs text-slate-400 mt-1">total en el sistema</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-sky-500">
          <div class="flex items-center gap-2 mb-1">
            <img src="/icons/boleto.svg" class="w-5 h-5 icon-slate" alt="" />
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Membresías Activas</p>
          </div>
          <p class="text-3xl font-black text-sky-500">{{ admin.stats.membresias }}</p>
          <p class="text-xs text-slate-400 mt-1">activas este mes</p>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 border-t-4 border-t-emerald-500">
          <div class="flex items-center gap-2 mb-1">
            <img src="/icons/efectivo.svg" class="w-5 h-5 icon-slate" alt="" />
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Ingresos Mensuales</p>
          </div>
          <p class="text-3xl font-black text-emerald-600">${{ Number(admin.stats.ingresos).toFixed(2) }}</p>
          <p class="text-xs text-slate-400 mt-1">mes actual</p>
        </div>
      </div>

      <!-- Acciones rápidas -->
      <p class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-4">Acciones rápidas</p>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <router-link to="/membresias"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/boleto.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Gestionar Membresías</p>
          <p class="text-xs text-slate-400">Consulta, asigna y renueva membresías</p>
        </router-link>

        <router-link to="/clientes"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/grupo.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Gestionar Clientes</p>
          <p class="text-xs text-slate-400">Registra, edita y elimina clientes</p>
        </router-link>

        <router-link to="/pagos"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/tarjeta.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Ver Pagos</p>
          <p class="text-xs text-slate-400">Historial de transacciones y cobros</p>
        </router-link>

        <router-link to="/auditorias"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-blue-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/usuario.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">Auditorías</p>
          <p class="text-xs text-slate-400">Registro de todas las acciones</p>
        </router-link>

        <router-link to="/membresias/asignar"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-emerald-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/boleto.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-emerald-600 transition-colors">Asignar Membresía</p>
          <p class="text-xs text-slate-400">Asigna una membresía a un cliente</p>
        </router-link>

        <router-link to="/clientes/registrar"
          class="bg-white rounded-2xl border-2 border-slate-200 hover:border-emerald-400 hover:shadow-md transition-all duration-200 p-5 flex flex-col gap-2 group">
          <img src="/icons/usuario.svg" class="w-8 h-8 icon-slate" alt="" />
          <p class="font-bold text-slate-800 text-sm group-hover:text-emerald-600 transition-colors">Registrar Cliente</p>
          <p class="text-xs text-slate-400">Añade un nuevo cliente al sistema</p>
        </router-link>
      </div>

    </main>
    <Footer />
  </div>
</template>

<style scoped>
.icon-slate { filter: brightness(0) saturate(0) opacity(0.55); }
html.dark .icon-slate { filter: brightness(0) invert(1) opacity(0.8); }
</style>
