<template>
  <div
    class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 border-t-4 transition-all duration-300 hover:-translate-y-1 hover:shadow-md"
    :class="borderColor"
  >
    <p class="text-slate-400 text-xs font-semibold uppercase tracking-widest mb-3">{{ title }}</p>
    <div class="font-black tracking-tight leading-none mb-1" :class="[valueColor, large ? 'text-4xl' : 'text-xl']">
      <slot>{{ prefix }}{{ value }}</slot>
    </div>
    <p v-if="subtitle" class="text-slate-400 text-xs mt-2">{{ subtitle }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title:    { type: String,  required: true },
  value:    { type: [String, Number], default: '' },
  prefix:   { type: String,  default: '' },
  subtitle: { type: String,  default: '' },
  color:    { type: String,  default: 'blue' },
  large:    { type: Boolean, default: true },
})

const borderColor = computed(() => ({
  'border-t-emerald-500': props.color === 'green',
  'border-t-sky-500':     props.color === 'blue',
  'border-t-amber-500':   props.color === 'orange',
  'border-t-violet-500':  props.color === 'purple',
}))

const valueColor = computed(() => ({
  'text-emerald-500': props.color === 'green',
  'text-sky-500':     props.color === 'blue',
  'text-amber-500':   props.color === 'orange',
  'text-violet-600':  props.color === 'purple',
}))
</script>
