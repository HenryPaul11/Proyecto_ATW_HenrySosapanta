import { defineStore } from 'pinia'
import { ref } from 'vue'
import { clienteApi } from '@/services/api'
import type { ClienteSession, MembresiaActiva, HistorialPago } from '@/services/api'

export const useClienteStore = defineStore('cliente', () => {
  const cliente = ref<ClienteSession | null>(null)
  const membresia = ref<MembresiaActiva | null>(null)
  const historialPagos = ref<HistorialPago[]>([])
  const loading = ref(false)

  async function fetchByUsuario(usuario: string) {
    loading.value = true
    cliente.value = await clienteApi.getByUsuario(usuario)
    loading.value = false
  }

  async function fetchMembresia(clienteId: number) {
    membresia.value = await clienteApi.getMembresia(clienteId)
  }

  async function fetchHistorialPagos(clienteId: number) {
    historialPagos.value = await clienteApi.getHistorialPagos(clienteId)
  }

  async function fetchAll(usuario: string) {
    loading.value = true
    const c = await clienteApi.getByUsuario(usuario)
    cliente.value = c
    if (c) {
      const [m, h] = await Promise.all([
        clienteApi.getMembresia(c.id),
        clienteApi.getHistorialPagos(c.id),
      ])
      membresia.value = m
      historialPagos.value = h
    }
    loading.value = false
  }

  return {
    cliente,
    membresia,
    historialPagos,
    loading,
    fetchByUsuario,
    fetchMembresia,
    fetchHistorialPagos,
    fetchAll,
  }
})
