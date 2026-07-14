import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminApi } from '@/services/api'
import type { AdminStats } from '@/services/api'

export const useAdminStore = defineStore('admin', () => {
  const stats = ref<AdminStats | null>(null)
  const loading = ref(false)

  async function fetchStats() {
    loading.value = true
    try {
      stats.value = await adminApi.getStats()
    } catch (e) {
      console.error('Error cargando stats:', e)
    } finally {
      loading.value = false
    }
  }

  return { stats, loading, fetchStats }
})
