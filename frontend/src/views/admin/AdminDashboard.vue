<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useAdminStore } from '@/stores/adminStore'
import Navbar     from '@/components/shared/Navbar.vue'
import Footer     from '@/components/shared/Footer.vue'
import StatCard   from '@/components/admin/StatCard.vue'
import ActionCard from '@/components/admin/ActionCard.vue'

const router = useRouter()
const auth   = useAuthStore()
const admin  = useAdminStore()

function logout() { auth.logout(); router.push('/login') }

onMounted(() => admin.fetchStats())
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-5xl mx-auto px-4 sm:px-8 py-10 fade-in">

      <!-- Header -->
      <div class="mb-8">
        <p class="text-blue-600 font-semibold text-sm mb-1">Panel de Administración</p>
        <h1 class="text-2xl sm:text-3xl font-black text-slate-800 tracking-tight">
          Bienvenido, {{ auth.usuario }} 👋
        </h1>
        <p class="text-slate-400 text-sm mt-1">Resumen general del sistema</p>
      </div>

      <!-- Stats -->
      <div v-if="admin.loading" class="grid grid-cols-1 sm:grid-cols-3 gap-5 mb-10">
        <div v-for="i in 3" :key="i" class="h-24 bg-slate-200 animate-pulse rounded-2xl" />
      </div>
      <div v-else-if="admin.stats" class="grid grid-cols-1 sm:grid-cols-3 gap-5 mb-10">
        <StatCard title="Clientes Registrados" :value="admin.stats.clientes"            color="blue"  />
        <StatCard title="Membresías Activas"   :value="admin.stats.membresias"          color="sky"   />
        <StatCard title="Ingresos Mensuales"   :value="admin.stats.ingresos.toFixed(2)" color="green" prefix="$" />
      </div>

      <!-- Acciones -->
      <p class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-4">Acciones rápidas</p>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <ActionCard
          title="Gestionar Membresías"
          description="Consulta, asigna y renueva membresías de clientes."
          label="Ver Membresías"
          to="/membresias"
        />
        <ActionCard
          title="Ver Pagos Realizados"
          description="Historial completo de transacciones y cobros."
          label="Ver Pagos"
          to="/pagos"
        />
        <ActionCard
          title="Auditorías del Sistema"
          description="Registro de todas las acciones realizadas."
          label="Ver Auditorías"
          to="/auditorias"
        />
      </div>

    </main>

    <Footer />
  </div>
</template>
