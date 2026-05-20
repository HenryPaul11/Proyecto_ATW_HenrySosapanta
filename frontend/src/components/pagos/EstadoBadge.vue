<template>
  <span
    class="inline-block px-3.5 py-1 rounded-full text-xs font-bold tracking-wide capitalize"
    :class="styles"
  >
    {{ estado }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  estado: { type: String, required: true },
})

const styles = computed(() => {
  switch (props.estado?.toLowerCase()) {
    case 'activa':      return 'bg-emerald-100 text-emerald-700'
    case 'vencida':     return 'bg-red-100 text-red-700'
    case 'suspendida':  return 'bg-slate-200 text-slate-600'
    default:            return 'bg-slate-100 text-slate-500'
  }
})
</script>
