<template>
  <div class="relative flex justify-between items-start mb-8">
    <!-- Línea conectora -->
    <div class="absolute top-4 sm:top-5 left-[16.66%] right-[16.66%] h-0.5 bg-slate-200 z-0" />

    <div
      v-for="(step, i) in steps"
      :key="i"
      class="relative z-10 flex flex-col items-center flex-1"
    >
      <!-- Círculo -->
      <div
        class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs sm:text-sm mb-1.5 transition-all duration-300 shrink-0"
        :class="stepClass(i)"
      >
        <svg v-if="i < current" class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
        </svg>
        <span v-else>{{ i + 1 }}</span>
      </div>
      <!-- Etiqueta — oculta en móvil muy pequeño -->
      <span
        class="text-[10px] sm:text-xs font-semibold text-center leading-tight block"
        :class="i <= current ? 'text-slate-700' : 'text-slate-400'"
      >
        {{ step }}
      </span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  steps:   { type: Array,  required: true },
  current: { type: Number, default: 0 },
})

function stepClass(i) {
  if (i < props.current)   return 'bg-blue-500 text-white shadow-sm'
  if (i === props.current) return 'bg-blue-500 text-white shadow-md ring-4 ring-blue-100'
  return 'bg-slate-200 text-slate-500'
}
</script>
