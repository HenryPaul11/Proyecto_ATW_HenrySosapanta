import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/services/api'
import type { User } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref('')
  const rol = ref('')

  async function login(u: string, p: string): Promise<User | null> {
    const match = await authApi.login(u, p)
    if (match) {
      usuario.value = match.usuario
      rol.value = match.rol
    }
    return match
  }

  function logout() {
    usuario.value = ''
    rol.value = ''
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, login, logout, isLoggedIn }
})
