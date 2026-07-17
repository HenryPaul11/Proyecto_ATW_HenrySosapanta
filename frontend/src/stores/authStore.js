import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { httpClient } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario        = ref(sessionStorage.getItem('auth_usuario')        || '')
  const rol            = ref(sessionStorage.getItem('auth_rol')            || '')
  const nombre         = ref(sessionStorage.getItem('auth_nombre')         || '')
  const correo         = ref(sessionStorage.getItem('auth_correo')         || '')
  const userId         = ref(sessionStorage.getItem('auth_id')             || '')
  const sucursalId     = ref(sessionStorage.getItem('auth_sucursal_id')
                             ? Number(sessionStorage.getItem('auth_sucursal_id')) : null)
  const sucursalNombre = ref(sessionStorage.getItem('auth_sucursal_nombre') || '')
  // Sucursal activa seleccionada por admin matriz al registrar (null = sin filtro)
  const sucursalActivaId = ref(sessionStorage.getItem('auth_sucursal_activa')
                               ? Number(sessionStorage.getItem('auth_sucursal_activa')) : null)

  // true = admin de sucursal específica, false = admin matriz
  const esSucursal = computed(() => !!sucursalId.value)
  // ID efectivo para filtros y registros
  const sucursalEfectiva = computed(() => sucursalId.value ?? sucursalActivaId.value ?? null)

  function setSucursalActiva(id) {
    sucursalActivaId.value = id ?? null
    sessionStorage.setItem('auth_sucursal_activa', id ?? '')
  }

  async function login(emailParam, password) {
    try {
      const res = await httpClient.post('/auth/login', { email: emailParam, password })
      const m   = res?.data ?? res
      if (!m) return null

      const rolRaw   = (m.rol || '').toLowerCase()
      const rolFront = rolRaw.includes('admin') ? 'admin'
                     : rolRaw === 'entrenador'   ? 'entrenador'
                     : 'recepcionista'

      usuario.value        = m.email           || m.nombreCompleto || ''
      rol.value            = rolFront
      nombre.value         = m.nombreCompleto  || ''
      correo.value         = m.email           || ''
      userId.value         = String(m.id ?? '')
      sucursalId.value     = m.sucursalId      ?? null
      sucursalNombre.value = m.sucursalNombre  ?? ''

      sessionStorage.setItem('auth_usuario',         usuario.value)
      sessionStorage.setItem('auth_rol',             rolFront)
      sessionStorage.setItem('auth_nombre',          nombre.value)
      sessionStorage.setItem('auth_correo',          correo.value)
      sessionStorage.setItem('auth_id',              userId.value)
      sessionStorage.setItem('auth_sucursal_id',     m.sucursalId     ?? '')
      sessionStorage.setItem('auth_sucursal_nombre', m.sucursalNombre ?? '')
      if (m.token) sessionStorage.setItem('auth_token', m.token)

      return { ...m, rol: rolFront }
    } catch {
      return null
    }
  }

  function logout() {
    usuario.value = rol.value = nombre.value = correo.value = userId.value = ''
    sucursalId.value = null
    sucursalNombre.value = ''
    sucursalActivaId.value = null
    sessionStorage.clear()
  }

  const isLoggedIn = () => !!usuario.value

  return {
    usuario, rol, nombre, correo, userId,
    sucursalId, sucursalNombre, sucursalActivaId, sucursalEfectiva,
    esSucursal, setSucursalActiva,
    login, logout, isLoggedIn
  }
})
