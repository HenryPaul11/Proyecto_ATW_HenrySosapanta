import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario     = ref(sessionStorage.getItem('auth_usuario')    || '')
  const rol         = ref(sessionStorage.getItem('auth_rol')        || '')
  const nombre      = ref(sessionStorage.getItem('auth_nombre')     || '')
  const correo      = ref(sessionStorage.getItem('auth_correo')     || '')
  const userId      = ref(sessionStorage.getItem('auth_id')         || '')
  const sucursalId  = ref(sessionStorage.getItem('auth_sucursal_id')
                          ? Number(sessionStorage.getItem('auth_sucursal_id'))
                          : null)
  const sucursalNombre = ref(sessionStorage.getItem('auth_sucursal_nombre') || '')

  // true = admin de una sucursal específica, false = admin matriz (ve todo)
  const esSucursal = computed(() => !!sucursalId.value)

  async function login(nombreUsuario, password) {
    const match = await authApi.login(nombreUsuario, password)
    if (match) {
      usuario.value      = match.usuario
      rol.value          = match.rol?.toLowerCase()
      nombre.value       = match.nombre
      correo.value       = match.correo
      userId.value       = String(match.id ?? '')
      sucursalId.value   = match.sucursalId ?? null
      sucursalNombre.value = match.sucursalNombre ?? ''

      sessionStorage.setItem('auth_usuario',         match.usuario)
      sessionStorage.setItem('auth_rol',             match.rol?.toLowerCase())
      sessionStorage.setItem('auth_nombre',          match.nombre)
      sessionStorage.setItem('auth_correo',          match.correo)
      sessionStorage.setItem('auth_id',              String(match.id ?? ''))
      sessionStorage.setItem('auth_sucursal_id',     match.sucursalId ?? '')
      sessionStorage.setItem('auth_sucursal_nombre', match.sucursalNombre ?? '')

      if (match.token) sessionStorage.setItem('auth_token', match.token)
      return match
    }
    return null
  }

  function logout() {
    usuario.value = ''
    rol.value     = ''
    nombre.value  = ''
    correo.value  = ''
    userId.value  = ''
    sucursalId.value   = null
    sucursalNombre.value = ''
    sessionStorage.clear()
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, nombre, correo, userId, sucursalId, sucursalNombre, esSucursal, login, logout, isLoggedIn }
})
