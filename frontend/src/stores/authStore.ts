import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/services/api'
import type { User } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref(sessionStorage.getItem('auth_usuario') || '')
  const nombreCompleto = ref(sessionStorage.getItem('auth_nombreCompleto') || '')
  const rol = ref(sessionStorage.getItem('auth_rol') || '')
  const sucursalId = ref<number | null>(JSON.parse(sessionStorage.getItem('auth_sucursalId') || 'null'))
  const sucursalNombre = ref(sessionStorage.getItem('auth_sucursalNombre') || '')

  // Propiedad computada: true si el usuario es admin de una sucursal (no matriz)
  const esSucursal = computed(() => rol.value === 'admin' && sucursalId.value !== null)

  async function login(u: string, p: string): Promise<User | null> {
    const match = await authApi.login(u, p)
    if (match) {
      usuario.value = match.usuario
      nombreCompleto.value = match.nombre || ''
      rol.value = match.rol
      sucursalId.value = match.sucursalId ?? null
      sucursalNombre.value = match.sucursalNombre ?? ''
      
      sessionStorage.setItem('auth_usuario', match.usuario)
      sessionStorage.setItem('auth_nombreCompleto', match.nombre || '')
      sessionStorage.setItem('auth_rol', match.rol)
      sessionStorage.setItem('auth_sucursalId', JSON.stringify(match.sucursalId ?? null))
      sessionStorage.setItem('auth_sucursalNombre', match.sucursalNombre ?? '')
      
      if (match.token) {
        sessionStorage.setItem('auth_token', match.token)
      }
    }
    return match
  }

  function logout() {
    usuario.value = ''
    nombreCompleto.value = ''
    rol.value = ''
    sucursalId.value = null
    sucursalNombre.value = ''
    sessionStorage.removeItem('auth_usuario')
    sessionStorage.removeItem('auth_nombreCompleto')
    sessionStorage.removeItem('auth_rol')
    sessionStorage.removeItem('auth_token')
    sessionStorage.removeItem('auth_sucursalId')
    sessionStorage.removeItem('auth_sucursalNombre')
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, nombreCompleto, rol, sucursalId, sucursalNombre, esSucursal, login, logout, isLoggedIn }
})

