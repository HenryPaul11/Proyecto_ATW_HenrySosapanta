/**
 * api.ts — Capa de servicio mock que simula llamadas HTTP.
 * Flujo: db.json → api.ts → stores → vistas
 */
import db from '@/data/db.json'

// Simula latencia de red
const delay = (ms = 300) => new Promise<void>(r => setTimeout(r, ms))

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface User {
  usuario: string
  contrasena: string
  rol: 'admin' | 'cliente' | 'entrenador'
}

export interface AdminStats {
  clientes: number
  membresias: number
  ingresos: number
}

export interface Cliente {
  id: number
  nombre: string
  apellido: string
  cedula: string
  telefono: string
  email: string
}

export interface TipoMembresia {
  id: number
  nombre: string
  descripcion: string
  duracion_dias: number
  precio: number
}

export interface Membresia {
  id: number
  cliente_id: number
  nombre: string
  apellido: string
  cedula: string
  tipo: string
  inicio: string
  fin: string
}

export interface ClienteSinMembresia {
  id: number
  nombre: string
  apellido: string
  cedula: string
  telefono: string
  email: string
}

export interface MembresiaCliente {
  id: number
  tipo_membresia: string
  precio: number
  fecha_inicio: string
  fecha_fin: string
  estado: string
}

export interface Pago {
  id: number
  cliente_nombre: string
  cliente_apellido: string
  cedula: string
  tipo_membresia: string
  monto: number
  metodo_pago: string
  fecha_pago: string
  fecha_inicio: string
  fecha_fin: string
  estado_membresia: string
}

export interface Auditoria {
  tabla_afectada: string
  accion: string
  usuario: string
  fecha_hora: string
  datos_anteriores: string
  datos_nuevos: string
}

export interface ClienteSession {
  id: number
  nombre: string
  apellido: string
  cedula: string
  telefono: string
  email: string
  fecha_registro: string
}

export interface MembresiaActiva {
  id: number
  tipo_membresia: string
  descripcion: string
  precio: number
  fecha_inicio: string
  fecha_fin: string
  estado: 'activa' | 'vencida'
}

export interface HistorialPago {
  id: number
  tipo_membresia: string
  monto: number
  metodo_pago: string
  fecha_pago: string
  fecha_inicio: string
  fecha_fin: string
}

export interface EntrenadorSession {
  id: number
  nombre: string
  apellido: string
  cedula: string
  telefono: string
  email: string
  especialidad: string
  fecha_ingreso: string
  horario: string
}

export interface ClienteAsignado {
  id: number
  nombre: string
  apellido: string
  cedula: string
  plan: string
  estado_membresia: string
  proxima_sesion: string | null
}

export interface Sesion {
  id: number
  cliente_nombre: string
  fecha: string
  hora: string
  tipo: string
  estado: 'completada' | 'pendiente' | 'cancelada'
  notas: string
}

export interface EntrenadorResumen {
  id: number
  nombre: string
  apellido: string
  especialidad: string
  clientes_activos: number
  sesiones_semana: number
  horario: string
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

export const authApi = {
  async login(usuario: string, contrasena: string): Promise<User | null> {
    await delay(600)
    return (db.users as User[]).find(u => u.usuario === usuario && u.contrasena === contrasena) ?? null
  },
}

// ─── Admin ────────────────────────────────────────────────────────────────────

export const adminApi = {
  async getStats(): Promise<AdminStats> {
    await delay()
    return db.adminStats as AdminStats
  },
}

// ─── Clientes ─────────────────────────────────────────────────────────────────

export const clientesApi = {
  async getAll(): Promise<Cliente[]> {
    await delay()
    return db.clientes as Cliente[]
  },

  async getByCedula(cedula: string): Promise<Cliente | null> {
    await delay(400)
    return (db.clientes as Cliente[]).find(c => c.cedula === cedula) ?? null
  },

  async getTiposMembresia(): Promise<TipoMembresia[]> {
    await delay()
    return db.tiposMembresia as TipoMembresia[]
  },

  async getMembresiasPorCliente(clienteId: number): Promise<MembresiaCliente[]> {
    await delay()
    const map = db.membresiasPorCliente as Record<string, MembresiaCliente[]>
    return map[String(clienteId)] ?? []
  },
}

// ─── Membresías ───────────────────────────────────────────────────────────────

export const membresiasApi = {
  async getAll(): Promise<Membresia[]> {
    await delay()
    return db.membresias as Membresia[]
  },

  async getClientesSinMembresia(): Promise<ClienteSinMembresia[]> {
    await delay()
    return db.clientesSinMembresia as ClienteSinMembresia[]
  },
}

// ─── Pagos ────────────────────────────────────────────────────────────────────

export const pagosApi = {
  async getAll(): Promise<Pago[]> {
    await delay(350)
    return db.pagos as Pago[]
  },

  async registrar(pago: Omit<Pago, 'id'>): Promise<Pago> {
    await delay(500)
    const nuevo: Pago = { id: Math.floor(Math.random() * 9000) + 1000, ...pago }
    return nuevo
  },
}

// ─── Auditorías ───────────────────────────────────────────────────────────────

export const auditoriasApi = {
  async getAll(): Promise<Auditoria[]> {
    await delay()
    return db.auditorias as Auditoria[]
  },
}

// ─── Cliente (sesión propia) ──────────────────────────────────────────────────

export const clienteApi = {
  async getByUsuario(usuario: string): Promise<ClienteSession | null> {
    await delay()
    const map = db.clientesPorUsuario as Record<string, ClienteSession>
    return map[usuario] ?? null
  },

  async getMembresia(clienteId: number): Promise<MembresiaActiva | null> {
    await delay()
    const map = db.membresiaCliente as Record<string, MembresiaActiva | null>
    return map[String(clienteId)] ?? null
  },

  async getHistorialPagos(clienteId: number): Promise<HistorialPago[]> {
    await delay()
    const map = db.historialPagos as Record<string, HistorialPago[]>
    return map[String(clienteId)] ?? []
  },
}

// ─── Entrenador ───────────────────────────────────────────────────────────────

export const entrenadorApi = {
  async getByUsuario(usuario: string): Promise<EntrenadorSession | null> {
    await delay()
    const map = db.entrenadorPorUsuario as Record<string, EntrenadorSession>
    return map[usuario] ?? null
  },

  async getClientesAsignados(entrenadorId: number): Promise<ClienteAsignado[]> {
    await delay()
    const map = db.clientesAsignados as Record<string, ClienteAsignado[]>
    return map[String(entrenadorId)] ?? []
  },

  async getSesiones(entrenadorId: number): Promise<Sesion[]> {
    await delay()
    const map = db.sesiones as Record<string, Sesion[]>
    return map[String(entrenadorId)] ?? []
  },

  async getTodos(): Promise<EntrenadorResumen[]> {
    await delay()
    return db.todosEntrenadores as EntrenadorResumen[]
  },
}
