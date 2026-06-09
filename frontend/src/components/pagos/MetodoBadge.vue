<template>
  <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold tracking-wide" :class="entry.cls">
    <img :src="entry.img" :alt="entry.label" class="w-4 h-4 object-contain" />
    {{ entry.label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  metodo: { type: String, required: true },
})

const map = {
  efectivo:      { label: 'Efectivo',      img: '/icons/efectivo.svg',      cls: 'bg-emerald-100 text-emerald-700' },
  tarjeta:       { label: 'Tarjeta',       img: '/icons/tarjeta.svg',       cls: 'bg-blue-100 text-blue-700'      },
  transferencia: { label: 'Transferencia', img: '/icons/transferencia.svg', cls: 'bg-amber-100 text-amber-700'    },
}

const entry = computed(() =>
  map[props.metodo?.toLowerCase()] ?? { label: props.metodo, img: '/icons/pago_otro.svg', cls: 'bg-slate-100 text-slate-600' }
)
</script>
