<template>
  <span
    class="inline-block px-2.5 py-1 rounded-lg text-xs font-bold uppercase tracking-wide"
    :class="styles"
  >
    {{ accion }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  accion: { type: String, required: true },
})

const styles = computed(() => {
  const a = props.accion?.toUpperCase()
  if (a === 'INSERT' || a === 'INSERTAR' || a === 'CREATE')
    return 'bg-emerald-100 text-emerald-700'
  if (a === 'UPDATE' || a === 'ACTUALIZAR' || a === 'EDITAR')
    return 'bg-blue-100 text-blue-700'
  if (a === 'DELETE' || a === 'ELIMINAR' || a === 'BORRAR')
    return 'bg-red-100 text-red-700'
  return 'bg-slate-100 text-slate-600'
})
</script>
