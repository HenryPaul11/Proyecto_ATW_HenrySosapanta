import { defineStore } from 'pinia'
import { ref } from 'vue'
import { pagosApi } from '@/services/api'
import type { Pago } from '@/services/api'

export const usePagosStore = defineStore('pagos', () => {
  const pagos = ref<Pago[]>([])
  const loading = ref(false)

  async function fetchPagos() {
    loading.value = true
    pagos.value = await pagosApi.getAll()
    loading.value = false
  }

  async function registrarPago(pago: Omit<Pago, 'id'>): Promise<Pago> {
    loading.value = true
    const nuevo = await pagosApi.registrar(pago)
    pagos.value.unshift(nuevo)
    loading.value = false
    return nuevo
  }

  return { pagos, loading, fetchPagos, registrarPago }
})
