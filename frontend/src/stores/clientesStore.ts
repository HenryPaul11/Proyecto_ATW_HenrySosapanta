import { defineStore } from 'pinia'
import { ref } from 'vue'
import { clientesApi } from '@/services/api'
import type { Cliente, TipoMembresia, MembresiaCliente } from '@/services/api'

export const useClientesStore = defineStore('clientes', () => {
  const clientes = ref<Cliente[]>([])
  const tiposMembresia = ref<TipoMembresia[]>([])
  const membresiasPorCliente = ref<Record<number, MembresiaCliente[]>>({})
  const loading = ref(false)

  async function fetchClientes(force = false) {
    if (clientes.value.length > 0 && !force) return
    loading.value = true
    clientes.value = await clientesApi.getAll()
    loading.value = false
  }

  async function fetchTiposMembresia() {
    tiposMembresia.value = await clientesApi.getTiposMembresia()
  }

  async function fetchMembresiasPorCliente(clienteId: number) {
    loading.value = true
    const data = await clientesApi.getMembresiasPorCliente(clienteId)
    membresiasPorCliente.value[clienteId] = data
    loading.value = false
  }

  async function buscarPorCedula(cedula: string): Promise<Cliente | null> {
    loading.value = true
    const result = await clientesApi.getByCedula(cedula)
    loading.value = false
    return result
  }

  return {
    clientes,
    tiposMembresia,
    membresiasPorCliente,
    loading,
    fetchClientes,
    fetchTiposMembresia,
    fetchMembresiasPorCliente,
    buscarPorCedula,
  }
})
