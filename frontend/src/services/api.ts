/**
 * api.ts — Capa de servicio mock.
 * Flujo: JSON individuales → api.ts → stores → vistas
 */

import usuarios   from '@/data/usuarios.json'
import clientesDB from '@/data/clientes.json'
import membresDB  from '@/data/membresias.json'
import pagosDB    from '@/data/pagos.json'
import auditDB    from '@/data/auditorias.json'
import entreDB    from '@/data/entrenadores.json'
import equiposDB  from '@/data/equipos.json'
import statsDB    from '@/data/stats.json'

const delay = (ms = 300) => new Promise<void>(r => setTimeout(r, ms))

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface User {
  id: number
  usuario: string
  nombre: string
  correo: string
  password: string
  rol: 'admin' | 'cliente' | 'entrenador'
}

export interface AdminStats   { clientes: number; membresias: number; ingresos: number }
export interface Cliente      { id: number; nombre: string; apellido: string; cedula: string; telefono: string; email: string }
export interface TipoMembresia{ id: number; nombre: string; descripcion: string; duracion_dias: number; precio: number }
export interface Membresia    { id: number; cliente_id: number; nombre: string; apellido: string; cedula: string; tipo: string; inicio: string; fin: string }
export interface ClienteSinMembresia { id: number; nombre: string; apellido: string; cedula: string; telefono: string; email: string }
export interface MembresiaCliente    { id: number; tipo_membresia: string; precio: number; fecha_inicio: string; fecha_fin: string; estado: string }
export interface MembresiaActiva     { id: number; tipo_membresia: string; descripcion: string; precio: number; fecha_inicio: string; fecha_fin: string; estado: 'activa' | 'vencida' }
export interface Pago         { id: number; cliente_nombre: string; cliente_apellido: string; cedula: string; tipo_membresia: string; monto: number; metodo_pago: string; fecha_pago: string; fecha_inicio: string; fecha_fin: string; estado_membresia: string }
export interface HistorialPago{ id: number; tipo_membresia: string; monto: number; metodo_pago: string; fecha_pago: string; fecha_inicio: string; fecha_fin: string }
export interface Auditoria    { tabla_afectada: string; accion: string; usuario: string; fecha_hora: string; datos_anteriores: string; datos_nuevos: string }
export interface ClienteSession{ id: number; nombre: string; apellido: string; cedula: string; telefono: string; email: string; fecha_registro: string }
export interface EntrenadorSession { id: number; nombre: string; apellido: string; cedula: string; telefono: string; email: string; especialidad: string; fecha_ingreso: string; horario: string }
export interface ClienteAsignado   { id: number; nombre: string; apellido: string; cedula: string; plan: string; estado_membresia: string; proxima_sesion: string | null }
export interface Sesion       { id: number; cliente_nombre: string; fecha: string; hora: string; tipo: string; estado: 'completada' | 'pendiente' | 'cancelada'; notas: string }
export interface EntrenadorResumen { id: number; nombre: string; apellido: string; especialidad: string; clientes_activos: number; sesiones_semana: number; horario: string }
export interface Equipo       { id: number; nombre: string; categoria: string; descripcion: string; estado: string; imagen: string }

// ─── Estado Temporal en Memoria ──────────────────────────────────────────────
const usuariosState = [...usuarios] as unknown as User[]
const clientesState = [...clientesDB.lista] as Cliente[]
const membresiasState = [...membresDB.lista] as Membresia[]
const pagosState = [...pagosDB.lista] as Pago[]
const equiposState = [...equiposDB] as Equipo[]

// ─── Auth ─────────────────────────────────────────────────────────────────────

export const authApi = {
  async login(usuario: string, password: string): Promise<User | null> {
    await delay(600)
    return usuariosState.find(u => u.usuario === usuario && u.password === password) ?? null
  },
}

// ─── Admin ────────────────────────────────────────────────────────────────────

export const adminApi = {
  async getStats(): Promise<AdminStats> {
    await delay()
    return statsDB.admin as AdminStats
  },
}

// ─── Clientes ─────────────────────────────────────────────────────────────────

export const clientesApi = {
  async getAll(): Promise<Cliente[]> {
    await delay()
    return clientesState
  },

  async getByCedula(cedula: string): Promise<Cliente | null> {
    await delay(400)
    return clientesState.find(c => c.cedula === cedula) ?? null
  },

  async getTiposMembresia(): Promise<TipoMembresia[]> {
    await delay()
    return membresDB.tipos as TipoMembresia[]
  },

  async getMembresiasPorCliente(clienteId: number): Promise<MembresiaCliente[]> {
    await delay()
    const map = membresDB.porCliente as Record<string, MembresiaCliente[]>
    return map[String(clienteId)] ?? []
  },
}

// ─── Membresías ───────────────────────────────────────────────────────────────

export const membresiasApi = {
  async getAll(): Promise<Membresia[]> {
    await delay()
    return membresiasState
  },

  async getClientesSinMembresia(): Promise<ClienteSinMembresia[]> {
    await delay()
    return clientesDB.sinMembresia as ClienteSinMembresia[]
  },
}

// ─── Pagos ────────────────────────────────────────────────────────────────────

export const pagosApi = {
  async getAll(): Promise<Pago[]> {
    await delay(350)
    return pagosState
  },

  async registrar(pago: Omit<Pago, 'id'>): Promise<Pago> {
    await delay(500)
    const nuevo = { id: Math.floor(Math.random() * 9000) + 1000, ...pago }
    pagosState.unshift(nuevo)
    return nuevo
  },
}

// ─── Auditorías ───────────────────────────────────────────────────────────────

export const auditoriasApi = {
  async getAll(): Promise<Auditoria[]> {
    await delay()
    return auditDB as Auditoria[]
  },
}

// ─── Cliente (sesión propia) ──────────────────────────────────────────────────

export const clienteApi = {
  async getByUsuario(usuario: string): Promise<ClienteSession | null> {
    await delay()
    const map = clientesDB.porUsuario as Record<string, ClienteSession>
    return map[usuario] ?? null
  },

  async getMembresia(clienteId: number): Promise<MembresiaActiva | null> {
    await delay()
    const map = membresDB.porClienteDetalle as Record<string, MembresiaActiva | null>
    return map[String(clienteId)] ?? null
  },

  async getHistorialPagos(clienteId: number): Promise<HistorialPago[]> {
    await delay()
    const map = pagosDB.historialPorCliente as Record<string, HistorialPago[]>
    return map[String(clienteId)] ?? []
  },
}

// ─── Entrenador ───────────────────────────────────────────────────────────────

export const entrenadorApi = {
  async getByUsuario(usuario: string): Promise<EntrenadorSession | null> {
    await delay()
    const map = entreDB.porUsuario as Record<string, EntrenadorSession>
    return map[usuario] ?? null
  },

  async getClientesAsignados(entrenadorId: number): Promise<ClienteAsignado[]> {
    await delay()
    const map = entreDB.clientesAsignados as Record<string, ClienteAsignado[]>
    return map[String(entrenadorId)] ?? []
  },

  async getSesiones(entrenadorId: number): Promise<Sesion[]> {
    await delay()
    const map = entreDB.sesiones as Record<string, Sesion[]>
    return map[String(entrenadorId)] ?? []
  },

  async getTodos(): Promise<EntrenadorResumen[]> {
    await delay()
    return entreDB.todos as EntrenadorResumen[]
  },
}

// ─── Equipos ──────────────────────────────────────────────────────────────────

export const equiposApi = {
  async getAll(): Promise<Equipo[]> {
    await delay()
    return equiposState
  },
}
