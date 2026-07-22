-- =============================================
-- PowerFit - Sistema de Gestión de Gimnasio
-- Base de datos: PostgreSQL
-- =============================================

-- Crear base de datos (ejecutar como superuser)
-- CREATE DATABASE sistema_gimnasio_pg;
-- CREATE USER powerfit_user WITH PASSWORD 'powerfit2026';
-- GRANT ALL PRIVILEGES ON DATABASE sistema_gimnasio_pg TO powerfit_user;

-- =============================================
-- Enum Types
-- =============================================

DO $$ BEGIN
    CREATE TYPE estado_general AS ENUM ('ACTIVO', 'INACTIVO');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE accion_auditoria AS ENUM ('INSERT', 'UPDATE', 'DELETE', 'TRANSFERENCIA');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE estado_pago AS ENUM ('COMPLETADO', 'PENDIENTE', 'ANULADO', 'RECHAZADO');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE metodo_pago AS ENUM ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'PAGO_MOVIL');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE estado_membresia AS ENUM ('ACTIVA', 'VENCIDA', 'CANCELADA', 'SUSPENDIDA');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE estado_equipo AS ENUM ('OPERATIVO', 'MANTENIMIENTO', 'DADO_DE_BAJA');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE tipo_sesion AS ENUM ('CLASE_GRUPAL', 'ENTRENAMIENTO_PERSONAL', 'LIBRE');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE ambito_rol AS ENUM ('MATRIZ', 'SUCURSAL');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- =============================================
-- Tables
-- =============================================

-- Sucursales
CREATE TABLE IF NOT EXISTS sucursales (
    sucursal_id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(80) NOT NULL,
    telefono VARCHAR(20),
    email_contacto VARCHAR(150),
    estado estado_general NOT NULL DEFAULT 'ACTIVO',
    fecha_apertura DATE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    rol_id BIGSERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    ambito ambito_rol NOT NULL,
    descripcion VARCHAR(255)
);

-- Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    usuario_id BIGSERIAL PRIMARY KEY,
    sucursal_id BIGINT REFERENCES sucursales(sucursal_id),
    rol_id BIGINT NOT NULL REFERENCES roles(rol_id),
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    estado estado_general NOT NULL DEFAULT 'ACTIVO',
    ultimo_acceso TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Clientes
CREATE TABLE IF NOT EXISTS clientes (
    cliente_id BIGSERIAL PRIMARY KEY,
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    usuario_id BIGINT REFERENCES usuarios(usuario_id),
    nombre_completo VARCHAR(150) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150),
    telefono VARCHAR(20),
    password_hash VARCHAR(255),
    fecha_nacimiento DATE,
    genero VARCHAR(20),
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    estado estado_general NOT NULL DEFAULT 'ACTIVO',
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Entrenadores
CREATE TABLE IF NOT EXISTS entrenadores (
    entrenador_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(usuario_id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    nombre_completo VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100),
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(150),
    horario VARCHAR(100),
    fecha_contratacion DATE,
    estado estado_general NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Categorías de Equipo
CREATE TABLE IF NOT EXISTS categorias_equipo (
    categoria_id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Equipos
CREATE TABLE IF NOT EXISTS equipos (
    equipo_id BIGSERIAL PRIMARY KEY,
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    categoria_id BIGINT NOT NULL REFERENCES categorias_equipo(categoria_id),
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(80),
    modelo VARCHAR(80),
    numero_serie VARCHAR(50) NOT NULL UNIQUE,
    fecha_adquisicion DATE,
    valor_adquisicion DECIMAL(10,2),
    estado estado_equipo NOT NULL DEFAULT 'OPERATIVO',
    imagen_url VARCHAR(2000),
    descripcion VARCHAR(500),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Planes
CREATE TABLE IF NOT EXISTS planes (
    plan_id BIGSERIAL PRIMARY KEY,
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    nombre_plan VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    duracion_dias INTEGER NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado estado_general NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Membresías
CREATE TABLE IF NOT EXISTS membresias (
    membresia_id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(cliente_id),
    plan_id BIGINT NOT NULL REFERENCES planes(plan_id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado estado_membresia NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Pagos
CREATE TABLE IF NOT EXISTS pagos (
    pago_id BIGSERIAL PRIMARY KEY,
    membresia_id BIGINT NOT NULL REFERENCES membresias(membresia_id),
    cliente_id BIGINT NOT NULL REFERENCES clientes(cliente_id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago metodo_pago NOT NULL,
    estado estado_pago NOT NULL DEFAULT 'COMPLETADO',
    referencia_transaccion VARCHAR(100),
    fecha_pago TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Sesiones
CREATE TABLE IF NOT EXISTS sesiones (
    sesion_id BIGSERIAL PRIMARY KEY,
    sucursal_id BIGINT NOT NULL REFERENCES sucursales(sucursal_id),
    entrenador_id BIGINT NOT NULL REFERENCES entrenadores(entrenador_id),
    cliente_id BIGINT REFERENCES clientes(cliente_id),
    nombre VARCHAR(150) NOT NULL,
    tipo tipo_sesion NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    cupo_maximo INTEGER DEFAULT 20,
    estado estado_general NOT NULL DEFAULT 'ACTIVO'
);

-- Auditoría
CREATE TABLE IF NOT EXISTS auditoria (
    auditoria_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(usuario_id),
    sucursal_id BIGINT REFERENCES sucursales(sucursal_id),
    tabla_afectada VARCHAR(50) NOT NULL,
    accion accion_auditoria NOT NULL,
    registro_id BIGINT,
    datos_anteriores JSONB,
    datos_nuevos JSONB,
    ip_origen VARCHAR(45),
    fecha_hora TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================
-- Índices
-- =============================================

CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_sucursal ON usuarios(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_clientes_documento ON clientes(documento_identidad);
CREATE INDEX IF NOT EXISTS idx_clientes_sucursal ON clientes(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_entrenadores_documento ON entrenadores(documento_identidad);
CREATE INDEX IF NOT EXISTS idx_entrenadores_sucursal ON entrenadores(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_equipos_sucursal ON equipos(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_equipos_categoria ON equipos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_membresias_cliente ON membresias(cliente_id);
CREATE INDEX IF NOT EXISTS idx_membresias_plan ON membresias(plan_id);
CREATE INDEX IF NOT EXISTS idx_pagos_cliente ON pagos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pagos_sucursal ON pagos(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_sesiones_sucursal ON sesiones(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_sesiones_entrenador ON sesiones(entrenador_id);
CREATE INDEX IF NOT EXISTS idx_sesiones_cliente ON sesiones(cliente_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_auditoria_tabla ON auditoria(tabla_afectada);

-- =============================================
-- Datos iniciales
-- =============================================

-- Roles
INSERT INTO roles (nombre_rol, ambito, descripcion) VALUES
    ('ADMIN', 'MATRIZ', 'Administrador principal del sistema'),
    ('ADMIN_SUCURSAL', 'SUCURSAL', 'Administrador de sucursal'),
    ('ENTRENADOR', 'SUCURSAL', 'Entrenador personal'),
    ('CLIENTE', 'SUCURSAL', 'Cliente del gimnasio')
ON CONFLICT (nombre_rol) DO NOTHING;

-- Sucursal inicial
INSERT INTO sucursales (nombre, codigo, direccion, ciudad, telefono, email_contacto) VALUES
    ('PowerFit Matriz', 'MAT001', 'Av. Principal 123', 'Guayaquil', '0991234561', 'matriz@powerfit.com')
ON CONFLICT (codigo) DO NOTHING;

-- Admin inicial (password: admin123 - hasheado con BCrypt)
INSERT INTO usuarios (sucursal_id, rol_id, nombre_completo, email, password_hash, estado)
SELECT
    (SELECT sucursal_id FROM sucursales WHERE codigo = 'MAT001'),
    (SELECT rol_id FROM roles WHERE nombre_rol = 'ADMIN'),
    'Administrador Principal',
    'admin@powerfit.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ACTIVO'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@powerfit.com');

-- Categorías de equipo iniciales
INSERT INTO categorias_equipo (nombre, descripcion) VALUES
    ('Cardio', 'Equipos de entrenamiento cardiovascular'),
    ('Fuerza', 'Equipos de entrenamiento de fuerza'),
    ('Funcional', 'Equipos de entrenamiento funcional'),
    ('Libre', 'Pesas y mancuernas libres')
ON CONFLICT (nombre) DO NOTHING;
