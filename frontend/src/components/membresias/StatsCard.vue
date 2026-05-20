<template>
  <div
    class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 border-t-4 transition-all duration-300 hover:-translate-y-1 hover:shadow-md"
    :class="borderColor"
  >
    <p class="text-slate-400 text-xs font-semibold uppercase tracking-widest mb-3">{{ title }}</p>
    <p class="font-black text-4xl tracking-tight leading-none" :class="valueColor">{{ value }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ title: String, value: Number, color: String })

const borderColor = computed(() => ({
  'border-t-emerald-500': props.color === 'green',
  'border-t-red-500':     props.color === 'red',
  'border-t-amber-500':   props.color === 'orange',
  'border-t-blue-500':    props.color === 'blue',
}))

const valueColor = computed(() => ({
  'text-emerald-500': props.color === 'green',
  'text-red-500':     props.color === 'red',
  'text-amber-500':   props.color === 'orange',
  'text-blue-600':    props.color === 'blue',
}))
</script>
