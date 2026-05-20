<template>
  <span
    class="inline-block px-3.5 py-1 rounded-full text-xs font-bold tracking-wide"
    :class="styles"
  >
    {{ icono }} {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  metodo: { type: String, required: true },
})

const map = {
  efectivo:      { label: 'Efectivo',      icono: '💵', cls: 'bg-emerald-100 text-emerald-700' },
  tarjeta:       { label: 'Tarjeta',       icono: '💳', cls: 'bg-blue-100 text-blue-700'     },
  transferencia: { label: 'Transferencia', icono: '🏦', cls: 'bg-amber-100 text-amber-700'   },
}

const entry = computed(() => map[props.metodo?.toLowerCase()] ?? { label: props.metodo, icono: '💰', cls: 'bg-slate-100 text-slate-600' })

const styles = computed(() => entry.value.cls)
const label  = computed(() => entry.value.label)
const icono  = computed(() => entry.value.icono)
</script>
