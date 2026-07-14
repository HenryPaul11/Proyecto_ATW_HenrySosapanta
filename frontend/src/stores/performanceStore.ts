import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePerformanceStore = defineStore('performance', () => {
  const responseTimes = ref<Record<string, number[]>>({
    clientes: [],
    pagos: [],
    sesiones: [],
    membresias: [],
    usuarios: [],
    auditorias: [],
  })

  function addResponseTime(key: string, ms: number) {
    if (!responseTimes.value[key]) {
      responseTimes.value[key] = []
    }
    responseTimes.value[key].push(ms)
    // Keep last 10 response times
    if (responseTimes.value[key].length > 10) {
      responseTimes.value[key].shift()
    }
  }

  function getAverageTime(key: string): number {
    const list = responseTimes.value[key] || []
    if (list.length === 0) return 0
    const sum = list.reduce((a, b) => a + b, 0)
    return parseFloat((sum / list.length).toFixed(2))
  }

  return { responseTimes, addResponseTime, getAverageTime }
})
