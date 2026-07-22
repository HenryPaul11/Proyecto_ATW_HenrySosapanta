<script setup lang="ts">
import { computed } from 'vue'
import ChatIA from '@/components/shared/ChatIA.vue'
import { useAuthStore } from '@/stores/authStore'

const auth = useAuthStore()
const showChat = computed(() => auth.isLoggedIn())
</script>

<template>
  <div id="app">
    <router-view />
    <ChatIA v-if="showChat" />
  </div>
</template>

<style>
/* ─── Sin scroll ─────────────────────────────────────────── */
html, body {
  overflow-x: hidden;
  overflow-y: auto;
}

/* ─── Transición global ─────────────────────────────────── */
*, *::before, *::after {
  transition-property: background-color, border-color, color;
  transition-duration: 0.2s;
  transition-timing-function: ease;
}

/* ─── Modo CLARO (default) ──────────────────────────────── */
html { color-scheme: light; }

body {
  background-color: #f1f5f9;
  color: #1e293b;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  -webkit-font-smoothing: antialiased;
  min-height: 100vh;
}

/* ─── Modo OSCURO ───────────────────────────────────────── */
html.dark { color-scheme: dark; }

html.dark body {
  background-color: #0f172a;
  color: #e2e8f0;
}

/* Fondos */
html.dark .bg-white          { background-color: #1e293b !important; }
html.dark .bg-white\/95      { background-color: rgba(30,41,59,0.97) !important; }
html.dark .bg-slate-50       { background-color: #0f172a !important; }
html.dark .bg-slate-50\/60   { background-color: rgba(15,23,42,0.6) !important; }
html.dark .bg-slate-100      { background-color: #1e293b !important; }
html.dark .bg-slate-200      { background-color: #334155 !important; }

/* Gradientes de fondo de página */
html.dark .bg-gradient-to-br.from-blue-50    { background: linear-gradient(135deg,#0f172a 0%,#1e293b 100%) !important; }
html.dark .bg-gradient-to-br.from-emerald-50 { background: linear-gradient(135deg,#0f172a 0%,#1e293b 100%) !important; }

/* Textos */
html.dark .text-slate-800 { color: #f1f5f9 !important; }
html.dark .text-slate-700 { color: #e2e8f0 !important; }
html.dark .text-slate-600 { color: #cbd5e1 !important; }
html.dark .text-slate-500 { color: #94a3b8 !important; }
html.dark .text-slate-400 { color: #64748b !important; }

/* Bordes */
html.dark .border-slate-100 { border-color: #334155 !important; }
html.dark .border-slate-200 { border-color: #334155 !important; }
html.dark .divide-slate-100 > * + * { border-color: #334155 !important; }
html.dark .divide-slate-200 > * + * { border-color: #334155 !important; }

/* Sombras */
html.dark .shadow-sm  { box-shadow: 0 1px 4px rgba(0,0,0,0.5) !important; }
html.dark .shadow-md  { box-shadow: 0 4px 12px rgba(0,0,0,0.5) !important; }
html.dark .shadow-xl  { box-shadow: 0 12px 32px rgba(0,0,0,0.6) !important; }

/* Inputs y selects */
html.dark input:not([type='submit']):not([type='button']),
html.dark select,
html.dark textarea {
  background-color: #1e293b !important;
  color: #e2e8f0 !important;
  border-color: #334155 !important;
}
html.dark input::placeholder { color: #64748b !important; }

/* Filas zebra en tablas */
html.dark .hover\:bg-blue-50:hover    { background-color: #1e3a5f !important; }
html.dark .hover\:bg-emerald-50:hover { background-color: #064e3b20 !important; }
html.dark .hover\:bg-slate-50:hover   { background-color: #1e293b !important; }

/* Navbar sticky */
html.dark header.sticky { background-color: rgba(15,23,42,0.97) !important; }
html.dark header .border-b { border-color: #334155 !important; }

/* Cards con hover de borde */
html.dark .hover\:border-blue-400:hover    { border-color: #3b82f6 !important; }
html.dark .hover\:border-emerald-400:hover { border-color: #34d399 !important; }

/* Badges — mantienen su color, solo ajustamos fondo para contraste */
html.dark .bg-emerald-50  { background-color: #064e3b !important; }
html.dark .bg-blue-50     { background-color: #1e3a5f !important; }
html.dark .bg-amber-50    { background-color: #451a03 !important; }
html.dark .bg-red-50      { background-color: #450a0a !important; }

html.dark .text-blue-700  { color: #93c5fd !important; }
html.dark .text-blue-800  { color: #bfdbfe !important; }
html.dark .text-emerald-700 { color: #6ee7b7 !important; }
html.dark .text-emerald-800 { color: #a7f3d0 !important; }
html.dark .text-amber-800 { color: #fcd34d !important; }
html.dark .text-red-800   { color: #fca5a5 !important; }

/* Animación global */
@keyframes fadeIn {
  from { opacity:0; transform:translateY(16px); }
  to   { opacity:1; transform:translateY(0);    }
}
.fade-in { animation: fadeIn 0.4s ease-out both; }
</style>
