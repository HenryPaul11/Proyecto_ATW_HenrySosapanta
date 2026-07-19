import { defineStore } from 'pinia'
import { ref } from 'vue'
import { httpClient } from '@/services/api'
import type { Equipo } from '@/services/api'

export const IMAGEN_EQUIPO_DEFAULT =
  'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80'

function normalizarEquipo(raw: Partial<Equipo> & Record<string, unknown>): Equipo {
  const url = String(raw.imagenUrl ?? raw.imagen ?? '').trim()
  const imagen = url || IMAGEN_EQUIPO_DEFAULT

  let cat = String(raw.categoria ?? '')
  if (typeof raw.categoria === 'object' && raw.categoria !== null) {
    cat = String((raw.categoria as Record<string, unknown>).nombre ?? '')
  }

  let est = String(raw.estado ?? 'disponible')
  const estMap: Record<string, string> = {
    OPERATIVO: 'disponible',
    MANTENIMIENTO: 'mantenimiento',
    DADO_DE_BAJA: 'fuera_de_servicio',
  }
  est = estMap[est] ?? est.toLowerCase()

  return {
    id: Number(raw.id),
    nombre: String(raw.nombre ?? ''),
    categoria: cat,
    descripcion: String(raw.descripcion ?? ''),
    estado: est,
    imagen,
    imagenUrl: imagen,
    marca: raw.marca != null ? String(raw.marca) : undefined,
    modelo: raw.modelo != null ? String(raw.modelo) : undefined,
    valorAdquisicion: raw.valorAdquisicion != null ? Number(raw.valorAdquisicion) : undefined,
    fechaAdquisicion: raw.fechaAdquisicion != null ? String(raw.fechaAdquisicion) : undefined,
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

  async function registrarEquipo(equipo: Omit<Equipo, 'id'> & { sucursalId?: number; marca?: string; modelo?: string; valorAdquisicion?: number; fechaAdquisicion?: string }) {
    loading.value = true
    try {
      const imagenUrl = (equipo.imagenUrl || equipo.imagen || IMAGEN_EQUIPO_DEFAULT).trim()
      const payload: Record<string, unknown> = {
        nombre: equipo.nombre,
        categoria: equipo.categoria,
        descripcion: equipo.descripcion,
        estado: equipo.estado,
        imagenUrl,
        sucursalId: equipo.sucursalId,
      }
      if (equipo.marca) payload.marca = equipo.marca
      if (equipo.modelo) payload.modelo = equipo.modelo
      if (equipo.valorAdquisicion != null) payload.valorAdquisicion = equipo.valorAdquisicion
      if (equipo.fechaAdquisicion) payload.fechaAdquisicion = equipo.fechaAdquisicion
      const res = await httpClient.post('/equipos', payload)
      const nuevo = normalizarEquipo(res.data)
      equipos.value.unshift(nuevo)
      return nuevo
    } catch (err: unknown) {
      const apiErr = err as { error?: string; message?: string }
      const msg = apiErr?.error || apiErr?.message || 'No se pudo registrar el equipo.'
      throw new Error(msg)
    } finally {
      loading.value = false
    }
  }

  return { equipos, loading, fetchEquipos, registrarEquipo }
})
