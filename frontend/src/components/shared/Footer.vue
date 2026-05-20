<template>
  <footer class="mt-auto border-t border-slate-200 bg-white">
    <div class="max-w-[1400px] mx-auto px-6 py-5 flex flex-col sm:flex-row items-center justify-between gap-2">
      <div class="flex items-center gap-2">
        <div class="w-6 h-6 rounded-lg bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center">
          <svg class="w-3.5 h-3.5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 9h12M6 15h12"/>
          </svg>
        </div>
        <span class="font-black text-sm text-slate-700">Power<span class="text-blue-600">Fit</span></span>
      </div>
      <p class="text-slate-400 text-xs text-center">
        © {{ year }} Sistema de Gimnasio · Lun–Vie 5:00–22:00 · Sáb–Dom 7:00–20:00
      </p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>
