import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref('')
  const rol = ref('')

  function login(user) {
    usuario.value = user.usuario
    rol.value = user.rol
  }

  function logout() {
    usuario.value = ''
    rol.value = ''
  }

  const isLoggedIn = () => !!usuario.value

  return { usuario, rol, login, logout, isLoggedIn }
})
