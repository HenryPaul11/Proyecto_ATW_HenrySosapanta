<template>
  <div class="bg-white border-2 border-blue-500 rounded-2xl p-6 mb-6">
    <h3 class="text-blue-600 font-bold text-lg mb-4">✅ Cliente Encontrado</h3>
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
  { label: 'Nombre',   value: `${props.cliente.nombre} ${props.cliente.apellido}` },
  { label: 'Cédula',   value: props.cliente.cedula },
  { label: 'Teléfono', value: props.cliente.telefono },
  { label: 'Email',    value: props.cliente.email },
])
</script>
