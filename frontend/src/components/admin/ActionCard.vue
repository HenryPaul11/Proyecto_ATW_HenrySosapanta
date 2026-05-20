<template>
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 flex flex-col gap-4 transition-all duration-300 hover:-translate-y-1 hover:shadow-md hover:border-blue-200">
    <h3 class="text-slate-800 font-bold text-base leading-snug">{{ title }}</h3>
    <p class="text-slate-400 text-sm leading-relaxed flex-1">{{ description }}</p>
    <router-link
      :to="to"
      class="inline-flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm px-4 py-2.5 rounded-xl transition-all duration-200 hover:shadow-md"
    >
      {{ label }}
      <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
      </svg>
    </router-link>
  </div>
</template>

<script setup>
defineProps({
  title:       String,
  label:       String,
  to:          String,
  description: { type: String, default: '' },
  iconColor:   { type: String, default: 'blue' },
})
</script>
