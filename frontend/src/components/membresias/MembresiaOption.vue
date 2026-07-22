<template>
  <label
    class="flex items-start gap-4 bg-white border-2 rounded-xl p-4 cursor-pointer transition-all duration-200 hover:border-blue-500 hover:translate-x-1"
    :class="selected ? 'border-blue-500 bg-blue-50' : 'border-slate-200'"
  >
    <input
      type="radio"
      :name="name"
      :value="tipo.id"
      :checked="selected"
      @change="$emit('select', tipo.id)"
      class="mt-1 accent-blue-500"
      required
    />
    <div class="flex-1">
      <p class="font-bold text-slate-800 mb-1">{{ tipo.nombre }}</p>
      <p class="text-slate-500 text-sm leading-relaxed">
        {{ tipo.descripcion }}<br />
        <span class="inline-flex items-center gap-1 mt-1">
          <svg class="w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          Duracion: <strong class="text-slate-700">{{ tipo.duracion_dias }} dias</strong>
        </span><br />
        <span class="inline-flex items-center gap-1">
          <svg class="w-4 h-4 text-emerald-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
          Precio:
          <strong class="text-emerald-600 text-base">${{ Number(tipo.precio).toFixed(2) }}</strong>
        </span>
      </p>
    </div>
  </label>
</template>

<script setup>
defineProps({
  tipo:     { type: Object,  required: true },
  selected: { type: Boolean, default: false },
  name:     { type: String,  default: 'tipo_membresia_id' },
})
defineEmits(['select'])
</script>
