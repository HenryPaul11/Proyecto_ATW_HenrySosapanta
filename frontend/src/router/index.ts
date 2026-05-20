import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    { path: '/dashboard', name: 'Dashboard', component: () => import('@/views/AdminDashboard.vue'), meta: { requiresAuth: true } },
    { path: '/membresias', name: 'Membresias', component: () => import('@/views/Membresias.vue'), meta: { requiresAuth: true } },
    { path: '/membresias/asignar', name: 'AsignarMembresia', component: () => import('@/views/AsignarMembresia.vue'), meta: { requiresAuth: true } },
    { path: '/auditorias', name: 'Auditorias', component: () => import('@/views/Auditorias.vue'), meta: { requiresAuth: true } },
    { path: '/pagos', name: 'Pagos', component: () => import('@/views/Pagos.vue'), meta: { requiresAuth: true } },
    { path: '/pagos/registrar', name: 'RegistrarPago', component: () => import('@/views/RegistrarPago.vue'), meta: { requiresAuth: true } },
    { path: '/:pathMatch(.*)*', redirect: '/login' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn()) {
    return '/login'
  }
})

export default router
