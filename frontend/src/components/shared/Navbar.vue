<template>
  <header class="navbar">
    <div class="logo">
      <h2> Gimnasio PowerFit</h2>
    </div>
    <div class="menu">
      <div class="user-pill">
        <span> {{ usuario }}</span>
      </div>
      <router-link to="/dashboard">Dashboard</router-link>
      <router-link to="/membresias">Membresías</router-link>
      <router-link to="/clientes">Clientes</router-link>
      <router-link to="/pagos">Pagos</router-link>
      <router-link to="/auditorias">Auditorías</router-link>

      <!-- Toggle modo oscuro -->
      <button class="btn-theme" @click="theme.toggle()" :title="theme.dark ? 'Modo claro' : 'Modo oscuro'">
        {{ theme.dark ? '' : '' }}
      </button>

      <button class="btn-logout" @click="$emit('logout')">Cerrar sesión</button>
    </div>
  </header>
</template>

<script setup>
import { useThemeStore } from '@/stores/themeStore'
defineProps({ usuario: String })
defineEmits(['logout'])
const theme = useThemeStore()
</script>

<style scoped>
.navbar {
  background: var(--navbar-bg, white);
  padding: 16px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.1);
  border-bottom: 3px solid #3b82f6;
  transition: background 0.3s ease;
  flex-wrap: wrap;
  gap: 12px;
}

.logo h2 {
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.menu {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
}

.user-pill {
  background: #eff6ff;
  padding: 6px 14px;
  border-radius: 10px;
  margin-right: 6px;
}

html.dark .user-pill { background: #1e3a5f; }

.user-pill span {
  color: #1e40af;
  font-weight: 700;
  font-size: 13px;
}

html.dark .user-pill span { color: #93c5fd; }

.menu a {
  color: #64748b;
  text-decoration: none;
  font-weight: 600;
  font-size: 13px;
  padding: 8px 14px;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.menu a:hover,
.menu a.router-link-active {
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  color: white;
  transform: translateY(-1px);
}

.btn-theme {
  background: #f1f5f9;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
  line-height: 1;
}

html.dark .btn-theme { background: #334155; border-color: #475569; }
.btn-theme:hover { transform: translateY(-1px); }

.btn-logout {
  background: #ef4444;
  color: white;
  border: none;
  font-weight: 600;
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-logout:hover { background: #dc2626; transform: translateY(-1px); }

@media (max-width: 768px) {
  .navbar { padding: 16px 20px; }
  .menu { width: 100%; justify-content: center; }
  .menu a, .btn-logout, .btn-theme { font-size: 12px; padding: 7px 10px; }
}
</style>
