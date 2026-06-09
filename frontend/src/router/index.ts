import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ─── Público ───────────────────────────────────────────────────────────
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },

    // ─── Admin ─────────────────────────────────────────────────────────────
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/admin/AdminDashboard.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/clientes',
      name: 'Clientes',
      component: () => import('@/views/admin/Clientes.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/clientes/registrar',
      name: 'RegistrarCliente',
      component: () => import('@/views/admin/RegistrarCliente.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/clientes/:id/editar',
      name: 'EditarCliente',
      component: () => import('@/views/admin/EditarCliente.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/membresias',
      name: 'Membresias',
      component: () => import('@/views/admin/Membresias.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/membresias/asignar',
      name: 'AsignarMembresia',
      component: () => import('@/views/admin/AsignarMembresia.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/membresias/:clienteId/renovar',
      name: 'RenovarMembresia',
      component: () => import('@/views/admin/RenovarMembresia.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/auditorias',
      name: 'Auditorias',
      component: () => import('@/views/admin/Auditorias.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/pagos',
      name: 'Pagos',
      component: () => import('@/views/admin/Pagos.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/pagos/registrar',
      name: 'RegistrarPago',
      component: () => import('@/views/admin/RegistrarPago.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },
    {
      path: '/equipos',
      name: 'Equipos',
      component: () => import('@/views/admin/Equipos.vue'),
      meta: { requiresAuth: true, rol: 'admin' },
    },

    // ─── Entrenador ────────────────────────────────────────────────────────
    {
      path: '/entrenador/dashboard',
      name: 'TrainerDashboard',
      component: () => import('@/views/trainer/TrainerDashboard.vue'),
      meta: { requiresAuth: true, rol: 'entrenador' },
    },
    {
      path: '/entrenador/clientes',
      name: 'MisClientes',
      component: () => import('@/views/trainer/MisClientes.vue'),
      meta: { requiresAuth: true, rol: 'entrenador' },
    },
    {
      path: '/entrenador/sesiones',
      name: 'Sesiones',
      component: () => import('@/views/trainer/Sesiones.vue'),
      meta: { requiresAuth: true, rol: 'entrenador' },
    },
    {
      path: '/entrenador/perfil',
      name: 'MiPerfilEntrenador',
      component: () => import('@/views/trainer/MiPerfilEntrenador.vue'),
      meta: { requiresAuth: true, rol: 'entrenador' },
    },

    // ─── Cliente ───────────────────────────────────────────────────────────
    {
      path: '/cliente/dashboard',
      name: 'ClientDashboard',
      component: () => import('@/views/client/ClientDashboard.vue'),
      meta: { requiresAuth: true, rol: 'cliente' },
    },
    {
      path: '/cliente/membresia',
      name: 'MiMembresia',
      component: () => import('@/views/client/MiMembresia.vue'),
      meta: { requiresAuth: true, rol: 'cliente' },
    },
    {
      path: '/cliente/pagos',
      name: 'MisPagos',
      component: () => import('@/views/client/MisPagos.vue'),
      meta: { requiresAuth: true, rol: 'cliente' },
    },
    {
      path: '/cliente/perfil',
      name: 'MiPerfil',
      component: () => import('@/views/client/MiPerfil.vue'),
      meta: { requiresAuth: true, rol: 'cliente' },
    },

    { path: '/:pathMatch(.*)*', redirect: '/login' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  // Ruta protegida sin sesión → manda al login guardando la URL de destino
  if (to.meta.requiresAuth && !auth.isLoggedIn()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // Ruta con rol requerido → redirige al dashboard del rol correcto
  if (to.meta.requiresAuth && to.meta.rol && auth.rol !== to.meta.rol) {
    if (auth.rol === 'admin')       return '/dashboard'
    if (auth.rol === 'entrenador')  return '/entrenador/dashboard'
    return '/cliente/dashboard'
  }
})

export default router
