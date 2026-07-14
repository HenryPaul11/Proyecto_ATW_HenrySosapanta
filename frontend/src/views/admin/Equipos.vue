<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useEquiposStore, IMAGEN_EQUIPO_DEFAULT } from '@/stores/equiposStore'
import Navbar from '@/components/admin/AdminNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useEquiposStore()

function logout() { auth.logout(); router.push('/login') }

// ── Filtros y búsqueda ──────────────────────────────────────────
const busqueda  = ref('')
const categoria = ref('')
const mostrarForm = ref(false)
const loading   = ref(false)
const message   = ref('')
const errorMsg  = ref('')

// ── Formulario nuevo equipo ────────────────────────────────────
const form = ref({ nombre: '', categoria: 'Cardio', descripcion: '', imagen: '', estado: 'disponible' })
const errores = ref<Record<string, string>>({})

const categorias = ['Cardio', 'Fuerza', 'Funcional', 'Flexibilidad']

// ── Filtro + búsqueda ──────────────────────────────────────────
const equiposFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  return store.equipos.filter(e => {
    const matchQ = !q || e.nombre.toLowerCase().includes(q) || e.descripcion.toLowerCase().includes(q)
    const matchC = !categoria.value || e.categoria === categoria.value
    return matchQ && matchC
  })
})

// ── Validaciones ───────────────────────────────────────────────
function validar(): boolean {
  errores.value = {}
  const { nombre, descripcion, imagen } = form.value

  if (!nombre.trim())
    errores.value.nombre = 'El nombre es obligatorio.'
  else if (nombre.trim().length < 3)
    errores.value.nombre = 'Mínimo 3 caracteres.'

  if (!descripcion.trim())
    errores.value.descripcion = 'La descripción es obligatoria.'
  else if (descripcion.trim().length < 10)
    errores.value.descripcion = 'Mínimo 10 caracteres.'

  if (imagen.trim() && !/^https?:\/\/.+/.test(imagen.trim()))
    errores.value.imagen = 'Debe ser una URL válida (https://…).'

  return Object.keys(errores.value).length === 0
}

// ── Registrar equipo ───────────────────────────────────────────
async function registrar() {
  if (!validar()) return
  loading.value = true
  errorMsg.value = ''

  try {
    await store.registrarEquipo({
      nombre:      form.value.nombre.trim(),
      categoria:   form.value.categoria,
      descripcion: form.value.descripcion.trim(),
      estado:      form.value.estado,
      imagen:      form.value.imagen.trim() || IMAGEN_EQUIPO_DEFAULT,
    })

    form.value    = { nombre: '', categoria: 'Cardio', descripcion: '', imagen: '', estado: 'disponible' }
    errores.value = {}
    mostrarForm.value = false
    message.value = '✅ Equipo registrado correctamente.'
    setTimeout(() => message.value = '', 3000)
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : 'Error al registrar el equipo.'
    errorMsg.value = msg.includes('character varying(255)')
      ? 'La URL de la imagen es demasiado larga. Reinicia el backend para aplicar la actualización de base de datos, o usa una URL más corta (por ejemplo, de Unsplash).'
      : msg
  } finally {
    loading.value = false
  }
}

function imagenEquipo(e: { imagenUrl?: string; imagen?: string }) {
  return (e.imagenUrl || e.imagen || IMAGEN_EQUIPO_DEFAULT).trim()
}

function onImgError(ev: Event) {
  const img = ev.target as HTMLImageElement
  if (img.src !== IMAGEN_EQUIPO_DEFAULT) img.src = IMAGEN_EQUIPO_DEFAULT
}

onMounted(() => store.fetchEquipos(true))
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 fade-in">

      <!-- Cabecera -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 tracking-tight">Equipos del Gimnasio</h1>
          <p class="text-slate-400 text-sm mt-1">Datos cargados desde la API del backend</p>
        </div>
        <button
          @click="mostrarForm = !mostrarForm"
          class="inline-flex items-center gap-2 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm px-5 py-3 rounded-xl shadow-sm transition-all hover:-translate-y-0.5 cursor-pointer"
        >
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          {{ mostrarForm ? 'Cancelar' : 'Agregar Equipo' }}
        </button>
      </div>

      <!-- Feedback -->
      <Transition name="fade">
        <div v-if="message" class="bg-emerald-50 text-emerald-800 border border-emerald-200 rounded-xl px-4 py-3 mb-5 text-sm font-medium">
          {{ message }}
        </div>
      </Transition>
      <Transition name="fade">
        <div v-if="errorMsg" class="bg-red-50 text-red-800 border border-red-200 rounded-xl px-4 py-3 mb-5 text-sm font-medium">
          {{ errorMsg }}
        </div>
      </Transition>

      <!-- Formulario -->
      <Transition name="slide-down">
        <div v-if="mostrarForm" class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-8">
          <h2 class="text-base font-bold text-slate-700 mb-5">Registrar Nuevo Equipo</h2>
          <form @submit.prevent="registrar" class="grid grid-cols-1 sm:grid-cols-2 gap-4">

            <!-- Nombre -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">
                Nombre <span class="text-red-500">*</span>
              </label>
              <input v-model="form.nombre" type="text" placeholder="Ej: Cinta de Correr"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.nombre ? 'border-red-400 focus:border-red-500' : 'border-slate-200 focus:border-blue-500'" />
              <p v-if="errores.nombre" class="text-red-500 text-xs mt-1">{{ errores.nombre }}</p>
            </div>

            <!-- Categoría -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Categoría <span class="text-red-500">*</span></label>
              <select v-model="form.categoria"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white">
                <option v-for="c in categorias" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>

            <!-- Descripción -->
            <div class="sm:col-span-2">
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">
                Descripción <span class="text-red-500">*</span>
              </label>
              <textarea v-model="form.descripcion" rows="2" placeholder="Describe el equipo (mínimo 10 caracteres)…"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white resize-none"
                :class="errores.descripcion ? 'border-red-400 focus:border-red-500' : 'border-slate-200 focus:border-blue-500'" />
              <p v-if="errores.descripcion" class="text-red-500 text-xs mt-1">{{ errores.descripcion }}</p>
            </div>

            <!-- URL imagen -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">URL de Imagen</label>
              <input v-model="form.imagen" type="text" placeholder="https://…"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 focus:bg-white"
                :class="errores.imagen ? 'border-red-400 focus:border-red-500' : 'border-slate-200 focus:border-blue-500'" />
              <p v-if="errores.imagen" class="text-red-500 text-xs mt-1">{{ errores.imagen }}</p>
              <p class="text-slate-400 text-xs mt-1">Opcional. Si no se ingresa se usará una imagen por defecto.</p>
            </div>

            <!-- Estado -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1.5">Estado</label>
              <select v-model="form.estado"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white">
                <option value="disponible">Disponible</option>
                <option value="mantenimiento">En mantenimiento</option>
                <option value="fuera_de_servicio">Fuera de servicio</option>
              </select>
            </div>

            <!-- Botón -->
            <div class="sm:col-span-2 flex justify-end gap-3 mt-1">
              <button type="button" @click="mostrarForm = false"
                class="px-5 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm rounded-xl transition-all cursor-pointer">
                Cancelar
              </button>
              <button type="submit" :disabled="loading"
                class="px-5 py-2.5 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm rounded-xl transition-all shadow-sm hover:-translate-y-0.5 disabled:opacity-60 cursor-pointer">
                {{ loading ? 'Registrando…' : 'Registrar Equipo' }}
              </button>
            </div>

          </form>
        </div>
      </Transition>

      <!-- Filtros -->
      <div class="flex flex-col sm:flex-row gap-3 mb-6">
        <div class="relative flex-1">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input v-model="busqueda" type="text" placeholder="Buscar equipo…"
            class="w-full pl-10 pr-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all" />
        </div>
        <select v-model="categoria"
          class="px-4 py-2.5 border-2 border-slate-200 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white sm:w-44">
          <option value="">Todas las categorías</option>
          <option v-for="c in categorias" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <p class="text-sm text-slate-400 font-medium mb-4">
        Mostrando <strong class="text-slate-600">{{ equiposFiltrados.length }}</strong> de {{ store.equipos.length }} equipos
      </p>

      <!-- Grid de equipos -->
      <div v-if="equiposFiltrados.length === 0" class="text-center py-20 text-slate-400">
        <p class="font-semibold">No se encontraron equipos.</p>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5 mb-10">
        <div
          v-for="e in equiposFiltrados"
          :key="e.id"
          class="bg-white rounded-2xl border-2 border-slate-200 overflow-hidden hover:border-blue-400 hover:shadow-md transition-all duration-200 group"
        >
          <!-- Imagen -->
          <div class="h-44 overflow-hidden bg-slate-100">
            <img
              :src="imagenEquipo(e)"
              :alt="e.nombre"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              loading="lazy"
              @error="onImgError"
            />
          </div>

          <!-- Info -->
          <div class="p-4">
            <div class="flex items-start justify-between gap-2 mb-2">
              <h3 class="font-bold text-slate-800 text-base leading-tight">{{ e.nombre }}</h3>
              <span
                class="shrink-0 text-xs font-bold px-2.5 py-1 rounded-full"
                :class="{
                  'bg-emerald-100 text-emerald-700': e.estado === 'disponible',
                  'bg-amber-100 text-amber-700':     e.estado === 'mantenimiento',
                  'bg-red-100 text-red-700':          e.estado === 'fuera_de_servicio',
                }"
              >
                {{ e.estado === 'disponible' ? 'Disponible' : e.estado === 'mantenimiento' ? 'Mantenimiento' : 'Fuera de servicio' }}
              </span>
            </div>
            <span class="inline-block text-xs font-semibold bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full mb-2">
              {{ e.categoria }}
            </span>
            <p class="text-slate-500 text-sm leading-relaxed">{{ e.descripcion }}</p>
          </div>
        </div>
      </div>

    </main>
    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active       { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to            { opacity: 0; }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; }
.slide-down-enter-from,   .slide-down-leave-to     { opacity: 0; transform: translateY(-10px); }
</style>
