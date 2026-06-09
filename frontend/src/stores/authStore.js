import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref('')
  const rol     = ref('')
  const nombre  = ref('')
  const correo  = ref('')

  async function login(nombreUsuario, password) {
    const match = await authApi.login(nombreUsuario, password)
    if (match) {
      usuario.value = match.usuario
      rol.value     = match.rol
      nombre.value  = match.nombre
      correo.value  = match.correo
      return match
    }
    return null
  }

  function logout() {
    usuario.value = ''
    rol.value     = ''
    nombre.value  = ''
    correo.value  = ''
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, nombre, correo, login, logout, isLoggedIn }
})
