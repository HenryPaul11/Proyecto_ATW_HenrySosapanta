<template>
  <div>
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <div class="container fade-in">
      <h1>Bienvenido, Administrador</h1>

      <!-- Estadísticas -->
      <div class="dashboard-stats">
        <StatCard title="Clientes Registrados" :value="stats.clientes" color="blue" />
        <StatCard title="Membresías Activas"   :value="stats.membresias" color="sky" />
        <StatCard title="Ingresos Mensuales"   :value="stats.ingresos.toFixed(2)" prefix="$" color="green" />
      </div>

      <!-- Acciones -->
      <div class="actions">
        <ActionCard title="Gestionar Membresías"  label="Ver Membresías"   to="/membresias" />
        <ActionCard title="Ver Pagos Realizados"  label="Ver Pagos"        to="/pagos" />
        <ActionCard title="Ver Auditorías"        label="Ver Auditorías"   to="/auditorias" />
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import Navbar from '../components/shared/Navbar.vue'
import Footer from '../components/shared/Footer.vue'
import StatCard from '../components/admin/StatCard.vue'
import ActionCard from '../components/admin/ActionCard.vue'

const router = useRouter()
const auth = useAuthStore()

// Mock — después se reemplaza con llamadas al backend
const stats = {
  clientes: 24,
  membresias: 18,
  ingresos: 1340.00,
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.container {
  max-width: 1300px;
  margin: 50px auto;
  padding: 0 50px;
}

.container h1 {
  color: var(--text-main);
  font-size: 36px;
  margin-bottom: 40px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 50px;
}

.actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

@media (max-width: 768px) {
  .container { padding: 0 25px; margin: 30px auto; }
  .container h1 { font-size: 28px; }
  .dashboard-stats, .actions { grid-template-columns: 1fr; gap: 20px; }
}
</style>
