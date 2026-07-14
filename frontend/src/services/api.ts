/* eslint-disable @typescript-eslint/no-explicit-any */
/**
 * api.ts — Capa de servicio API real conectada al backend de Spring Boot.
 */

import axios from 'axios'

// ─── Cliente HTTP Centralizado con Interceptores ──────────────────────────────
export const httpClient = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 30000,
})

// Interceptor para inyectar token JWT
httpClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Interceptor para desenvolver respuestas y mapear errores estructurados
httpClient.interceptors.response.use(
  (response) => {
    // Si la respuesta ya viene envuelta en { success, data, message }
    if (response.data && response.data.success !== undefined) {
      if (response.data.success) {
        return response.data
      } else {
        return Promise.reject(response.data)
      }
    }
    return { success: true, data: response.data }
  },
  (error) => {
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data)
    }
    return Promise.reject({
      success: false,
      error: error.message || 'Error de conexión con el servidor',
      code: 'CONNECTION_ERROR'
    })
  }
)

// ─── Tipos e Interfaces ────────────────────────────────────────────────────────

export interface User {
  id: number
  usuario: string
  nombre: string
  correo: string
  password?: string
  rol: 'admin' | 'cliente' | 'entrenador'
  token?: string // Token JWT opcional recibido al loguear
}

export interface AdminStats {
  totalClientes: number
  clientesActivos: number
  membresiasActivas: number
  membresiasTotales: number
  ingresosMensuales: number
  ingresosTotal: number
  sesionesTotales: number
  sesionesCompletadas: number
  sesionesPendientes: number
  pagosTotales: number
  equiposDisponibles: number
  totalEntrenadores: number
  // aliases para compatibilidad con vistas existentes
  clientes?: number
  membresias?: number
  ingresos?: number
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

export interface MembresiaActiva {
  id: number
  tipo_membresia: string
  descripcion: string
  precio: number
  fecha_inicio: string
  fecha_fin: string
  estado: 'activa' | 'vencida'
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

export interface HistorialPago {
  id: number
  tipo_membresia: string
  monto: number
  metodo_pago: string
  fecha_pago: string
  fecha_inicio: string
  fecha_fin: string
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
  clientes_activos: number;
  sesiones_semana: number;
  horario: string
}

export interface Equipo {
  id: number
  nombre: string
  categoria: string
  descripcion: string
  estado: string
  imagen: string
}

// ─── API Auth ─────────────────────────────────────────────────────────────────

export const authApi = {
  async login(usuario: string, password: string): Promise<User | null> {
    try {
      // Enviamos tanto 'usuario' como 'username' para total compatibilidad
      const res = (await httpClient.post<any>('/auth/login', {
        usuario,
        contrasena: password
      })) as any
      if (res && res.success && res.data) {
        const u = res.data
        if (u.rol) u.rol = u.rol.toLowerCase()
        // Guardar el token JWT si viene en la respuesta
        if (u.token) {
          sessionStorage.setItem('auth_token', u.token)
        }
        return u
      }
      return null
    } catch (e) {
      console.error('Error de login:', e)
      return null
    }
  },
}

// ─── API Admin ────────────────────────────────────────────────────────────────

export const adminApi = {
  async getStats(): Promise<AdminStats> {
    const res = await httpClient.get<any>('/admin/stats')
    const d = res.data
    // Normalizar campos para compatibilidad con el dashboard
    return {
      ...d,
      clientes:  d.totalClientes   ?? 0,
      membresias: d.membresiasActivas ?? 0,
      ingresos:  Number(d.ingresosMensuales ?? 0),
    }
  },
}

// ─── API Clientes ─────────────────────────────────────────────────────────────

export const clientesApi = {
  async getAll(): Promise<Cliente[]> {
    const res = await httpClient.get<any>('/clientes')
    return res.data
  },

  async getByCedula(cedula: string): Promise<Cliente | null> {
    try {
      const res = await httpClient.get<any>(`/clientes/cedula/${cedula}`)
      return res.data
    } catch {
      // Fallback a buscar query
      try {
        const res = await httpClient.get<any>(`/clientes/buscar?cedula=${cedula}`)
        return res.data
      } catch {
        return null
      }
    }
  },

  async getTiposMembresia(): Promise<TipoMembresia[]> {
    const res = await httpClient.get<any>('/tipos-membresia')
    return res.data
  },

  async getMembresiasPorCliente(clienteId: number): Promise<MembresiaCliente[]> {
    try {
      const res = await httpClient.get<any>(`/membresias/cliente/${clienteId}`)
      return res.data
    } catch {
      try {
        const res = await httpClient.get<any>(`/clientes/${clienteId}/membresias`)
        return res.data
      } catch {
        return []
      }
    }
  },
}

// ─── API Membresías ───────────────────────────────────────────────────────────

export const membresiasApi = {
  async getAll(): Promise<Membresia[]> {
    const res = await httpClient.get<any>('/membresias')
    return res.data
  },

  async getClientesSinMembresia(): Promise<ClienteSinMembresia[]> {
    try {
      const res = await httpClient.get<any>('/clientes/sin-membresia')
      return res.data
    } catch {
      try {
        const res = await httpClient.get<any>('/membresias/sin-membresia')
        return res.data
      } catch {
        return []
      }
    }
  },
}

// ─── API Pagos ────────────────────────────────────────────────────────────────

export const pagosApi = {
  async getAll(): Promise<Pago[]> {
    const res = await httpClient.get<any>('/pagos')
    return res.data
  },

  async registrar(pago: Omit<Pago, 'id'>): Promise<Pago> {
    const res = await httpClient.post<any>('/pagos', pago)
    return res.data
  },
}

// ─── API Auditorías ───────────────────────────────────────────────────────────

export const auditoriasApi = {
  async getAll(): Promise<Auditoria[]> {
    const res = await httpClient.get<any>('/auditorias')
    return res.data
  },
}

// ─── Cliente (sesión propia) ──────────────────────────────────────────────────

export const clienteApi = {
  async getByUsuario(usuario: string): Promise<ClienteSession | null> {
    try {
      const res = await httpClient.get<any>(`/clientes/usuario/${usuario}`)
      return res.data
    } catch {
      try {
        const res = await httpClient.get<any>(`/clientes/perfil?usuario=${usuario}`)
        return res.data
      } catch {
        return null
      }
    }
  },

  async getMembresia(clienteId: number): Promise<MembresiaActiva | null> {
    try {
      const res = await httpClient.get<any>(`/membresias/cliente/${clienteId}/activa`)
      return res.data
    } catch {
      return null
    }
  },

  async getHistorialPagos(clienteId: number): Promise<HistorialPago[]> {
    try {
      const res = await httpClient.get<any>(`/pagos/cliente/${clienteId}`)
      return res.data
    } catch {
      return []
    }
  },
}

// ─── Entrenador ───────────────────────────────────────────────────────────────

export const entrenadorApi = {
  async getByUsuario(usuario: string): Promise<EntrenadorSession | null> {
    try {
      const res = await httpClient.get<any>(`/entrenadores/usuario/${usuario}`)
      return res.data
    } catch {
      return null
    }
  },

  async getClientesAsignados(entrenadorId: number): Promise<ClienteAsignado[]> {
    try {
      const res = await httpClient.get<any>(`/entrenadores/${entrenadorId}/clientes`)
      return res.data
    } catch {
      return []
    }
  },

  async getSesiones(entrenadorId: number): Promise<Sesion[]> {
    try {
      const res = await httpClient.get<any>(`/sesiones/entrenador/${entrenadorId}`)
      return res.data
    } catch {
      return []
    }
  },

  async getTodos(): Promise<EntrenadorResumen[]> {
    const res = await httpClient.get<any>('/entrenadores')
    return res.data
  },
}

// ─── Equipos ──────────────────────────────────────────────────────────────────

export const equiposApi = {
  async getAll(): Promise<Equipo[]> {
    const res = await httpClient.get<any>('/equipos')
    return res.data
  },
}
