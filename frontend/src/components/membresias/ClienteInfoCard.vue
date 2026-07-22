<template>
  <div class="bg-white border-2 border-blue-500 rounded-2xl p-6 mb-6">
    <h3 class="text-blue-600 font-bold text-lg mb-4 flex items-center gap-2">
      <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
      Cliente Encontrado
    </h3>
    <div class="divide-y divide-slate-100">
      <div
        v-for="field in fields"
        :key="field.label"
        class="flex justify-between items-center py-3"
      >
        <span class="text-slate-500 font-semibold text-sm">{{ field.label }}</span>
        <span class="text-slate-800 font-medium text-sm">{{ field.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  cliente: { type: Object, required: true },
})

const fields = computed(() => [
  { label: 'Nombre',   value: props.cliente.nombreCompleto || `${props.cliente.nombre ?? ''} ${props.cliente.apellido ?? ''}`.trim() || 'Sin nombre' },
  { label: 'Cédula',   value: props.cliente.documentoIdentidad || props.cliente.cedula || 'Sin documento' },
  { label: 'Teléfono', value: props.cliente.telefono || 'Sin teléfono' },
  { label: 'Email',    value: props.cliente.email || 'Sin email' },
])
</script>
