<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useMembresiasStore } from '@/stores/membresiasStore'
import Navbar         from '@/components/shared/Navbar.vue'
import Footer         from '@/components/shared/Footer.vue'
import StatsCard      from '@/components/membresias/StatsCard.vue'
import MembresiaTable from '@/components/membresias/MembresiaTable.vue'

const router     = useRouter()
const auth       = useAuthStore()
const store      = useMembresiasStore()

function logout() { auth.logout(); router.push('/login') }

const hoy      = new Date()
const fechaHoy = hoy.toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })

function diasRestantes(fin) { return Math.max(0, Math.ceil((new Date(fin) - hoy) / 86_400_000)) }
function formatDate(d)      { return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' }) }

const activas = computed(() =>
  store.membresias
    .filter(m => new Date(m.fin) >= hoy)
    .map(m => ({ ...m, diasRestantes: diasRestantes(m.fin) }))
    .sort((a, b) => a.diasRestantes - b.diasRestantes),
)
const vencidas     = computed(() => store.membresias.filter(m => new Date(m.fin) < hoy))
const sinMembresia = computed(() => store.clientesSinMembresia)

onMounted(() => store.fetchAll())
</script>

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

      <!-- Loading -->
      <div v-if="store.loading" class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div v-for="i in 3" :key="i" class="h-20 bg-slate-200 animate-pulse rounded-2xl" />
      </div>

      <template v-else>
        <!-- Estadísticas -->
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
          <StatsCard title="Membresías Activas"  :value="activas.length"      color="green"  />
          <StatsCard title="Membresías Vencidas" :value="vencidas.length"     color="red"    />
          <StatsCard title="Sin Membresía"       :value="sinMembresia.length" color="orange" />
        </div>

        <!-- Clientes sin membresía -->
        <template v-if="sinMembresia.length > 0">
          <div class="bg-amber-50 border-l-4 border-amber-400 text-amber-800 rounded-xl px-4 py-3 mb-6 text-sm font-medium">
            Hay <strong class="font-bold">{{ sinMembresia.length }}</strong> cliente(s) sin membresía activa.
          </div>

          <h2 class="text-lg sm:text-xl font-bold text-slate-800 mt-8 mb-4">Clientes Sin Membresía Activa</h2>

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
                <router-link
                  :to="{ name: 'RenovarMembresia', params: { clienteId: m.id } }"
                  class="bg-blue-500 hover:bg-blue-600 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm inline-block whitespace-nowrap"
                >
                  Renovar
                </router-link>
              </td>
            </tr>
          </tbody>
        </MembresiaTable>
      </template>

    </main>

    <Footer />
  </div>
</template>
