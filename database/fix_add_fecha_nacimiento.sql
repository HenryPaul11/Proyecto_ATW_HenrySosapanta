-- Migracion: agregar columna fecha_nacimiento a la tabla clientes
-- Ejecutar si la columna no existe en la BD
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS fecha_nacimiento DATE;
