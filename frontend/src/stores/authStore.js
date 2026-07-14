import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref(sessionStorage.getItem('auth_usuario') || '')
  const rol     = ref(sessionStorage.getItem('auth_rol') || '')
  const nombre  = ref(sessionStorage.getItem('auth_nombre') || '')
  const correo  = ref(sessionStorage.getItem('auth_correo') || '')
  const userId  = ref(sessionStorage.getItem('auth_id') || '')

  async function login(nombreUsuario, password) {
    const match = await authApi.login(nombreUsuario, password)
    if (match) {
      usuario.value = match.usuario
      rol.value     = match.rol?.toLowerCase()
      nombre.value  = match.nombre
      correo.value  = match.correo
      userId.value  = String(match.id ?? '')
      sessionStorage.setItem('auth_usuario', match.usuario)
      sessionStorage.setItem('auth_rol',     match.rol?.toLowerCase())
      sessionStorage.setItem('auth_nombre',  match.nombre)
      sessionStorage.setItem('auth_correo',  match.correo)
      sessionStorage.setItem('auth_id',      String(match.id ?? ''))
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
    sessionStorage.removeItem('auth_usuario')
    sessionStorage.removeItem('auth_rol')
    sessionStorage.removeItem('auth_nombre')
    sessionStorage.removeItem('auth_correo')
    sessionStorage.removeItem('auth_id')
    sessionStorage.removeItem('auth_token')
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, nombre, correo, userId, login, logout, isLoggedIn }
})
