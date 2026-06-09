<template>
  <div>
    <Navbar :usuario="auth.usuario" @logout="logout" />

    <div class="container fade-in">
      <h1>Gestión de Membresías</h1>

      <div class="alert alert-info">
        <strong>Nota:</strong> Los estados se calculan automáticamente según la fecha actual.
        <small>Fecha actual: {{ fechaHoy }}</small>
      </div>

      <!-- Estadísticas -->
      <div class="stats-container">
        <StatsCard title="Membresías Activas"      :value="activas.length"      color="green" />
        <StatsCard title="Membresías Vencidas"     :value="vencidas.length"     color="red" />
        <StatsCard title="Clientes Sin Membresía"  :value="sinMembresia.length" color="orange" />
      </div>

      <!-- Clientes sin membresía -->
      <template v-if="sinMembresia.length > 0">
        <div class="alert alert-warning">
          Hay <strong>{{ sinMembresia.length }}</strong> cliente(s) sin membresía activa.
        </div>
        <h2>Clientes Sin Membresía Activa</h2>
        <MembresiaTable
          :columns="['Nombre', 'Cédula', 'Teléfono', 'Email', 'Acciones']"
          :rows="sinMembresia"
          empty-text="No hay clientes sin membresía."
        >
          <tr v-for="c in sinMembresia" :key="c.id">
            <td><strong>{{ c.nombre }} {{ c.apellido }}</strong></td>
            <td>{{ c.cedula }}</td>
            <td>{{ c.telefono }}</td>
            <td>{{ c.email }}</td>
            <td class="actions">
              <button class="btn btn-success">➕ Asignar Membresía</button>
            </td>
          </tr>
        </MembresiaTable>
      </template>

      <!-- ── Membresías Activas con buscador + paginador ── -->
      <div class="section-header">
        <h2>Membresías Activas</h2>
        <div class="search-box">
          <span class="search-icon">🔍</span>
          <input
            v-model="busqueda"
            type="text"
            placeholder="Buscar por nombre, cédula o tipo..."
            @input="paginaActual = 1"
          />
          <button v-if="busqueda" class="clear-btn" @click="busqueda = ''; paginaActual = 1">✕</button>
        </div>
      </div>

      <MembresiaTable
        :columns="['Cliente', 'Cédula', 'Tipo', 'Fecha Inicio', 'Fecha Fin', 'Días Restantes', 'Estado', 'Acciones']"
        :rows="activasPagina"
        empty-text="No hay membresías activas que coincidan."
      >
        <tr v-for="m in activasPagina" :key="m.id">
          <td><strong>{{ m.nombre }} {{ m.apellido }}</strong></td>
          <td>{{ m.cedula }}</td>
          <td>{{ m.tipo }}</td>
          <td>{{ formatDate(m.inicio) }}</td>
          <td>{{ formatDate(m.fin) }}</td>
          <td>
            <span class="badge" :class="m.diasRestantes <= 7 ? 'badge-proxima' : 'badge-activa'">
              {{ m.diasRestantes }} días
            </span>
          </td>
          <td><span class="badge badge-activa">Activa</span></td>
          <td class="actions">—</td>
        </tr>
      </MembresiaTable>

      <!-- Paginador -->
      <div v-if="totalPaginas > 1" class="paginador">
        <button :disabled="paginaActual === 1" @click="paginaActual--">‹ Anterior</button>

        <button
          v-for="n in totalPaginas"
          :key="n"
          :class="{ active: n === paginaActual }"
          @click="paginaActual = n"
        >
          {{ n }}
        </button>

        <button :disabled="paginaActual === totalPaginas" @click="paginaActual++">Siguiente ›</button>
      </div>

      <p v-if="activasFiltradas.length" class="paginador-info">
        Mostrando {{ desde + 1 }}–{{ Math.min(hasta, activasFiltradas.length) }}
        de {{ activasFiltradas.length }} membresías
        <span v-if="busqueda"> · filtrado por "<strong>{{ busqueda }}</strong>"</span>
      </p>

      <!-- Membresías Vencidas -->
      <h2 style="margin-top: 40px;">Membresías Vencidas</h2>
      <MembresiaTable
        :columns="['Cliente', 'Cédula', 'Tipo', 'Fecha Inicio', 'Fecha Fin', 'Estado', 'Acciones']"
        :rows="vencidas"
        empty-text="No hay membresías vencidas."
      >
        <tr v-for="m in vencidas" :key="m.id">
          <td><strong>{{ m.nombre }} {{ m.apellido }}</strong></td>
          <td>{{ m.cedula }}</td>
          <td>{{ m.tipo }}</td>
          <td>{{ formatDate(m.inicio) }}</td>
          <td>{{ formatDate(m.fin) }}</td>
          <td><span class="badge badge-vencida">Vencida</span></td>
          <td class="actions">
            <button class="btn btn-primary">🔄 Renovar</button>
          </td>
        </tr>
      </MembresiaTable>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import Navbar from '../components/shared/Navbar.vue'
import Footer from '../components/shared/Footer.vue'
import StatsCard from '../components/membresias/StatsCard.vue'
import MembresiaTable from '../components/membresias/MembresiaTable.vue'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}

const hoy = new Date()
const fechaHoy = hoy.toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })

function diasRestantes(fin) {
  return Math.max(0, Math.ceil((new Date(fin) - hoy) / 86400000))
}

function formatDate(d) {
  return new Date(d).toLocaleDateString('es-EC', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

// ── Mock data ──────────────────────────────────────────────
const todasMembresias = [
  { id: 1,  nombre: 'Juan',    apellido: 'Pérez',    cedula: '1234567890', tipo: 'Premium', inicio: '2026-05-01', fin: '2026-06-25' },
  { id: 2,  nombre: 'María',   apellido: 'López',    cedula: '0987654321', tipo: 'Básica',  inicio: '2026-04-01', fin: '2026-06-20' },
  { id: 3,  nombre: 'Carlos',  apellido: 'Ruiz',     cedula: '1122334455', tipo: 'Mensual', inicio: '2026-03-01', fin: '2026-04-01' },
  { id: 4,  nombre: 'Ana',     apellido: 'Torres',   cedula: '5566778899', tipo: 'Premium', inicio: '2026-02-01', fin: '2026-03-01' },
  { id: 5,  nombre: 'Pedro',   apellido: 'Salazar',  cedula: '6677889900', tipo: 'Premium', inicio: '2026-05-10', fin: '2026-07-10' },
  { id: 6,  nombre: 'Lucía',   apellido: 'Mora',     cedula: '7788990011', tipo: 'Básica',  inicio: '2026-05-15', fin: '2026-06-15' },
  { id: 7,  nombre: 'Diego',   apellido: 'Castro',   cedula: '8899001122', tipo: 'Mensual', inicio: '2026-05-01', fin: '2026-06-01' },
  { id: 8,  nombre: 'Sofía',   apellido: 'Vega',     cedula: '9900112233', tipo: 'Premium', inicio: '2026-04-20', fin: '2026-07-20' },
  { id: 9,  nombre: 'Andrés',  apellido: 'Ríos',     cedula: '0011223344', tipo: 'Básica',  inicio: '2026-05-05', fin: '2026-06-05' },
  { id: 10, nombre: 'Valeria', apellido: 'Ortiz',    cedula: '1122334466', tipo: 'Mensual', inicio: '2026-05-20', fin: '2026-06-20' },
]

const sinMembresia = [
  { id: 20, nombre: 'Luis', apellido: 'Gómez', cedula: '1111111111', telefono: '0991111111', email: 'luis@mail.com' },
  { id: 21, nombre: 'Sara', apellido: 'Mora',  cedula: '2222222222', telefono: '0992222222', email: 'sara@mail.com' },
]

const activas = computed(() =>
  todasMembresias
    .filter(m => new Date(m.fin) >= hoy)
    .map(m => ({ ...m, diasRestantes: diasRestantes(m.fin) }))
    .sort((a, b) => a.diasRestantes - b.diasRestantes)
)

const vencidas = computed(() =>
  todasMembresias.filter(m => new Date(m.fin) < hoy)
)

// ── Buscador ──────────────────────────────────────
const busqueda = ref('')

const activasFiltradas = computed(() => {
  const q = busqueda.value.toLowerCase().trim()
  if (!q) return activas.value
  return activas.value.filter(m =>
    `${m.nombre} ${m.apellido}`.toLowerCase().includes(q) ||
    m.cedula.includes(q) ||
    m.tipo.toLowerCase().includes(q)
  )
})

// ── Paginador ─────────────────────────────────────
const POR_PAGINA = 5
const paginaActual = ref(1)

const totalPaginas = computed(() =>
  Math.max(1, Math.ceil(activasFiltradas.value.length / POR_PAGINA))
)

const desde = computed(() => (paginaActual.value - 1) * POR_PAGINA)
const hasta = computed(() => desde.value + POR_PAGINA)

const activasPagina = computed(() =>
  activasFiltradas.value.slice(desde.value, hasta.value)
)
</script>

<style scoped>
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 50px 60px;
}

.container h1 {
  color: var(--text-main);
  font-size: 38px;
  margin-bottom: 30px;
  font-weight: 700;
  letter-spacing: -0.8px;
}

.container h2 {
  color: var(--text-main);
  font-size: 22px;
  margin-bottom: 16px;
  font-weight: 700;
}

/* ── Barra buscador + título ── */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin: 40px 0 16px;
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 12px;
  padding: 10px 16px;
  gap: 8px;
  min-width: 300px;
  transition: border-color 0.2s;
}

.search-box:focus-within { border-color: #3b82f6; }

.search-icon { font-size: 15px; flex-shrink: 0; }

.search-box input {
  border: none;
  outline: none;
  background: transparent;
  color: var(--text-main);
  font-size: 14px;
  font-family: inherit;
  width: 100%;
}

.search-box input::placeholder { color: var(--text-muted); }

.clear-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
  font-size: 14px;
  padding: 2px 4px;
  border-radius: 4px;
  transition: color 0.2s;
}
.clear-btn:hover { color: #ef4444; }

/* ── Paginador ── */
.paginador {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  margin: 16px 0 8px;
  flex-wrap: wrap;
}

.paginador button {
  padding: 8px 14px;
  border: 2px solid var(--border);
  background: var(--bg-card);
  color: var(--text-main);
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.paginador button:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.paginador button.active {
  background: linear-gradient(135deg, #3b82f6, #1e40af);
  color: white;
  border-color: #3b82f6;
}

.paginador button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.paginador-info {
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 30px;
}

/* ── Alertas ── */
.alert {
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  font-size: 14px;
  font-weight: 500;
}
.alert small { display: block; margin-top: 5px; font-size: 12px; opacity: 0.8; }
.alert strong { font-weight: 700; }

.alert-info    { background: var(--bg-card); color: #1e40af; border-left: 4px solid #3b82f6; }
.alert-warning { background: #fef3c7; color: #92400e; border-left: 4px solid #f59e0b; }
html.dark .alert-info { color: #93c5fd; }
html.dark .alert-warning { background: #2d1f00; color: #fbbf24; }

/* ── Stats ── */
.stats-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

/* ── Badges ── */
.badge {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}
.badge-activa  { background: #d1fae5; color: #065f46; }
.badge-vencida { background: #fee2e2; color: #991b1b; }
.badge-proxima { background: #fef3c7; color: #92400e; }

html.dark .badge-activa  { background: #064e3b; color: #6ee7b7; }
html.dark .badge-vencida { background: #450a0a; color: #fca5a5; }
html.dark .badge-proxima { background: #451a03; color: #fcd34d; }

/* ── Botones ── */
.btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
  font-family: inherit;
}
.btn-primary { background: #3b82f6; color: white; }
.btn-primary:hover { background: #2563eb; transform: translateY(-2px); }
.btn-success { background: #10b981; color: white; }
.btn-success:hover { background: #059669; transform: translateY(-2px); }

.actions { display: flex; gap: 8px; flex-wrap: wrap; }

@media (max-width: 1200px) {
  .stats-container { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .container { padding: 30px 20px; }
  .container h1 { font-size: 28px; }
  .stats-container { grid-template-columns: 1fr; }
  .section-header { flex-direction: column; align-items: stretch; }
  .search-box { min-width: auto; }
  .actions { flex-direction: column; }
  .btn { width: 100%; text-align: center; }
}
</style>
