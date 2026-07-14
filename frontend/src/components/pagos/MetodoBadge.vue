<template>
  <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold tracking-wide" :class="entry.cls">
    <!-- Efectivo -->
    <svg v-if="props.metodo?.toLowerCase() === 'efectivo'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <rect x="2" y="6" width="20" height="12" rx="2"/>
      <circle cx="12" cy="12" r="3"/>
      <path d="M6 12h.01M18 12h.01"/>
    </svg>
    <!-- Tarjeta -->
    <svg v-else-if="props.metodo?.toLowerCase() === 'tarjeta'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <rect x="1" y="4" width="22" height="16" rx="2"/>
      <line x1="1" y1="10" x2="23" y2="10"/>
    </svg>
    <!-- Transferencia -->
    <svg v-else-if="props.metodo?.toLowerCase() === 'transferencia'" class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="17 1 21 5 17 9"/>
      <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
      <polyline points="7 23 3 19 7 15"/>
      <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
    </svg>
    <!-- Otro -->
    <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <circle cx="12" cy="12" r="10"/>
      <line x1="12" y1="8" x2="12" y2="16"/>
      <line x1="8" y1="12" x2="16" y2="12"/>
    </svg>
    {{ entry.label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  metodo: { type: String, required: true },
})

const map = {
  efectivo:      { label: 'Efectivo',      cls: 'bg-emerald-100 text-emerald-700' },
  tarjeta:       { label: 'Tarjeta',       cls: 'bg-blue-100 text-blue-700'       },
  transferencia: { label: 'Transferencia', cls: 'bg-amber-100 text-amber-700'     },
}

const entry = computed(() =>
  map[props.metodo?.toLowerCase()] ?? { label: props.metodo, cls: 'bg-slate-100 text-slate-600' }
)
</script>
