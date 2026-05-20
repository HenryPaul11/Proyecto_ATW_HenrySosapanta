<template>
  <div
    class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 border-t-4 transition-all duration-300 hover:-translate-y-1 hover:shadow-md"
    :class="borderColor"
  >
    <p class="text-slate-400 text-xs font-semibold uppercase tracking-widest mb-3">{{ title }}</p>
    <p class="font-black text-4xl tracking-tight leading-none" :class="valueColor">
      {{ prefix }}{{ value }}
    </p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title:  String,
  value:  [String, Number],
  prefix: { type: String, default: '' },
  color:  { type: String, default: 'blue' },
})

const borderColor = computed(() => ({
  'border-t-blue-500':    props.color === 'blue',
  'border-t-sky-500':     props.color === 'sky',
  'border-t-emerald-500': props.color === 'green',
}))

const valueColor = computed(() => ({
  'text-blue-600':    props.color === 'blue',
  'text-sky-500':     props.color === 'sky',
  'text-emerald-500': props.color === 'green',
}))
</script>
