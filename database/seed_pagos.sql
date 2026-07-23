-- =============================================
-- PowerFit - Seed de Pagos
-- Ejecutar después de tener clientes y membresías
-- =============================================

-- 1) Contar registros existentes
SELECT 'sucursales' AS tabla, COUNT(*) AS total FROM sucursales
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes WHERE estado = 'ACTIVO'
UNION ALL SELECT 'membresias', COUNT(*) FROM membresias
UNION ALL SELECT 'membresias_activas', COUNT(*) FROM membresias WHERE estado = 'ACTIVA'
UNION ALL SELECT 'pagos', COUNT(*) FROM pagos
UNION ALL SELECT 'planes', COUNT(*) FROM planes WHERE estado = 'ACTIVO';

-- 2) Insertar pagos para todas las membresías que no tengan pago asociado
--    Solo usa EFECTIVO, TARJETA y TRANSFERENCIA
INSERT INTO pagos (membresia_id, cliente_id, sucursal_id, monto, metodo_pago, estado, referencia_transaccion, fecha_pago)
SELECT
    m.membresia_id,
    m.cliente_id,
    m.sucursal_id,
    p.precio AS monto,
    CASE (abs(hashtext(m.membresia_id::text)) % 3)
        WHEN 0 THEN 'EFECTIVO'::metodo_pago
        WHEN 1 THEN 'TARJETA'::metodo_pago
        WHEN 2 THEN 'TRANSFERENCIA'::metodo_pago
    END AS metodo_pago,
    'COMPLETADO'::estado_pago,
    CASE (abs(hashtext(m.membresia_id::text)) % 3)
        WHEN 0 THEN NULL
        WHEN 1 THEN 'TAR-' || lpad((abs(hashtext(m.membresia_id::text)) % 999999)::text, 6, '0')
        WHEN 2 THEN 'TRF-' || lpad((abs(hashtext(m.membresia_id::text)) % 999999)::text, 6, '0')
    END AS referencia_transaccion,
    m.fecha_inicio + interval '1 day' * (abs(hashtext(m.membresia_id::text)) % 5)
        + interval '1 hour' * (8 + (abs(hashtext(m.membresia_id::text)) % 12))
        + interval '1 minute' * (abs(hashtext(m.membresia_id::text)) % 60)
        AS fecha_pago
FROM membresias m
JOIN planes p ON p.plan_id = m.plan_id
WHERE NOT EXISTS (
    SELECT 1 FROM pagos pg WHERE pg.membresia_id = m.membresia_id
);

-- 3) Verificar cuántos pagos se insertaron
SELECT COUNT(*) AS pagos_insertados FROM pagos;
