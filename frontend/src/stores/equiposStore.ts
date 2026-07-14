import { defineStore } from 'pinia'
import { ref } from 'vue'
import { httpClient } from '@/services/api'
import type { Equipo } from '@/services/api'

export const IMAGEN_EQUIPO_DEFAULT =
  'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80'

function normalizarEquipo(raw: Partial<Equipo> & Record<string, unknown>): Equipo {
  const url = String(raw.imagenUrl ?? raw.imagen ?? '').trim()
  const imagen = url || IMAGEN_EQUIPO_DEFAULT
  return {
    id: Number(raw.id),
    nombre: String(raw.nombre ?? ''),
    categoria: String(raw.categoria ?? ''),
    descripcion: String(raw.descripcion ?? ''),
    estado: String(raw.estado ?? 'disponible'),
    imagen,
    imagenUrl: imagen,
  }
}

export const useEquiposStore = defineStore('equipos', () => {
  const equipos = ref<Equipo[]>([])
  const loading = ref(false)

  async function fetchEquipos(force = false) {
    if (equipos.value.length > 0 && !force) return
    loading.value = true
    try {
      const res = await httpClient.get('/equipos')
      const lista = Array.isArray(res.data) ? res.data : []
      equipos.value = lista.map((e: Partial<Equipo>) => normalizarEquipo(e))
    } catch { equipos.value = [] }
    finally { loading.value = false }
  }

  async function registrarEquipo(equipo: Omit<Equipo, 'id'>) {
    loading.value = true
    try {
      const imagenUrl = (equipo.imagenUrl || equipo.imagen || IMAGEN_EQUIPO_DEFAULT).trim()
      const payload = {
        nombre: equipo.nombre,
        categoria: equipo.categoria,
        descripcion: equipo.descripcion,
        estado: equipo.estado,
        imagenUrl,
      }
      const res = await httpClient.post('/equipos', payload)
      const nuevo = normalizarEquipo(res.data)
      equipos.value.unshift(nuevo)
      return nuevo
    } finally {
      loading.value = false
    }
  }

  return { equipos, loading, fetchEquipos, registrarEquipo }
})
