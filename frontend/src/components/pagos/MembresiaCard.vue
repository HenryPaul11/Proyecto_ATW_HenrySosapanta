<template>
  <div
    class="border-2 rounded-xl p-4 cursor-pointer transition-all duration-200 hover:-translate-x-0 hover:border-blue-500 hover:bg-blue-50"
    :class="selected
      ? 'border-blue-500 bg-blue-50 shadow-sm'
      : 'border-slate-200 bg-white'"
    @click="$emit('select', membresia.id, membresia.precio)"
  >
    <div class="flex items-start gap-3">
      <!-- Radio -->
      <input
        type="radio"
        :name="name"
        :value="membresia.id"
        :checked="selected"
        class="mt-1 accent-blue-500 shrink-0"
        required
        @click.stop
        @change="$emit('select', membresia.id, membresia.precio)"
      />
      <div class="flex-1">
        <p class="font-bold text-slate-800 mb-1">{{ membresia.tipo_membresia }}</p>
        <p class="text-slate-500 text-sm">
          Vigencia:
          <strong class="text-slate-700">
            {{ formatFecha(membresia.fecha_inicio) }} – {{ formatFecha(membresia.fecha_fin) }}
          </strong>
        </p>
        <p class="text-slate-500 text-sm mt-0.5">
          Precio:
          <strong class="text-emerald-600 text-lg">${{ Number(membresia.precio).toFixed(2) }}</strong>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  membresia: { type: Object,  required: true },
  selected:  { type: Boolean, default: false },
  name:      { type: String,  default: 'membresia_id' },
})
defineEmits(['select'])

function formatFecha(str) {
  if (!str) return '—'
  return new Date(str).toLocaleDateString('es-EC', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })
}
</script>
