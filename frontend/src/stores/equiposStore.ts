import { defineStore } from 'pinia'
import { ref } from 'vue'
import { equiposApi } from '@/services/api'
import type { Equipo } from '@/services/api'

export const useEquiposStore = defineStore('equipos', () => {
  const equipos = ref<Equipo[]>([])
  const loading = ref(false)

  async function fetchEquipos(force = false) {
    if (equipos.value.length > 0 && !force) return
    loading.value = true
    equipos.value = await equiposApi.getAll()
    loading.value = false
  }

  async function registrarEquipo(equipo: Omit<Equipo, 'id'>) {
    loading.value = true
    // Simulamos un pequeño retraso de red
    await new Promise(r => setTimeout(r, 400))
    const nuevoId = equipos.value.length ? Math.max(...equipos.value.map(e => e.id)) + 1 : 1
    const nuevo: Equipo = {
      id: nuevoId,
      ...equipo
    }
    equipos.value.unshift(nuevo)
    loading.value = false
    return nuevo
  }

  return {
    equipos,
    loading,
    fetchEquipos,
    registrarEquipo
  }
})
