<template>
  <div class="w-full flex flex-col gap-4">
    <!-- Controles superiores de tabla: Buscar + Tamaño de Página -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
      <div class="flex items-center gap-2">
        <label for="pageSize" class="text-xs font-semibold text-slate-500 uppercase">Mostrar</label>
        <select
          id="pageSize"
          v-model="pageSize"
          class="px-2 py-1.5 border border-slate-200 rounded-lg text-sm bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-100"
        >
          <option :value="10">10 registros</option>
          <option :value="20">20 registros</option>
          <option :value="50">50 registros</option>
        </select>
      </div>

      <div class="relative w-full sm:w-72">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Buscar..."
          class="w-full px-4 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
        />
        <svg class="absolute right-3.5 top-2.5 w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
      </div>
    </div>

    <!-- Indicador de velocidad de consulta (Opcional, muy senior) -->
    <div class="flex justify-between items-center px-1 text-xs text-slate-400 font-medium">
      <div>
        Mostrando de <span class="text-slate-700 font-semibold">{{ startIdx }}</span> a
        <span class="text-slate-700 font-semibold">{{ endIdx }}</span> de
        <span class="text-slate-700 font-semibold">{{ totalElements }}</span> registros
      </div>
      <div v-if="lastResponseTime !== null" class="flex items-center gap-1">
        <span class="inline-block w-2 h-2 rounded-full bg-emerald-500"></span>
        Servidor optimizado: respondido en <strong class="text-slate-600 font-bold">{{ lastResponseTime }}ms</strong>
      </div>
    </div>

    <!-- Contenedor de la Tabla -->
    <div class="w-full max-w-full overflow-hidden bg-white rounded-2xl shadow-sm border border-slate-200">
      <div class="overflow-x-auto">
        <table class="w-full border-collapse" style="min-width: 600px">
          <thead class="bg-gradient-to-r from-blue-500 to-blue-700 text-white">
            <tr>
              <th
                v-for="col in columns"
                :key="col"
                class="px-4 py-3 text-left font-bold text-xs uppercase tracking-wider whitespace-nowrap"
              >
                {{ col }}
              </th>
            </tr>
          </thead>
          <tbody>
            <!-- Estado: Cargando -->
            <tr v-if="loading">
              <td :colspan="columns.length" class="text-center py-16 text-slate-400 text-sm">
                <div class="flex flex-col items-center justify-center gap-3">
                  <div class="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                  <span class="animate-pulse font-semibold">Consultando base de datos masiva (400k+ registros)...</span>
                </div>
              </td>
            </tr>

            <!-- Estado: Error -->
            <tr v-else-if="error">
              <td :colspan="columns.length" class="text-center py-12 px-4">
                <div class="max-w-md mx-auto flex flex-col items-center gap-2">
                  <svg class="w-10 h-10 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                  </svg>
                  <p class="text-slate-700 font-bold">Error al cargar datos</p>
                  <p class="text-slate-400 text-xs">{{ error }}</p>
                  <button
                    @click="fetchData"
                    class="mt-3 px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold rounded-lg shadow-sm transition-all"
                  >
                    Reintentar consulta
                  </button>
                </div>
              </td>
            </tr>

            <!-- Estado: Vacío -->
            <tr v-else-if="items.length === 0">
              <td :colspan="columns.length" class="text-center py-16 text-slate-400">
                <p class="font-semibold text-slate-600">No se encontraron registros</p>
                <p class="text-xs mt-1">Prueba cambiando los filtros o la búsqueda</p>
              </td>
            </tr>

            <!-- Estado: Datos listos (se inyectan mediante slot) -->
            <slot v-else :items="items" />
          </tbody>
        </table>
      </div>
    </div>

    <!-- Controles de Paginación -->
    <div
      v-if="totalPages > 1"
      class="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-sm"
    >
      <button
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
        class="w-full sm:w-auto px-4 py-2 border border-slate-200 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:hover:bg-white cursor-pointer"
      >
        Anterior
      </button>

      <div class="flex items-center gap-1.5 flex-wrap justify-center">
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="changePage(page)"
          class="w-8 h-8 rounded-lg text-xs font-bold transition-all cursor-pointer"
          :class="currentPage === page
            ? 'bg-blue-500 text-white shadow-sm'
            : 'text-slate-600 hover:bg-slate-100'"
        >
          {{ page + 1 }}
        </button>
      </div>

      <button
        :disabled="currentPage === totalPages - 1"
        @click="changePage(currentPage + 1)"
        class="w-full sm:w-auto px-4 py-2 border border-slate-200 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:hover:bg-white cursor-pointer"
      >
        Siguiente
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { httpClient } from '@/services/api'
import { usePerformanceStore } from '@/stores/performanceStore'

const props = defineProps<{
  endpoint: string
  columns: string[]
  filtros?: Record<string, unknown>
  // Clave del performanceStore para guardar tiempos
  endpointKey?: 'clientes' | 'pagos' | 'sesiones' | 'membresias' | 'usuarios' | 'auditorias'
}>()

const emit = defineEmits(['loaded'])

const performanceStore = usePerformanceStore()

// Estados reactivos internos
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const items = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const currentPage = ref(0) // 0-indexed por Spring Boot
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)
const lastResponseTime = ref<number | null>(null)

// Campo de búsqueda con debounce
const searchQuery = ref('')
let debounceTimer: ReturnType<typeof setTimeout> | null = null

// Índices mostrados
const startIdx = computed(() => (totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1))
const endIdx = computed(() => Math.min(startIdx.value + items.value.length - 1, totalElements.value))

// Páginas visibles en el paginador
const visiblePages = computed(() => {
  const pages: number[] = []
  const maxVisible = 5
  let start = Math.max(0, currentPage.value - Math.floor(maxVisible / 2))
  const end = Math.min(totalPages.value - 1, start + maxVisible - 1)

  if (end - start + 1 < maxVisible) {
    start = Math.max(0, end - maxVisible + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// Función principal de carga de datos
async function fetchData() {
  loading.value = true
  error.value = null

  const startTime = performance.now()

  try {
    // Unir la búsqueda debounced y otros filtros
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      search: searchQuery.value || undefined,
      query: searchQuery.value || undefined, // compatibilidad
      ...props.filtros,
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const response = await httpClient.get<any>(props.endpoint, { params })

    // Validar formato del backend { success, data: { content, totalElements } }
    if (response && response.data) {
      const pageData = response.data
      if (pageData.content && Array.isArray(pageData.content)) {
        items.value = pageData.content
        totalElements.value = pageData.totalElements ?? pageData.content.length
        totalPages.value = pageData.totalPages ?? Math.ceil(totalElements.value / pageSize.value)
      } else if (Array.isArray(pageData)) {
        // Fallback si retorna un listado directo en lugar de Page
        items.value = pageData
        totalElements.value = pageData.length
        totalPages.value = 1
      } else {
        items.value = []
        totalElements.value = 0
        totalPages.value = 0
      }
    } else {
      items.value = []
      totalElements.value = 0
      totalPages.value = 0
    }

    const duration = Math.round(performance.now() - startTime)
    lastResponseTime.value = duration

    // Registrar en el performanceStore si corresponde
    if (props.endpointKey) {
      performanceStore.addResponseTime(props.endpointKey, duration)
    }

    emit('loaded', { items: items.value, responseTime: duration })
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    console.error('Error fetching paginated data:', err)
    error.value = err.error || err.message || 'Error desconocido del servidor'
  } finally {
    loading.value = false
  }
}

// Cambiar de página
function changePage(page: number) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchData()
  }
}

// Recargar cuando cambie el tamaño de página o filtros externos
watch(pageSize, () => {
  currentPage.value = 0
  fetchData()
})

watch(
  () => props.filtros,
  () => {
    currentPage.value = 0
    fetchData()
  },
  { deep: true }
)

// Recargar cuando cambie la búsqueda con debounce
watch(searchQuery, () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    currentPage.value = 0
    fetchData()
  }, 400) // 400ms de rebote
})

onMounted(() => {
  fetchData()
})

// Exponer método de recarga
defineExpose({
  reload: fetchData
})
</script>
