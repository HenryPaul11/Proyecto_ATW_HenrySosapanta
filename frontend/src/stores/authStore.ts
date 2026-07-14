import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/services/api'
import type { User } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref(sessionStorage.getItem('auth_usuario') || '')
  const rol = ref(sessionStorage.getItem('auth_rol') || '')

  async function login(u: string, p: string): Promise<User | null> {
    const match = await authApi.login(u, p)
    if (match) {
      usuario.value = match.usuario
      rol.value = match.rol
      sessionStorage.setItem('auth_usuario', match.usuario)
      sessionStorage.setItem('auth_rol', match.rol)
      if (match.token) {
        sessionStorage.setItem('auth_token', match.token)
      }
    }
    return match
  }

  function logout() {
    usuario.value = ''
    rol.value = ''
    sessionStorage.removeItem('auth_usuario')
    sessionStorage.removeItem('auth_rol')
    sessionStorage.removeItem('auth_token')
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, login, logout, isLoggedIn }
})

