import { defineStore } from 'pinia'
import { ref } from 'vue'
import { entrenadorApi } from '@/services/api'
import type { EntrenadorSession, ClienteAsignado, Sesion, EntrenadorResumen } from '@/services/api'

export const useEntrenadorStore = defineStore('entrenador', () => {
  const entrenador = ref<EntrenadorSession | null>(null)
  const clientes = ref<ClienteAsignado[]>([])
  const sesiones = ref<Sesion[]>([])
  const todosEntrenadores = ref<EntrenadorResumen[]>([])
  const loading = ref(false)

  async function fetchByUsuario(usuario: string) {
    loading.value = true
    entrenador.value = await entrenadorApi.getByUsuario(usuario)
    loading.value = false
  }

  async function fetchClientesAsignados(entrenadorId: number) {
    clientes.value = await entrenadorApi.getClientesAsignados(entrenadorId)
  }

  async function fetchSesiones(entrenadorId: number) {
    sesiones.value = await entrenadorApi.getSesiones(entrenadorId)
  }

  async function fetchAll(usuario: string) {
    loading.value = true
    const e = await entrenadorApi.getByUsuario(usuario)
    entrenador.value = e
    if (e) {
      const [c, s] = await Promise.all([
        entrenadorApi.getClientesAsignados(e.id),
        entrenadorApi.getSesiones(e.id),
      ])
      clientes.value = c
      sesiones.value = s
    }
    loading.value = false
  }

  async function fetchTodos() {
    todosEntrenadores.value = await entrenadorApi.getTodos()
  }

  return {
    entrenador,
    clientes,
    sesiones,
    todosEntrenadores,
    loading,
    fetchByUsuario,
    fetchClientesAsignados,
    fetchSesiones,
    fetchAll,
    fetchTodos,
  }
})
