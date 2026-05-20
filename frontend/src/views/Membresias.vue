<template>
  <div class="min-h-screen flex flex-col bg-slate-50 overflow-x-hidden">
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 md:py-10 fade-in">

      <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 tracking-tight mb-6">
        Gestión de Membresías
      </h1>

      <!-- Alerta informativa -->
      <div class="bg-white border-l-4 border-blue-500 text-blue-800 rounded-xl px-4 py-3 mb-6 text-sm font-medium">
        <strong class="font-bold">Nota:</strong> Los estados se actualizan automáticamente según la fecha actual.
        <small class="block mt-1 text-xs opacity-75">Fecha actual: {{ fechaHoy }}</small>
      </div>

      <!-- Estadísticas -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <StatsCard title="Membresías Activas"     :value="activas.length"      color="green"  />
        <StatsCard title="Membresías Vencidas"    :value="vencidas.length"     color="red"    />
        <StatsCard title="Sin Membresía"          :value="sinMembresia.length" color="orange" />
      </div>

      <!-- Clientes sin membresía -->
      <template v-if="sinMembresia.length > 0">
        <div class="bg-amber-50 border-l-4 border-amber-400 text-amber-800 rounded-xl px-4 py-3 mb-6 text-sm font-medium">
          Hay <strong class="font-bold">{{ sinMembresia.length }}</strong> cliente(s) sin membresía activa.
        </div>

        <h2 class="text-lg sm:text-xl font-bold text-slate-800 mt-8 mb-4">
          Clientes Sin Membresía Activa
        </h2>

        <MembresiaTable
          :columns="['Nombre', 'Cédula', 'Teléfono', 'Email', 'Acciones']"
          :rows="sinMembresia"
          empty-text="No hay clientes sin membresía."
        >
          <tbody class="membresia-table-body">
            <tr v-for="c in sinMembresia" :key="c.id">
              <td><strong>{{ c.nombre }} {{ c.apellido }}</strong></td>
              <td>{{ c.cedula }}</td>
              <td>{{ c.telefono }}</td>
              <td>{{ c.email }}</td>
              <td>
                <router-link
                  :to="{ name: 'AsignarMembresia' }"
                  class="bg-emerald-500 hover:bg-emerald-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Asignar
                </router-link>
              </td>
            </tr>
          </tbody>
        </MembresiaTable>
      </template>

      <!-- Membresías Activas -->
      <h2 class="text-lg sm:text-xl font-bold text-slate-800 mt-8 mb-4">
        Membresías Activas
        <span class="text-sm font-normal text-slate-400 ml-1 hidden sm:inline">({{ fechaHoy }} o posterior)</span>
      </h2>

      <MembresiaTable
        :columns="['Cliente', 'Cédula', 'Tipo', 'F. Inicio', 'F. Fin', 'Días', 'Estado', 'Acción']"
        :rows="activas"
        empty-text="No hay membresías activas."
      >
        <tbody class="membresia-table-body">
          <tr v-for="m in activas" :key="m.id">
            <td><strong>{{ m.nombre }} {{ m.apellido }}</strong></td>
            <td>{{ m.cedula }}</td>
            <td>{{ m.tipo }}</td>
            <td>{{ formatDate(m.inicio) }}</td>
            <td>{{ formatDate(m.fin) }}</td>
            <td>
              <span
                class="inline-block px-2.5 py-1 rounded-full text-xs font-bold"
                :class="m.diasRestantes <= 7 ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'"
              >
                {{ m.diasRestantes }}d
              </span>
            </td>
            <td>
              <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                Activa
              </span>
            </td>
            <td class="text-slate-400 text-sm">—</td>
          </tr>
        </tbody>
      </MembresiaTable>

      <!-- Membresías Vencidas -->
      <h2 class="text-lg sm:text-xl font-bold text-slate-800 mt-8 mb-4">
        Membresías Vencidas
        <span class="text-sm font-normal text-slate-400 ml-1 hidden sm:inline">(anterior a {{ fechaHoy }})</span>
      </h2>

      <MembresiaTable
        :columns="['Cliente', 'Cédula', 'Tipo', 'F. Inicio', 'F. Fin', 'Estado', 'Acción']"
        :rows="vencidas"
        empty-text="No hay membresías vencidas."
      >
        <tbody class="membresia-table-body">
          <tr v-for="m in vencidas" :key="m.id">
            <td><strong>{{ m.nombre }} {{ m.apellido }}</strong></td>
            <td>{{ m.cedula }}</td>
            <td>{{ m.tipo }}</td>
            <td>{{ formatDate(m.inicio) }}</td>
            <td>{{ formatDate(m.fin) }}</td>
            <td>
              <span class="inline-block px-2.5 py-1 rounded-full text-xs font-bold bg-red-100 text-red-800">
                Vencida
              </span>
            </td>
            <td>
              <button class="bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm cursor-pointer whitespace-nowrap">
                Renovar
              </button>
            </td>
          </tr>
        </tbody>
      </MembresiaTable>

    </main>

    <Footer />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import Navbar         from '../components/shared/Navbar.vue'
import Footer         from '../components/shared/Footer.vue'
import StatsCard      from '../components/membresias/StatsCard.vue'
import MembresiaTable from '../components/membresias/MembresiaTable.vue'

const router = useRouter()
const auth   = useAuthStore()

function logout() { auth.logout(); router.push('/login') }

const hoy = new Date()
const fechaHoy = hoy.toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })

function diasRestantes(fin) { return Math.max(0, Math.ceil((new Date(fin) - hoy) / 86_400_000)) }
function formatDate(d) { return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' }) }

const todasMembresias = [
  { id: 1, nombre: 'Juan',   apellido: 'Pérez',  cedula: '1234567890', tipo: 'Premium', inicio: '2026-05-01', fin: '2026-05-25' },
  { id: 2, nombre: 'María',  apellido: 'López',  cedula: '0987654321', tipo: 'Básica',  inicio: '2026-04-01', fin: '2026-05-20' },
  { id: 3, nombre: 'Carlos', apellido: 'Ruiz',   cedula: '1122334455', tipo: 'Mensual', inicio: '2026-03-01', fin: '2026-04-01' },
  { id: 4, nombre: 'Ana',    apellido: 'Torres', cedula: '5566778899', tipo: 'Premium', inicio: '2026-02-01', fin: '2026-03-01' },
]
const sinMembresia = [
  { id: 10, nombre: 'Luis', apellido: 'Gómez', cedula: '1111111111', telefono: '0991111111', email: 'luis@mail.com' },
  { id: 11, nombre: 'Sara', apellido: 'Mora',  cedula: '2222222222', telefono: '0992222222', email: 'sara@mail.com' },
]

const activas = computed(() =>
  todasMembresias.filter(m => new Date(m.fin) >= hoy)
    .map(m => ({ ...m, diasRestantes: diasRestantes(m.fin) }))
    .sort((a, b) => a.diasRestantes - b.diasRestantes),
)
const vencidas = computed(() => todasMembresias.filter(m => new Date(m.fin) < hoy))
</script>
