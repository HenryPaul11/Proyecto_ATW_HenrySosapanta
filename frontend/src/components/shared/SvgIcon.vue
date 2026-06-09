<template>
  <img
    :src="`/icons/${name}.svg`"
    :alt="name"
    :style="{ width: size, height: size }"
    class="svg-icon object-contain inline-block"
  />
</template>

<script setup>
defineProps({
  name: { type: String, required: true },
  size: { type: String, default: '1.25rem' },
})
</script>

<style scoped>
/* SVGs negros → se adaptan al contexto de color */
.svg-icon {
  filter: brightness(0) saturate(100%) opacity(0.65);
  transition: filter 0.2s ease;
}
html.dark .svg-icon {
  filter: brightness(0) invert(1) opacity(0.8);
}
/* Cuando está dentro de un elemento con clase de color específica */
.text-emerald-600 .svg-icon,
.group-hover\:text-emerald-600:hover .svg-icon { filter: brightness(0) saturate(100%) invert(40%) sepia(80%) saturate(400%) hue-rotate(120deg) opacity(0.9); }
</style>
