<script setup lang="ts">
import { ref } from 'vue'
import { iaApi } from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

const auth = useAuthStore()
const abierto = ref(false)
const mensaje = ref('')
const mensajes = ref<{ rol: 'user' | 'ia'; texto: string }[]>([])
const cargando = ref(false)

async function enviar() {
  if (!mensaje.value.trim() || cargando.value) return
  const texto = mensaje.value.trim()
  mensajes.value.push({ rol: 'user', texto })
  mensaje.value = ''
  cargando.value = true
  try {
    const userRol = auth.rol || 'admin'
    const keywords = ['cliente', 'membresia', 'membresías', 'pago', 'ingreso', 'vencida', 'vencimiento', 'plan', 'resumen', 'estadistica', 'reporte', 'cuantos', 'cuántos', 'total', 'dashboard', 'ejercicio', 'entren', 'rutina', 'muscul', 'pecho', 'espalda', 'pierna', 'nutri', 'dieta', 'proteina', 'fuerza', 'cardio']
    const esPreguntaDatos = keywords.some(k => texto.toLowerCase().includes(k))
    let respuesta: string
    if (esPreguntaDatos) {
      respuesta = await iaApi.chatDatos(texto, userRol)
    } else {
      const rolPrompt = userRol === 'cliente'
        ? 'Eres PowerFit AI, asistente de bienestar del gimnasio PowerFit para clientes. Responde en español sobre ejercicios, nutrición y membresías de forma amigable y motivadora.'
        : userRol === 'entrenador'
          ? 'Eres PowerFit AI, asistente técnico del gimnasio PowerFit para entrenadores. Responde en español con terminología fitness correcta sobre entrenamiento, ejercicios y programación.'
          : 'Eres PowerFit AI, asistente administrativo del gimnasio PowerFit. Responde en español de forma concisa y profesional sobre gestión del negocio.'
      respuesta = await iaApi.chat(texto, rolPrompt)
    }
    mensajes.value.push({ rol: 'ia', texto: respuesta })
  } catch {
    mensajes.value.push({ rol: 'ia', texto: 'Error al conectar con la IA.' })
  }
  cargando.value = false
}
</script>

<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Boton flotante -->
    <button v-if="!abierto" @click="abierto = true"
      class="w-14 h-14 bg-gradient-to-br from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200 flex items-center justify-center">
      <svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
      </svg>
    </button>

    <!-- Panel de chat -->
    <div v-if="abierto" class="w-80 h-96 bg-white rounded-2xl shadow-2xl border border-slate-200 flex flex-col overflow-hidden">
      <!-- Header -->
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-3 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <span class="text-white font-bold text-sm">PowerFit AI</span>
        </div>
        <button @click="abierto = false" class="text-white/70 hover:text-white transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Mensajes -->
      <div class="flex-1 overflow-y-auto p-3 space-y-3">
        <div v-if="mensajes.length === 0" class="text-center text-slate-400 text-xs mt-8">
          <p class="font-semibold">Hola! Soy PowerFit AI</p>
          <p class="mt-1">Preguntame lo que quieras</p>
        </div>
        <div v-for="(m, i) in mensajes" :key="i"
          class="flex" :class="m.rol === 'user' ? 'justify-end' : 'justify-start'">
          <div class="max-w-[80%] px-3 py-2 rounded-2xl text-xs leading-relaxed"
            :class="m.rol === 'user'
              ? 'bg-blue-500 text-white rounded-br-md'
              : 'bg-slate-100 text-slate-700 rounded-bl-md'">
            {{ m.texto }}
          </div>
        </div>
        <div v-if="cargando" class="flex justify-start">
          <div class="bg-slate-100 px-3 py-2 rounded-2xl rounded-bl-md">
            <div class="flex gap-1">
              <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
              <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
              <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="p-3 border-t border-slate-100">
        <div class="flex gap-2">
          <input v-model="mensaje" @keyup.enter="enviar" placeholder="Escribe tu pregunta..."
            class="flex-1 px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-blue-400 transition-all" />
          <button @click="enviar" :disabled="!mensaje.trim() || cargando"
            class="w-9 h-9 bg-blue-500 hover:bg-blue-600 disabled:opacity-40 text-white rounded-xl flex items-center justify-center transition-all shrink-0">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
