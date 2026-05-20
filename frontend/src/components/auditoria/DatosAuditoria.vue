<template>
  <div
    v-if="campos.length"
    class="max-w-[280px] bg-slate-50 border-2 border-slate-200 rounded-xl p-3 text-xs leading-relaxed"
  >
    <div
      v-for="(campo, i) in campos"
      :key="i"
      class="pb-2 mb-2 border-b border-dashed border-slate-300 last:border-0 last:mb-0 last:pb-0"
    >
      <span class="font-bold text-blue-800 uppercase tracking-wide text-[11px]">
        {{ campo.key }}:
      </span>
      <span class="text-slate-600 ml-1">{{ campo.value }}</span>
    </div>
  </div>

  <em v-else class="text-slate-400 text-xs">Sin datos</em>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  datos: { type: String, default: '' },
})

/**
 * Parsea el string de datos (JSON o "Clave: valor, Clave: valor")
 * y devuelve un array de { key, value }.
 */
const campos = computed(() => {
  const raw = props.datos?.trim()
  if (!raw) return []

  // Intentar JSON
  if (raw.startsWith('{')) {
    try {
      const obj = JSON.parse(raw)
      return Object.entries(obj)
        .filter(([, v]) => v !== null && v !== '')
        .map(([k, v]) => ({ key: k, value: String(v) }))
    } catch {
      // no es JSON válido, continuar
    }
  }

  // Formato "Clave: valor, Clave: valor"
  if (raw.includes(':')) {
    return raw.split(', ').flatMap(item => {
      const idx = item.indexOf(':')
      if (idx === -1) return [{ key: item.trim(), value: '' }]
      return [{
        key:   item.slice(0, idx).trim(),
        value: item.slice(idx + 1).trim(),
      }]
    })
  }

  // Texto plano
  return [{ key: 'Info', value: raw }]
})
</script>
