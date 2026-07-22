<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useEquiposStore, IMAGEN_EQUIPO_DEFAULT } from '@/stores/equiposStore'
import { httpClient, equiposTransferApi } from '@/services/api'
import AppNavbar from '@/components/shared/AppNavbar.vue'
import Footer from '@/components/shared/Footer.vue'

const router = useRouter()
const auth   = useAuthStore()
const store  = useEquiposStore()

function logout() { auth.logout(); router.push('/login') }

const navLinks = computed(() => {
  const links = [
    { to: '/dashboard',    label: 'Inicio',       icon: 'home'       },
    { to: '/clientes',     label: 'Clientes',      icon: 'clientes'   },
    { to: '/entrenadores', label: 'Entrenadores',  icon: 'clientes'   },
    { to: '/membresias',   label: 'Membresías',    icon: 'membresias' },
    { to: '/equipos',      label: 'Equipos',       icon: 'equipos'    },
    { to: '/pagos',        label: 'Pagos',         icon: 'pagos'      },
    { to: '/auditorias',   label: 'Auditorías',    icon: 'auditorias' },
  ]
  if (!auth.esSucursal) {
    links.splice(6, 0, { to: '/sucursales', label: 'Sucursales', icon: 'home' })
  }
  return links
})

// ── Filtros y búsqueda ──────────────────────────────────────────
const busqueda  = ref('')
const categoria = ref('')
const mostrarForm = ref(false)
const loading   = ref(false)
const message   = ref('')
const messageType = ref('')
const errorMsg  = ref('')

// ── Formulario nuevo equipo ────────────────────────────────────
const form = ref({ nombre: '', categoria: 'Cardio', descripcion: '', imagen: '', estado: 'disponible', marca: '', modelo: '', valorAdquisicion: '', fechaAdquisicion: '' })
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
      imagenUrl:   form.value.imagen.trim() || IMAGEN_EQUIPO_DEFAULT,
      marca:       form.value.marca.trim() || undefined,
      modelo:      form.value.modelo.trim() || undefined,
      valorAdquisicion: form.value.valorAdquisicion ? Number(form.value.valorAdquisicion) : undefined,
      fechaAdquisicion: form.value.fechaAdquisicion || undefined,
      sucursalId:  auth.sucursalId ?? auth.sucursalActivaId ?? 1,
    })

    form.value    = { nombre: '', categoria: 'Cardio', descripcion: '', imagen: '', estado: 'disponible', marca: '', modelo: '', valorAdquisicion: '', fechaAdquisicion: '' }
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

// ── Editar equipo ──────────────────────────────────────────────
const equipoEditar  = ref<any>(null)
const formEditar    = ref({ nombre: '', categoria: 'Cardio', descripcion: '', imagen: '', estado: 'disponible' })
const loadingEditar = ref(false)

function abrirEditar(e: any) {
  equipoEditar.value = e
  formEditar.value   = { nombre: e.nombre, categoria: e.categoria, descripcion: e.descripcion, imagen: e.imagenUrl ?? e.imagen ?? '', estado: e.estado }
}

async function guardarEdicion() {
  loadingEditar.value = true
  try {
    const res = await httpClient.put(`/equipos/${equipoEditar.value.id}`, {
      nombre: formEditar.value.nombre, categoria: formEditar.value.categoria,
      descripcion: formEditar.value.descripcion, estado: formEditar.value.estado,
      imagenUrl: formEditar.value.imagen, imagen: formEditar.value.imagen,
    })
    const idx = store.equipos.findIndex((e: any) => e.id === equipoEditar.value.id)
    if (idx !== -1) store.equipos[idx] = normalizarEquipo(res.data)
    equipoEditar.value = null
    message.value = '✓ Equipo actualizado.'
    setTimeout(() => message.value = '', 3000)
  } catch (err: any) { message.value = err?.error || 'No se pudo actualizar.' }
  finally { loadingEditar.value = false }
}

// ── Eliminar equipo ────────────────────────────────────────────
const equipoEliminar = ref<any>(null)

async function confirmarEliminar() {
  if (!equipoEliminar.value) return
  try {
    await httpClient.delete(`/equipos/${equipoEliminar.value.id}`)
    store.equipos = store.equipos.filter((e: any) => e.id !== equipoEliminar.value.id)
    message.value = '✓ Equipo eliminado.'
    setTimeout(() => message.value = '', 3000)
  } catch (err: any) { message.value = err?.error || 'No se pudo eliminar.' }
  finally { equipoEliminar.value = null }
}

onMounted(() => store.fetchEquipos(true))

// ── Transferir equipo ─────────────────────────────────────────
const equipoTransferir = ref<any>(null)
const sucursales       = ref<any[]>([])
const sucursalDestino  = ref<number | null>(null)
const loadingTransfer  = ref(false)

async function cargarSucursales() {
  try {
    const res = await httpClient.get('/sucursales')
    sucursales.value = (Array.isArray(res.data) ? res.data : []).filter((s: any) => s.activo !== false)
  } catch { sucursales.value = [] }
}

function abrirTransferir(e: any) {
  equipoTransferir.value = e
  sucursalDestino.value = null
  cargarSucursales()
}

async function confirmarTransferir() {
  if (!equipoTransferir.value || !sucursalDestino.value) return
  loadingTransfer.value = true
  try {
    await equiposTransferApi.transferir(equipoTransferir.value.id, sucursalDestino.value)
    const destino = sucursales.value.find((s: any) => s.id === sucursalDestino.value)
    message.value = `✓ Equipo transferido a ${destino?.nombre || 'sucursal destino'}.`
    messageType.value = 'success'
    store.fetchEquipos(true)
    setTimeout(() => message.value = '', 4000)
  } catch (err: any) {
    message.value = err?.error || 'No se pudo transferir el equipo.'
    messageType.value = 'error'
  } finally {
    equipoTransferir.value = null
    loadingTransfer.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-slate-50 dark:bg-slate-900">
    <AppNavbar :usuario="auth.usuario" :links="navLinks" badge="Matriz" variant="blue" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 fade-in">

      <!-- Cabecera -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 dark:text-slate-100 tracking-tight">Equipos del Gimnasio</h1>
          <p class="text-slate-400 dark:text-slate-500 text-sm mt-1">Datos cargados desde la API del backend</p>
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
        <div v-if="message" class="bg-emerald-50 dark:bg-emerald-900/30 text-emerald-800 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-700 rounded-xl px-4 py-3 mb-5 text-sm font-medium">
          {{ message }}
        </div>
      </Transition>
      <Transition name="fade">
        <div v-if="errorMsg" class="bg-red-50 dark:bg-red-900/30 text-red-800 dark:text-red-300 border border-red-200 dark:border-red-700 rounded-xl px-4 py-3 mb-5 text-sm font-medium">
          {{ errorMsg }}
        </div>
      </Transition>

      <!-- Formulario -->
      <Transition name="slide-down">
        <div v-if="mostrarForm" class="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm p-6 mb-8">
          <h2 class="text-base font-bold text-slate-700 dark:text-slate-200 mb-5">Registrar Nuevo Equipo</h2>
          <form @submit.prevent="registrar" class="grid grid-cols-1 sm:grid-cols-2 gap-4">

            <!-- Nombre -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                Nombre <span class="text-red-500">*</span>
              </label>
              <input v-model="form.nombre" type="text" placeholder="Ej: Cinta de Correr"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 dark:bg-slate-900 focus:bg-white dark:focus:bg-slate-800 text-slate-700 dark:text-slate-200"
                :class="errores.nombre ? 'border-red-400 focus:border-red-500' : 'border-slate-200 dark:border-slate-600 focus:border-blue-500'" />
              <p v-if="errores.nombre" class="text-red-500 text-xs mt-1">{{ errores.nombre }}</p>
            </div>

            <!-- Categoría -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Categoría <span class="text-red-500">*</span></label>
              <select v-model="form.categoria"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200">
                <option v-for="c in categorias" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>

            <!-- Descripción -->
            <div class="sm:col-span-2">
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                Descripción <span class="text-red-500">*</span>
              </label>
              <textarea v-model="form.descripcion" rows="2" placeholder="Describe el equipo (mínimo 10 caracteres)…"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 dark:bg-slate-900 focus:bg-white dark:focus:bg-slate-800 text-slate-700 dark:text-slate-200 resize-none"
                :class="errores.descripcion ? 'border-red-400 focus:border-red-500' : 'border-slate-200 dark:border-slate-600 focus:border-blue-500'" />
              <p v-if="errores.descripcion" class="text-red-500 text-xs mt-1">{{ errores.descripcion }}</p>
            </div>

            <!-- URL imagen -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">URL de Imagen</label>
              <input v-model="form.imagen" type="text" placeholder="https://…"
                class="w-full px-4 py-3 border-2 rounded-xl text-sm focus:outline-none transition-all bg-slate-50 dark:bg-slate-900 focus:bg-white dark:focus:bg-slate-800 text-slate-700 dark:text-slate-200"
                :class="errores.imagen ? 'border-red-400 focus:border-red-500' : 'border-slate-200 dark:border-slate-600 focus:border-blue-500'" />
              <p v-if="errores.imagen" class="text-red-500 text-xs mt-1">{{ errores.imagen }}</p>
              <p class="text-slate-400 dark:text-slate-500 text-xs mt-1">Opcional. Si no se ingresa se usará una imagen por defecto.</p>
            </div>

            <!-- Estado -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Estado</label>
              <select v-model="form.estado"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200">
                <option value="disponible">Disponible</option>
                <option value="mantenimiento">En mantenimiento</option>
                <option value="fuera_de_servicio">Fuera de servicio</option>
              </select>
            </div>

            <!-- Marca -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Marca</label>
              <input v-model="form.marca" type="text" placeholder="Ej: Life Fitness"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-slate-50 dark:bg-slate-900 text-slate-700 dark:text-slate-200 focus:bg-white dark:focus:bg-slate-800" />
            </div>

            <!-- Modelo -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Modelo</label>
              <input v-model="form.modelo" type="text" placeholder="Ej: 95T"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-slate-50 dark:bg-slate-900 text-slate-700 dark:text-slate-200 focus:bg-white dark:focus:bg-slate-800" />
            </div>

            <!-- Valor de adquisición -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Valor de Adquisición ($)</label>
              <input v-model="form.valorAdquisicion" type="number" step="0.01" min="0" placeholder="Ej: 1500.00"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-slate-50 dark:bg-slate-900 text-slate-700 dark:text-slate-200 focus:bg-white dark:focus:bg-slate-800" />
            </div>

            <!-- Fecha de adquisición -->
            <div>
              <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Fecha de Adquisición</label>
              <input v-model="form.fechaAdquisicion" type="date"
                class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-slate-50 dark:bg-slate-900 text-slate-700 dark:text-slate-200 focus:bg-white dark:focus:bg-slate-800" />
            </div>

            <!-- Botón -->
            <div class="sm:col-span-2 flex justify-end gap-3 mt-1">
              <button type="button" @click="mostrarForm = false"
                class="px-5 py-2.5 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-semibold text-sm rounded-xl transition-all cursor-pointer">
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
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 dark:text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input v-model="busqueda" type="text" placeholder="Buscar equipo…"
            class="w-full pl-10 pr-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-200 placeholder-slate-400 dark:placeholder-slate-500" />
        </div>
        <select v-model="categoria"
          class="px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-200 sm:w-44">
          <option value="">Todas las categorías</option>
          <option v-for="c in categorias" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <p class="text-sm text-slate-400 dark:text-slate-500 font-medium mb-4">
        Mostrando <strong class="text-slate-600 dark:text-slate-300">{{ equiposFiltrados.length }}</strong> de {{ store.equipos.length }} equipos
      </p>

      <!-- Grid de equipos -->
      <div v-if="equiposFiltrados.length === 0" class="text-center py-20 text-slate-400 dark:text-slate-500">
        <p class="font-semibold">No se encontraron equipos.</p>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5 mb-10">
        <div
          v-for="e in equiposFiltrados"
          :key="e.id"
          class="bg-white dark:bg-slate-800 rounded-2xl border-2 border-slate-200 dark:border-slate-700 overflow-hidden hover:border-blue-400 dark:hover:border-blue-500 hover:shadow-md transition-all duration-200 group"
        >
          <!-- Imagen -->
          <div class="h-44 overflow-hidden bg-slate-100 dark:bg-slate-700">
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
              <h3 class="font-bold text-slate-800 dark:text-slate-100 text-base leading-tight">{{ e.nombre }}</h3>
              <span
                class="shrink-0 text-xs font-bold px-2.5 py-1 rounded-full"
                :class="{
                  'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-400': e.estado === 'disponible',
                  'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-400':     e.estado === 'mantenimiento',
                  'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-400':          e.estado === 'fuera_de_servicio',
                }"
              >
                {{ e.estado === 'disponible' ? 'Disponible' : e.estado === 'mantenimiento' ? 'Mantenimiento' : 'Fuera de servicio' }}
              </span>
            </div>
            <div class="flex items-center gap-2 mb-2 flex-wrap">
              <span class="inline-block text-xs font-semibold bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-400 px-2 py-0.5 rounded-full">
                {{ e.categoria }}
              </span>
              <span v-if="e.sucursalNombre" class="inline-block text-xs font-semibold bg-slate-100 text-slate-600 dark:bg-slate-700 dark:text-slate-300 px-2 py-0.5 rounded-full">
                {{ e.sucursalNombre }}
              </span>
            </div>
            <p class="text-slate-500 dark:text-slate-400 text-sm leading-relaxed">{{ e.descripcion }}</p>
            <!-- Botones editar/eliminar/transferir -->
            <div class="flex gap-2 mt-3 pt-3 border-t border-slate-100 dark:border-slate-700">
              <button @click.stop="abrirEditar(e)"
                class="flex-1 bg-blue-50 hover:bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:hover:bg-blue-800/50 dark:text-blue-400 font-semibold text-xs py-2 rounded-lg transition-all cursor-pointer">
                Editar
              </button>
              <button v-if="!auth.esSucursal" @click.stop="abrirTransferir(e)"
                class="flex-1 bg-amber-50 hover:bg-amber-100 text-amber-600 dark:bg-amber-900/30 dark:hover:bg-amber-800/50 dark:text-amber-400 font-semibold text-xs py-2 rounded-lg transition-all cursor-pointer">
                Transferir
              </button>
              <button @click.stop="equipoEliminar = e"
                class="flex-1 bg-red-50 hover:bg-red-100 text-red-600 dark:bg-red-900/30 dark:hover:bg-red-800/50 dark:text-red-400 font-semibold text-xs py-2 rounded-lg transition-all cursor-pointer">
                Eliminar
              </button>
            </div>
          </div>
        </div>
      </div>

    </main>

    <!-- Modal Editar -->
    <Transition name="fade">
      <div v-if="equipoEditar" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-xl p-6 w-full max-w-md">
          <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4">Editar Equipo</h3>
          <div class="flex flex-col gap-3">
            <input v-model="formEditar.nombre" type="text" placeholder="Nombre"
              class="w-full px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200" />
            <select v-model="formEditar.categoria"
              class="w-full px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200">
              <option v-for="c in categorias" :key="c" :value="c">{{ c }}</option>
            </select>
            <textarea v-model="formEditar.descripcion" rows="2" placeholder="Descripción"
              class="w-full px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all resize-none bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200" />
            <input v-model="formEditar.imagen" type="text" placeholder="URL de imagen (https://…)"
              class="w-full px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200" />
            <select v-model="formEditar.estado"
              class="w-full px-4 py-2.5 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200">
              <option value="disponible">Disponible</option>
              <option value="mantenimiento">En mantenimiento</option>
              <option value="fuera_de_servicio">Fuera de servicio</option>
            </select>
          </div>
          <div class="flex gap-3 mt-5">
            <button @click="guardarEdicion" :disabled="loadingEditar"
              class="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-bold text-sm py-2.5 rounded-xl transition-all cursor-pointer disabled:opacity-60">
              {{ loadingEditar ? 'Guardando…' : 'Guardar' }}
            </button>
            <button @click="equipoEditar = null"
              class="flex-1 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Modal Eliminar -->
    <Transition name="fade">
      <div v-if="equipoEliminar" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-xl p-6 w-full max-w-sm">
          <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-2">¿Eliminar equipo?</h3>
          <p class="text-sm text-slate-500 dark:text-slate-400 mb-5">
            Se eliminará <strong>{{ equipoEliminar?.nombre }}</strong>. Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-3">
            <button @click="confirmarEliminar"
              class="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Sí, eliminar
            </button>
            <button @click="equipoEliminar = null"
              class="flex-1 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Modal Transferir -->
    <Transition name="fade">
      <div v-if="equipoTransferir" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-xl p-6 w-full max-w-md">
          <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-1">Transferir Equipo</h3>
          <p class="text-sm text-slate-500 dark:text-slate-400 mb-4">
            Transferir <strong>{{ equipoTransferir?.nombre }}</strong> a otra sucursal.
          </p>

          <div class="bg-slate-50 dark:bg-slate-700 rounded-xl p-3 mb-4 text-sm">
            <p class="text-slate-500 dark:text-slate-400">Sucursal actual: <span class="font-semibold text-slate-700 dark:text-slate-200">{{ equipoTransferir?.sucursalNombre || 'Sin asignar' }}</span></p>
          </div>

          <div class="mb-5">
            <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5">Sucursal destino <span class="text-red-500">*</span></label>
            <select v-model="sucursalDestino"
              class="w-full px-4 py-3 border-2 border-slate-200 dark:border-slate-600 rounded-xl text-sm focus:outline-none focus:border-blue-500 transition-all bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200">
              <option :value="null" disabled>Seleccionar sucursal…</option>
              <option v-for="s in sucursales.filter(s => s.id !== equipoTransferir?.sucursalId)" :key="s.id" :value="s.id">
                {{ s.nombre }}{{ s.ciudad ? ` — ${s.ciudad}` : '' }}
              </option>
            </select>
          </div>

          <div class="flex gap-3">
            <button @click="confirmarTransferir" :disabled="!sucursalDestino || loadingTransfer"
              class="flex-1 bg-amber-500 hover:bg-amber-600 text-white font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer disabled:opacity-50">
              {{ loadingTransfer ? 'Transfiriendo…' : 'Transferir' }}
            </button>
            <button @click="equipoTransferir = null"
              class="flex-1 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-semibold text-sm py-2.5 rounded-xl transition-all cursor-pointer">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Footer />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active       { transition: opacity 0.25s ease; }
.fade-enter-from,  .fade-leave-to            { opacity: 0; }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; }
.slide-down-enter-from,   .slide-down-leave-to     { opacity: 0; transform: translateY(-10px); }
</style>
