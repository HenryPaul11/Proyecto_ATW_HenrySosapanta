import { defineStore } from 'pinia'
import { ref } from 'vue'
import { membresiasApi } from '@/services/api'
import type { Membresia, ClienteSinMembresia } from '@/services/api'

export const useMembresiasStore = defineStore('membresias', () => {
  const membresias = ref<Membresia[]>([])
  const clientesSinMembresia = ref<ClienteSinMembresia[]>([])
  const loading = ref(false)

  async function fetchMembresias() {
    loading.value = true
    membresias.value = await membresiasApi.getAll()
    loading.value = false
  }

  async function fetchClientesSinMembresia() {
    clientesSinMembresia.value = await membresiasApi.getClientesSinMembresia()
  }

  async function fetchAll() {
    loading.value = true
    const [m, s] = await Promise.all([
      membresiasApi.getAll(),
      membresiasApi.getClientesSinMembresia(),
    ])
    membresias.value = m
    clientesSinMembresia.value = s
    loading.value = false
  }

  return {
    membresias,
    clientesSinMembresia,
    loading,
    fetchMembresias,
    fetchClientesSinMembresia,
    fetchAll,
  }
})
