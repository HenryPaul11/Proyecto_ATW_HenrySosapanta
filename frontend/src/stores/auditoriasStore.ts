import { defineStore } from 'pinia'
import { ref } from 'vue'
import { auditoriasApi } from '@/services/api'
import type { Auditoria } from '@/services/api'

export const useAuditoriasStore = defineStore('auditorias', () => {
  const auditorias = ref<Auditoria[]>([])
  const loading = ref(false)

  async function fetchAuditorias() {
    loading.value = true
    auditorias.value = await auditoriasApi.getAll()
    loading.value = false
  }

  return { auditorias, loading, fetchAuditorias }
})
