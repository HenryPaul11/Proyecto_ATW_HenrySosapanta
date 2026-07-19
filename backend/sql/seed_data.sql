-- ================================================================
-- PowerFit - Script de Generacion de ~1,000,000 de registros
-- Password para todos los usuarios creados: powerfit123
-- Ejecutar: psql -U powerfit_user -d sistema_gimnasio_pg -f seed_data.sql
-- ================================================================

BEGIN;

-- ================================================================
-- 1. LIMPIEZA (orden de dependencias)
-- ================================================================
DELETE FROM auditoria;
DELETE FROM notificaciones;
DELETE FROM pagos;
DELETE FROM sesiones;
DELETE FROM membresias;
DELETE FROM asignacion_rutinas;
DELETE FROM asistencia;
DELETE FROM rutina_ejercicios;
DELETE FROM rutinas;
DELETE FROM ejercicios;
DELETE FROM equipos;
DELETE FROM clientes;
DELETE FROM entrenadores;
DELETE FROM usuarios WHERE usuario_id != 1;
DELETE FROM planes;
DELETE FROM categorias_equipo;
DELETE FROM sucursales;

-- ================================================================
-- 2. SUCURSALES (24 provincias de Ecuador)
-- ================================================================
INSERT INTO sucursales (sucursal_id, nombre, codigo, direccion, ciudad, telefono, email_contacto, estado, fecha_apertura, fecha_creacion, fecha_actualizacion) OVERRIDING SYSTEM VALUE VALUES
(1,  'PowerFit Matriz - Santo Domingo', 'MATRIZ',    'Av. Abraham Calderon y 9 de Octubre',      'Santo Domingo',              '0202030400', 'matriz@powerfit.com',       'ACTIVO', '2020-01-15', NOW(), NOW()),
(2,  'PowerFit Azuay',                  'AZU-001',   'Av. Remigio Crespo 1-23',                  'Cuenca',                     '0701234567', 'cuenca@powerfit.com',      'ACTIVO', '2021-03-10', NOW(), NOW()),
(3,  'PowerFit Bolivar',                'BOL-002',   'Av. Universidad y Calle 10 de Agosto',      'Guaranda',                   '0302345678', 'guaranda@powerfit.com',    'ACTIVO', '2021-05-20', NOW(), NOW()),
(4,  'PowerFit Canar',                  'CAN-003',   'Av. Atahualpa y Solano',                   'Azogues',                    '0703456789', 'azogues@powerfit.com',     'ACTIVO', '2021-06-15', NOW(), NOW()),
(5,  'PowerFit Carchi',                 'CAR-004',   'Av. Constitucion y Sucre',                 'Tulcan',                     '0604567890', 'tulcan@powerfit.com',      'ACTIVO', '2021-07-01', NOW(), NOW()),
(6,  'PowerFit Chimborazo',             'CHI-005',   'Av. Baltazar y Mera',                      'Riobamba',                   '0305678901', 'riobamba@powerfit.com',    'ACTIVO', '2021-08-10', NOW(), NOW()),
(7,  'PowerFit Cotopaxi',               'COT-006',   'Av. Cevallos y Calle 5 de Junio',          'Latacunga',                  '0306789012', 'latacunga@powerfit.com',   'ACTIVO', '2021-09-05', NOW(), NOW()),
(8,  'PowerFit El Oro',                 'ELO-007',   'Av. Bolivar y 9 de Octubre',               'Machala',                    '0707890123', 'machala@powerfit.com',     'ACTIVO', '2021-10-12', NOW(), NOW()),
(9,  'PowerFit Esmeraldas',             'ESM-008',   'Av. Libertad y Calle 10 de Agosto',        'Esmeraldas',                 '0608901234', 'esmeraldas@powerfit.com',  'ACTIVO', '2021-11-20', NOW(), NOW()),
(10, 'PowerFit Galapagos',              'GAL-009',   'Av. Charles Darwin y Bolivar',              'Puerto Baquerizo Moreno',    '0509012345', 'galapagos@powerfit.com',   'ACTIVO', '2022-01-15', NOW(), NOW()),
(11, 'PowerFit Guayas',                 'GUA-010',   'Av. 9 de Octubre y Malecon 2000',          'Guayaquil',                  '0400123456', 'guayaquil@powerfit.com',   'ACTIVO', '2020-06-01', NOW(), NOW()),
(12, 'PowerFit Imbabura',               'IMA-011',   'Av. Costanera y Sucre',                    'Ibarra',                     '0601123457', 'ibarra@powerfit.com',      'ACTIVO', '2022-02-20', NOW(), NOW()),
(13, 'PowerFit Loja',                   'LOJ-012',   'Av. Cuenca y Mariscal Sucre',              'Loja',                       '0702134568', 'loja@powerfit.com',        'ACTIVO', '2022-03-10', NOW(), NOW()),
(14, 'PowerFit Los Rios',               'LOR-013',   'Av. 10 de Agosto y Calle Bolivar',         'Babahoyo',                   '0503145679', 'babahoyo@powerfit.com',    'ACTIVO', '2022-04-15', NOW(), NOW()),
(15, 'PowerFit Manabi',                 'MAN-014',   'Av. Amazonas y 9 de Octubre',              'Portoviejo',                 '0504156780', 'portoviejo@powerfit.com',  'ACTIVO', '2022-05-01', NOW(), NOW()),
(16, 'PowerFit Morona Santiago',        'MOR-015',   'Av. Macas y Calle 6 de Noviembre',         'Macas',                      '0705167891', 'macas@powerfit.com',       'ACTIVO', '2022-06-10', NOW(), NOW()),
(17, 'PowerFit Napo',                   'NAP-016',   'Av. Tena y Calle Oriente',                 'Tena',                       '0606178902', 'tena@powerfit.com',        'ACTIVO', '2022-07-20', NOW(), NOW()),
(18, 'PowerFit Orellana',               'ORE-017',   'Av. Francisco de Orellana',                'Puerto Francisco',           '0707189013', 'puertoFrancisco@powerfit.com', 'ACTIVO', '2022-08-15', NOW(), NOW()),
(19, 'PowerFit Pastaza',                'PAS-018',   'Av. Amazonas y Mera',                      'Puyo',                       '0608190124', 'puyo@powerfit.com',        'ACTIVO', '2022-09-01', NOW(), NOW()),
(20, 'PowerFit Pichincha',              'PIC-019',   'Av. Amazonas N36-156 y De los Shyris',      'Quito',                      '0209201235', 'quito@powerfit.com',       'ACTIVO', '2020-03-01', NOW(), NOW()),
(21, 'PowerFit Santa Elena',            'STE-020',   'Av. Libertad y Calle Principal',            'Santa Elena',                '0400212346', 'santaelena@powerfit.com',  'ACTIVO', '2022-10-20', NOW(), NOW()),
(22, 'PowerFit Santo Domingo',          'SDT-021',   'Av. Jose de Aviles y Thomas de Berlanga',   'Santo Domingo',              '0202223457', 'santodomingo@powerfit.com','ACTIVO', '2022-11-10', NOW(), NOW()),
(23, 'PowerFit Sucumbios',              'SUC-022',   'Av. Quito y Lago Agrio',                   'Nueva Loja',                 '0607234568', 'nuevaloja@powerfit.com',   'ACTIVO', '2022-12-01', NOW(), NOW()),
(24, 'PowerFit Tungurahua',             'TUN-023',   'Av. Cevallos y Salinas',                   'Ambato',                     '0303245679', 'ambato@powerfit.com',      'ACTIVO', '2021-04-01', NOW(), NOW()),
(25, 'PowerFit Zamora Chinchipe',       'ZAM-024',   'Av. Zamora y Calle Gran Colombia',          'Zamora',                     '0706256780', 'zamora@powerfit.com',      'ACTIVO', '2023-01-15', NOW(), NOW());

-- ================================================================
-- 3. CATEGORIAS DE EQUIPO (6)
-- ================================================================
INSERT INTO categorias_equipo (categoria_id, nombre, descripcion) OVERRIDING SYSTEM VALUE VALUES
(1, 'Cardio',         'Equipos de cardiovascular y resistencia'),
(2, 'Fuerza',         'Equipos de fuerza y pesas libres'),
(3, 'Funcional',      'Equipos de entrenamiento funcional'),
(4, 'Flexibilidad',   'Equipos de estiramiento y yoga'),
(5, 'Movilidad',      'Equipos de movilidad articular y recuperacion'),
(6, 'Rehabilitacion', 'Equipos de rehabilitacion fisica');

-- ================================================================
-- 4. PLANES (5 por sucursal = 120)
-- ================================================================
INSERT INTO planes (sucursal_id, nombre_plan, descripcion, duracion_dias, precio, estado, fecha_creacion, fecha_actualizacion)
SELECT
  s.sucursal_id,
  p.nombre_plan,
  p.descripcion,
  p.duracion_dias,
  p.precio,
  'ACTIVO'::estado_general,
  NOW(),
  NOW()
FROM sucursales s
CROSS JOIN (VALUES
  ('Mensual Basico',   'Acceso basico al gimnasio, sala de pesas y cardio',       30,  25.00),
  ('Mensual Plus',     'Acceso completo con clases grupales y sauna',             30,  40.00),
  ('Trimestral',       'Plan trimestral con 15% de descuento',                    90, 100.00),
  ('Semestral',        'Plan semestral premium con beneficios extras',           180, 180.00),
  ('Anual Premium',    'Acceso total ilimitado por un ano con todos los beneficios', 365, 300.00)
) AS p(nombre_plan, descripcion, duracion_dias, precio);

-- ================================================================
-- 5. USUARIOS ADMIN SUCURSAL (23, una por sucursal no-MATRIZ)
-- ================================================================
INSERT INTO usuarios (sucursal_id, rol_id, nombre_completo, email, password_hash, telefono, estado, fecha_creacion, fecha_actualizacion)
SELECT
  s.sucursal_id,
  2,
  'Admin ' || s.ciudad,
  'admin_' || LOWER(REPLACE(s.codigo, '-', '')) || '@powerfit.com',
  '$2b$10$voJkPzr1VuzTWxgaSWDoguFkN4Z7qsOdmSDPXQbVuqC.x3e1rnKXm',
  '09' || LPAD(((s.sucursal_id * 10000000 + 1234567) % 100000000)::text, 8, '0'),
  'ACTIVO'::estado_general,
  NOW(),
  NOW()
FROM sucursales s
WHERE s.codigo != 'MATRIZ';

-- ================================================================
-- 6. USUARIOS ENTRENADORES + ENTRENADORES (20 por sucursal = 480)
-- ================================================================
WITH t_nombres AS (
  SELECT ARRAY['Carlos','Jose','Luis','Miguel','Juan','Pedro','Francisco','Antonio','Manuel','Jorge',
               'Roberto','Fernando','Ricardo','Eduardo','Alberto','Raul','Diego','Sergio','Andres','Oscar',
               'Alejandro','Rafael','Enrique','Hector','Arturo','Gustavo','Adolfo','Felipe','Ruben','Victor',
               'Ernesto','Guillermo','Ramon','Tomas','Emilio','Saul','Ivan','Esteban','Mauricio','Damian',
               'Daniel','Gabriel','Mateo','Tobias','Sebastian','Adrian','Nicolas','Emiliano','Leonardo','Martin'] AS arr
),
t_apellidos AS (
  SELECT ARRAY['Morales','Vera','Cevallos','Yanez','Paredes','Santos','Cordova','Miranda',
               'Paz','Carrera','Bravo','Solis','Cruz','Mera','Tobar','Bustamante',
               'Jaramillo','Lasso','Rojas','Benitez','Acosta','Suarez','Ponce','Cisneros',
               'Delgado','Salazar','Herrera','Carabajo','Naranjo','Espinoza','Calle','Guallpa',
               'Bajaña','Mite','Quiroz','Guzman','Barrezueta','Tixi','Cayambe','Gualan'] AS arr
),
t_indices AS (
  SELECT
    s.sucursal_id,
    t.n,
    ((t.n - 1) % 50) + 1 AS nom_idx,
    ((t.n * 7 + 3) % 40) + 1 AS ape1_idx,
    ((t.n * 13 + 11) % 40) + 1 AS ape2_idx
  FROM sucursales s, generate_series(1, 20) AS t(n)
),
trainer_usuarios AS (
  INSERT INTO usuarios (sucursal_id, rol_id, nombre_completo, email, password_hash, telefono, estado, fecha_creacion, fecha_actualizacion)
  SELECT
    ti.sucursal_id,
    4,
    (SELECT arr[ti.nom_idx] FROM t_nombres) || ' ' || (SELECT arr[ti.ape1_idx] FROM t_apellidos) || ' ' || (SELECT arr[ti.ape2_idx] FROM t_apellidos),
    'trainer_' || ti.sucursal_id || '_' || ti.n || '@powerfit.com',
    '$2b$10$voJkPzr1VuzTWxgaSWDoguFkN4Z7qsOdmSDPXQbVuqC.x3e1rnKXm',
    '09' || LPAD(((ti.sucursal_id * 100000 + ti.n * 37) % 100000000)::text, 8, '0'),
    'ACTIVO'::estado_general,
    NOW(),
    NOW()
  FROM t_indices ti
  ORDER BY ti.sucursal_id, ti.n
  RETURNING usuario_id, sucursal_id
)
INSERT INTO entrenadores (sucursal_id, usuario_id, nombre_completo, documento_identidad, email, telefono, especialidad, horario, fecha_contratacion, estado, fecha_creacion, fecha_actualizacion)
SELECT
  tu.sucursal_id,
  tu.usuario_id,
  (SELECT arr[((ROW_NUMBER() OVER (ORDER BY tu.usuario_id) - 1) % 50) + 1] FROM t_nombres) || ' ' ||
  (SELECT arr[((ROW_NUMBER() OVER (ORDER BY tu.usuario_id) * 7 + 3) % 40) + 1] FROM t_apellidos) || ' ' ||
  (SELECT arr[((ROW_NUMBER() OVER (ORDER BY tu.usuario_id) * 13 + 11) % 40) + 1] FROM t_apellidos),
  LPAD(tu.usuario_id::text, 10, '0'),
  'trainer_' || tu.sucursal_id || '_' || ROW_NUMBER() OVER (ORDER BY tu.usuario_id) || '@powerfit.com',
  '09' || LPAD(((tu.usuario_id * 37 + 999) % 100000000)::text, 8, '0'),
  (ARRAY['Fuerza y Condicionamiento', 'Cardio y Resistencia', 'Funcional', 'Yoga y Flexibilidad', 'Rehabilitacion', 'CrossFit', 'Pilates'])[(ROW_NUMBER() OVER (ORDER BY tu.usuario_id) % 7) + 1],
  (ARRAY['Lun-Vie 6:00-14:00', 'Lun-Vie 10:00-18:00', 'Lun-Vie 14:00-22:00', 'Sab-Dom 8:00-16:00', 'Lun-Sab 6:00-20:00'])[(ROW_NUMBER() OVER (ORDER BY tu.usuario_id) % 5) + 1],
  (CURRENT_DATE - (random() * 1800 + 180)::int),
  'ACTIVO'::estado_general,
  NOW(),
  NOW()
FROM trainer_usuarios tu;

-- ================================================================
-- 7. CLIENTES (250,000)
-- ================================================================
WITH nombres_m AS (
  SELECT ARRAY['Carlos','Jose','Luis','Miguel','Juan','Pedro','Francisco','Antonio','Manuel','Jorge',
               'Roberto','Fernando','Ricardo','Eduardo','Alberto','Raul','Diego','Sergio','Andres','Oscar',
               'Alejandro','Rafael','Enrique','Hector','Arturo','Gustavo','Adolfo','Felipe','Ruben','Victor',
               'Ernesto','Guillermo','Ramon','Tomas','Emilio','Saul','Ivan','Esteban','Mauricio','Damian',
               'Daniel','Gabriel','Mateo','Tobias','Sebastian','Adrian','Nicolas','Emiliano','Leonardo','Martin',
               'Pablo','Santiago','Mateo','Agustin','Bruno','Lucas','Thiago','Isaac','Benjamin','Camilo',
               'Andres','Fabricio','Henry','Alexis','Dylan','Axel','IAN','Lautaro','Valentino','Milagros',
               'Cristian','Mauricio','Jhon','Brayan','Jean','Kevin','Michael','Brian','Anthony','Joshua',
               'Elias','Samuel','Joel','Aaron','Gonzalo','Hugo','Alvaro','Iker','Oliver','Eric'] AS arr
),
nombres_f AS (
  SELECT ARRAY['Maria','Ana','Carmen','Rosa','Laura','Patricia','Sandra','Gloria','Claudia','Monica',
               'Teresa','Veronica','Elizabeth','Gabriela','Adriana','Natalia','Paula','Vanessa','Diana','Cristina',
               'Silvia','Daniela','Karina','Lucia','Andrea','Carolina','Valeria','Paola','Rocio','Mariana',
               'Alexandra','Irene','Tatiana','Cecilia','Ximena','Romina','Fernanda','Liliana','Camila','Johana',
               'Sofia','Isabella','Valentina','Mariana','Luciana','Gabriela','Elena','Victoria','Martina','Renata',
               'Antonella','Micaela','Alejandra','Lorena','Diana','Catalina','Juliana','Natalia','Anita','Priscila',
               'Rosa','Luisa','Fernanda','Constanza','Javiera','Florencia','Brenda','Daniela','Monica','Tatiana',
               'Gladys','Milagros','Yolanda','Zulema','Nancy','Olga','Eugenia','Iris','Janet','Lidia'] AS arr
),
apellidos AS (
  SELECT ARRAY['Garcia','Lopez','Rodriguez','Martinez','Hernandez','Gonzalez','Perez','Sanchez',
               'Ramirez','Torres','Flores','Rivera','Gomez','Diaz','Cruz','Morales',
               'Reyes','Ortiz','Gutierrez','Chavez','Vargas','Castillo','Ramos','Jimenez',
               'Ruiz','Alvarez','Mendoza','Aguilar','Vasquez','Romero','Vera','Cevallos',
               'Paredes','Santos','Cordova','Miranda','Paz','Carrera','Bravo','Solis',
               'Mera','Tobar','Bustamante','Jaramillo','Lasso','Rojas','Benitez','Acosta',
               'Suarez','Ponce','Cisneros','Delgado','Salazar','Herrera','Naranjo','Espinoza',
               'Calle','Guallpa','Bajaña','Quiroz','Guzman','Barrezueta'] AS arr
),
-- Use hash-like combination to maximize uniqueness
cliente_data AS (
  SELECT
    i,
    ((i - 1) % 24) + 1 AS sucursal_id,
    ((i * 7 + 13) % 80) + 1 AS nom_idx,
    ((i * 11 + 5) % 80) + 1 AS nom2_idx,
    ((i * 3 + 17) % 60) + 1 AS ape1_idx,
    ((i * 19 + 23) % 60) + 1 AS ape2_idx,
    ((i * 29 + 7) % 60) + 1 AS ape3_idx
  FROM generate_series(1, 250000) AS i
)
INSERT INTO clientes (sucursal_id, nombre_completo, documento_identidad, email, telefono, genero, estado, fecha_registro, fecha_actualizacion)
SELECT
  cd.sucursal_id,
  CASE WHEN i % 2 = 0 THEN
    (SELECT arr[cd.nom_idx] FROM nombres_f)
  ELSE
    (SELECT arr[cd.nom_idx] FROM nombres_m)
  END || ' ' ||
  CASE WHEN i % 5 = 0 THEN
    CASE WHEN i % 2 = 0 THEN
      (SELECT arr[cd.nom2_idx] FROM nombres_f)
    ELSE
      (SELECT arr[cd.nom2_idx] FROM nombres_m)
    END || ' '
  ELSE
    ''
  END ||
  (SELECT arr[cd.ape1_idx] FROM apellidos) || ' ' ||
  (SELECT arr[cd.ape2_idx] FROM apellidos) AS nombre_completo,
  LPAD((((i - 1) / 24 + 1) + ((i - 1) % 24) * 1000000)::text, 10, '0') AS documento_identidad,
  'cliente' || i || '@powerfit.com' AS email,
  '09' || LPAD(((i * 7 + 13) % 100000000)::text, 8, '0') AS telefono,
  CASE WHEN i % 2 = 0 THEN 'Femenino' ELSE 'Masculino' END AS genero,
  CASE WHEN (i % 100) < 87 THEN 'ACTIVO'::estado_general ELSE 'INACTIVO'::estado_general END AS estado,
  NOW() - (random() * interval '730 days'),
  NOW()
FROM cliente_data cd;

-- ================================================================
-- 8. EQUIPOS (15 por sucursal = 360)
-- ================================================================
WITH eq_data AS (
  SELECT
    s.sucursal_id,
    s.codigo,
    t.n,
    ((t.n - 1) % 6) + 1 AS cat_offset
  FROM sucursales s, generate_series(1, 15) AS t(n)
),
eq_names AS (
  SELECT ARRAY[
    'Cinta de Correr Pro', 'Bicicleta Spinning', 'Eliptica Total', 'Remo Ergometrico', 'Escaladora Titan',
    'Banco Multiusuario', 'Multipower Smith', 'Prensa Cuadriceps', 'Jalcon al Pecho', 'Extension de Piernas',
    'Kettlebell 16kg', 'Battle Ropes 12m', 'Kit TRX Professional', 'Medicine Ball 8kg', 'Caja Pliometrica',
    'Esterilla Premium', 'Set Bandas Elasticas', 'Rodillo Foam 45cm', 'Rueda Abdominal', 'Bloque Yoga Cork',
    'Foam Roller Largo', 'Bola Lacrosse 9cm', 'Polea Articulada', 'Disco Balance 33cm', 'Set Mini Bands',
    'Bola Suiza 55cm', 'Tubo Resistencia Set', 'Bosu Pro Equilibrium', 'Aro Pilates 35cm', 'Pelota Fisio 25cm'
  ] AS arr
)
INSERT INTO equipos (sucursal_id, categoria_id, nombre, marca, modelo, numero_serie, fecha_adquisicion, valor_adquisicion, estado, imagen_url, descripcion, fecha_actualizacion)
SELECT
  ed.sucursal_id,
  ed.cat_offset,
  (SELECT arr[ed.n] FROM eq_names),
  (ARRAY['Life Fitness', 'Technogym', 'Precor', 'Rogue', 'Star Trac', 'Cybex', 'Hoist', 'Matrix'])[((ed.n) % 8) + 1],
  'Modelo-' || LPAD(ed.n::text, 3, '0'),
  'PF-' || ed.codigo || '-EQ-' || LPAD(ed.n::text, 3, '0'),
  CURRENT_DATE - (random() * 1800)::int,
  (random() * 9500 + 500)::numeric(10,2),
  (ARRAY['OPERATIVO', 'OPERATIVO', 'OPERATIVO', 'OPERATIVO', 'MANTENIMIENTO'])[((ed.n) % 5) + 1]::estado_equipo,
  NULL,
  'Equipo de entrenamiento para ' || (ARRAY['cardiovascular', 'fuerza', 'funcional', 'flexibilidad', 'movilidad', 'rehabilitacion'])[ed.cat_offset],
  NOW()
FROM eq_data ed;

-- ================================================================
-- 9. MEMBRESIAS (~250,000 - una por cada cliente)
-- ================================================================
WITH plan_data AS (
  SELECT
    plan_id,
    sucursal_id,
    duracion_dias,
    precio,
    ROW_NUMBER() OVER (PARTITION BY sucursal_id ORDER BY plan_id) AS plan_num
  FROM planes
),
memb_base AS (
  SELECT
    c.cliente_id,
    c.sucursal_id,
    pd.plan_id,
    pd.duracion_dias,
    pd.precio,
    CURRENT_DATE - (30 + (random() * 700)::int) AS fecha_inicio_calc
  FROM clientes c
  JOIN plan_data pd ON pd.sucursal_id = c.sucursal_id
    AND pd.plan_num = ((c.cliente_id - 1) % 5) + 1
)
INSERT INTO membresias (cliente_id, plan_id, sucursal_id, fecha_inicio, fecha_fin, estado, fecha_creacion, fecha_actualizacion)
SELECT
  cliente_id,
  plan_id,
  sucursal_id,
  fecha_inicio_calc,
  fecha_inicio_calc + duracion_dias,
  CASE
    WHEN fecha_inicio_calc + duracion_dias > CURRENT_DATE THEN
      CASE WHEN random() < 0.92 THEN 'ACTIVA'::estado_membresia ELSE 'SUSPENDIDA'::estado_membresia END
    ELSE
      CASE WHEN random() < 0.82 THEN 'VENCIDA'::estado_membresia ELSE 'CANCELADA'::estado_membresia END
  END,
  NOW() - (random() * interval '730 days'),
  NOW()
FROM memb_base;

-- ================================================================
-- 10. PAGOS (~250,000 - uno por membresia)
-- ================================================================
WITH mem_con_precio AS (
  SELECT
    m.membresia_id,
    m.cliente_id,
    m.sucursal_id,
    m.fecha_inicio,
    m.fecha_creacion,
    p.precio AS monto_base
  FROM membresias m
  JOIN planes p ON p.plan_id = m.plan_id
)
INSERT INTO pagos (membresia_id, cliente_id, sucursal_id, monto, metodo_pago, estado, referencia_transaccion, fecha_pago)
SELECT
  mc.membresia_id,
  mc.cliente_id,
  mc.sucursal_id,
  CASE
    WHEN random() < 0.85 THEN mc.monto_base
    ELSE (mc.monto_base * (0.8 + random() * 0.4))::numeric(10,2)
  END,
  (ARRAY['EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'PAGO_MOVIL'])[((mc.membresia_id % 4) + 1)]::metodo_pago,
  CASE
    WHEN random() < 0.88 THEN 'COMPLETADO'::estado_pago
    WHEN random() < 0.60 THEN 'PENDIENTE'::estado_pago
    WHEN random() < 0.80 THEN 'ANULADO'::estado_pago
    ELSE 'RECHAZADO'::estado_pago
  END,
  'REF-' || mc.membresia_id || '-' || EXTRACT(EPOCH FROM mc.fecha_creacion)::bigint,
  mc.fecha_creacion + (random() * interval '3 days')
FROM mem_con_precio mc;

-- ================================================================
-- 11. SESIONES (~250,000)
-- ================================================================
WITH ses_base AS (
  SELECT
    i,
    ((i - 1) % 24) + 1 AS sucursal_id,
    ((i - 1) % 20) + 1 AS trainer_local
  FROM generate_series(1, 250000) AS i
),
entrenadores_con_sucursal AS (
  SELECT
    e.entrenador_id,
    e.sucursal_id,
    ROW_NUMBER() OVER (PARTITION BY e.sucursal_id ORDER BY e.entrenador_id) AS rn_in_sucursal
  FROM entrenadores e
),
clientes_con_sucursal AS (
  SELECT
    c.cliente_id,
    c.sucursal_id,
    ROW_NUMBER() OVER (PARTITION BY c.sucursal_id ORDER BY c.cliente_id) AS rn_in_sucursal
  FROM clientes c
  WHERE c.estado = 'ACTIVO'
),
clientes_por_sucursal AS (
  SELECT COUNT(*) / 24 AS total FROM clientes WHERE estado = 'ACTIVO'
)
INSERT INTO sesiones (sucursal_id, entrenador_id, cliente_id, nombre, tipo, fecha, hora_inicio, hora_fin, cupo_maximo, estado)
SELECT
  sb.sucursal_id,
  ec.entrenador_id,
  CASE WHEN random() < 0.75 THEN cc.cliente_id ELSE NULL END,
  (ARRAY['Clase de Cardio', 'Sesion de Fuerza', 'Entrenamiento Funcional', 'Clase de Flexibilidad',
          'CrossFit Basico', 'Clase de Spinning', 'Sesion Personal', 'Clase Grupal',
          'Entrenamiento HIIT', 'Pilates Matutino', 'Yoga Vespertino', 'Circuito Total'])[((sb.i % 12) + 1)],
  (ARRAY['CLASE_GRUPAL'::tipo_sesion, 'CLASE_GRUPAL'::tipo_sesion, 'ENTRENAMIENTO_PERSONAL'::tipo_sesion, 'LIBRE'::tipo_sesion])[((sb.i % 4) + 1)],
  CURRENT_DATE - (random() * 730)::int,
  (ARRAY['06:00'::time, '07:00'::time, '08:00'::time, '09:00'::time, '10:00'::time, '14:00'::time, '15:00'::time, '16:00'::time, '17:00'::time, '18:00'::time, '19:00'::time, '20:00'::time])[((sb.i % 12) + 1)],
  (ARRAY['07:00'::time, '08:00'::time, '09:00'::time, '10:00'::time, '11:00'::time, '15:00'::time, '16:00'::time, '17:00'::time, '18:00'::time, '19:00'::time, '20:00'::time, '21:00'::time])[((sb.i % 12) + 1)],
  (5 + (sb.i % 26))::smallint,
  'ACTIVO'::estado_general
FROM ses_base sb
JOIN entrenadores_con_sucursal ec ON ec.sucursal_id = sb.sucursal_id AND ec.rn_in_sucursal = sb.trainer_local
LEFT JOIN clientes_con_sucursal cc ON cc.sucursal_id = sb.sucursal_id
  AND cc.rn_in_sucursal = ((sb.i - 1) / 24) % (SELECT total FROM clientes_por_sucursal) + 1;

-- ================================================================
-- 12. NOTIFICACIONES (50,000)
-- ================================================================
WITH clientes_activos AS (
  SELECT cliente_id, sucursal_id,
    ROW_NUMBER() OVER (ORDER BY cliente_id) AS rn
  FROM clientes
  WHERE estado = 'ACTIVO'
)
INSERT INTO notificaciones (cliente_id, sucursal_id, titulo, mensaje, tipo, leido, fecha_creacion)
SELECT
  ca.cliente_id,
  ca.sucursal_id,
  (ARRAY['Recordatorio de pago', 'Membresia por vencer', 'Nueva rutina disponible',
          'Bienvenido a PowerFit', 'Pago confirmado', 'Sesion programada',
          'Promocion especial', 'Actualizacion de perfil'])[((ca.rn % 8) + 1)],
  (ARRAY[
    'Su pago ha sido procesado correctamente. Gracias por su preferencia.',
    'Su membresia vence en 5 dias. Visite su sucursal para renovar.',
    'Su entrenador ha creado una nueva rutina personalizada para usted.',
    'Bienvenido a PowerFit! Disfrute de nuestras instalaciones y servicios.',
    'Hemos recibido su pago de forma exitosa. Referencia: ' || ca.rn,
    'Tiene una sesion de entrenamiento programada para manana a las 10:00.',
    'Aproveche nuestra promocion: 20% de descuento en planes trimestrales.',
    'Sus datos de perfil han sido actualizados correctamente.'
  ])[((ca.rn % 8) + 1)],
  (ARRAY['PAGO', 'MEMBRESIA', 'RUTINA', 'SISTEMA', 'PAGO', 'SESION', 'MEMBRESIA', 'SISTEMA'])[((ca.rn % 8) + 1)]::tipo_notificacion,
  (ca.rn % 100) < 40,
  NOW() - (random() * interval '365 days')
FROM clientes_activos ca
WHERE ca.rn <= 50000;

-- ================================================================
-- 13. AUDITORIA (50,000)
-- ================================================================
INSERT INTO auditoria (usuario_id, sucursal_id, tabla_afectada, accion, registro_id, datos_anteriores, datos_nuevos, ip_origen, fecha_hora)
SELECT
  CASE WHEN (i % 10) < 3 THEN 1 ELSE NULL END,
  ((i - 1) % 24) + 1,
  (ARRAY['clientes', 'equipos', 'membresias', 'pagos', 'sesiones', 'entrenadores', 'usuarios', 'planes'])[((i % 8) + 1)],
  (ARRAY['INSERT', 'UPDATE', 'DELETE'])[((i % 3) + 1)]::accion_auditoria,
  (random() * 300000 + 1)::bigint,
  NULL,
  NULL,
  ('192.168.' || ((i / 256) % 256) || '.' || ((i % 254) + 1))::inet,
  NOW() - (random() * interval '365 days')
FROM generate_series(1, 50000) AS i;

COMMIT;

-- ================================================================
-- 14. RESET DE SECUENCIAS
-- ================================================================
SELECT setval('public.sucursales_sucursal_id_seq', (SELECT MAX(sucursal_id) FROM sucursales));
SELECT setval('public.categorias_equipo_categoria_id_seq', (SELECT MAX(categoria_id) FROM categorias_equipo));
SELECT setval('public.planes_plan_id_seq', (SELECT MAX(plan_id) FROM planes));
SELECT setval('public.usuarios_usuario_id_seq', (SELECT MAX(usuario_id) FROM usuarios));
SELECT setval('public.entrenadores_entrenador_id_seq', (SELECT MAX(entrenador_id) FROM entrenadores));
SELECT setval('public.clientes_cliente_id_seq', (SELECT MAX(cliente_id) FROM clientes));
SELECT setval('public.equipos_equipo_id_seq', (SELECT MAX(equipo_id) FROM equipos));
SELECT setval('public.membresias_membresia_id_seq', (SELECT MAX(membresia_id) FROM membresias));
SELECT setval('public.pagos_pago_id_seq', (SELECT MAX(pago_id) FROM pagos));
SELECT setval('public.sesiones_sesion_id_seq', (SELECT MAX(sesion_id) FROM sesiones));
SELECT setval('public.notificaciones_notificacion_id_seq', (SELECT MAX(notificacion_id) FROM notificaciones));
SELECT setval('public.auditoria_auditoria_id_seq', (SELECT MAX(auditoria_id) FROM auditoria));

-- ================================================================
-- RESUMEN FINAL
-- ================================================================
SELECT 'sucursales' AS tabla, COUNT(*) AS registros FROM sucursales
UNION ALL SELECT 'roles', COUNT(*) FROM roles
UNION ALL SELECT 'categorias_equipo', COUNT(*) FROM categorias_equipo
UNION ALL SELECT 'planes', COUNT(*) FROM planes
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'entrenadores', COUNT(*) FROM entrenadores
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'equipos', COUNT(*) FROM equipos
UNION ALL SELECT 'membresias', COUNT(*) FROM membresias
UNION ALL SELECT 'pagos', COUNT(*) FROM pagos
UNION ALL SELECT 'sesiones', COUNT(*) FROM sesiones
UNION ALL SELECT 'notificaciones', COUNT(*) FROM notificaciones
UNION ALL SELECT 'auditoria', COUNT(*) FROM auditoria
ORDER BY registros DESC;
