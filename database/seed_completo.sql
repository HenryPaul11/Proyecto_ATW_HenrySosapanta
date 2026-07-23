-- =============================================
-- PowerFit - Seed Completo (~1.5M registros)
-- Ejecutar después de schema.sql
-- =============================================

-- Limpiar datos dependientes primero
DELETE FROM sesiones;
DELETE FROM pagos;
DELETE FROM membresias;
DELETE FROM clientes;

-- Verificar estado actual
SELECT 'ANTES' AS momento, 'sucursales' AS tabla, COUNT(*) AS total FROM sucursales
UNION ALL SELECT 'ANTES', 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'ANTES', 'membresias', COUNT(*) FROM membresias
UNION ALL SELECT 'ANTES', 'pagos', COUNT(*) FROM pagos
UNION ALL SELECT 'ANTES', 'entrenadores', COUNT(*) FROM entrenadores
UNION ALL SELECT 'ANTES', 'equipos', COUNT(*) FROM equipos;

-- =============================================
-- 1. SUCURSALES - Ya existen
-- =============================================

-- =============================================
-- 2. PLANES - Ya existen, no insertar
-- =============================================

-- =============================================
-- 3. ENTRENADORES (~216 = 8 por sucursal)
-- =============================================
WITH
fn AS (SELECT ARRAY['Carlos','Miguel','Andres','Pedro','Luis','Jorge','Roberto','Fernando',
  'Diego','Sergio','Ricardo','Hector','Raul','Oscar','Martin','Pablo','Alberto',
  'Eduardo','Rafael','Javier','Felipe','Tomas','Adrian','Bruno','Marco','Luca',
  'Antoine','Pierre','Jean','Louis','Hans','Stefan','Karl','Thomas','Oliver',
  'James','Michael','David','Robert','William','Richard','Daniel','Andrew',
  'Omar','Ahmad','Hassan','Karim','Tariq','Kaito','Kenji','Ryo','Yuki',
  'Minjun','Taehyun','Arjun','Rahul','Kwame','Kofi'] AS a),
ln AS (SELECT ARRAY['Garcia','Lopez','Martinez','Rodriguez','Hernandez','Fernandez',
  'Gonzalez','Perez','Sanchez','Ramirez','Torres','Flores','Rivera','Gomez',
  'Diaz','Cruz','Morales','Reyes','Ortiz','Gutierrez','Ramos','Ruiz',
  'Alvarez','Mendoza','Castillo','Vargas','Romero','Herrera','Medina','Aguilar',
  'Rossi','Russo','Ferrari','Bianchi','Romano','Colombo','Ricci','Marino',
  'Martin','Bernard','Dubois','Moreau','Laurent','Lefevre','Michel','Durand',
  'Muller','Schmidt','Schneider','Fischer','Weber','Meyer','Wagner','Becker',
  'Silva','Santos','Souza','Oliveira','Pereira','Costa','Rodrigues','Almeida',
  'Tanaka','Suzuki','Watanabe','Nakamura','Kim','Lee','Park','Choi',
  'Sharma','Patel','Kumar','Singh','Mensah','Okafor','Diallo','Traore'] AS a),
esp AS (SELECT ARRAY['Fuerza','Cardio','Funcional','Yoga','Pilates','CrossFit','Natacion','Boxeo'] AS a)
INSERT INTO entrenadores (sucursal_id, nombre_completo, documento_identidad, especialidad, telefono, email, estado, fecha_contratacion, fecha_creacion, fecha_actualizacion)
SELECT
    (SELECT sucursal_id FROM sucursales ORDER BY sucursal_id OFFSET (i % 27) LIMIT 1),
    fa.a[1 + (i % array_length(fa.a,1))] || ' ' || la.a[1 + ((i / array_length(fa.a,1)) % array_length(la.a,1))],
    'ENT-' || lpad((i+1)::text, 4, '0'),
    ea.a[1 + (i % array_length(ea.a,1))],
    '+5939' || lpad((70000000 + i * 13579)::text, 8, '0'),
    lower(fa.a[1 + (i % array_length(fa.a,1))]) || '.' || lower(la.a[1 + ((i / array_length(fa.a,1)) % array_length(la.a,1))]) || i || '@powerfit.com',
    'ACTIVO'::estado_general,
    ('2022-01-01'::date + (i * 5))::date,
    NOW(),
    NOW()
FROM generate_series(0, 215) AS i, fn fa, ln la, esp ea
ON CONFLICT (documento_identidad) DO NOTHING;

-- =============================================
-- 4. CLIENTES (~500K) - Cédula EC, emails únicos, contraseña nombre123
-- =============================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

WITH
fn AS (SELECT ARRAY[
  'Juan','Maria','Carlos','Ana','Pedro','Laura','Luis','Carmen','Miguel','Isabel',
  'Antonio','Rosa','Francisco','Pilar','Jose','Marta','Manuel','Lucia','Alejandro','Patricia',
  'Fernando','Sara','Andres','Elena','Diego','Claudia','Javier','Cristina','Rafael','Diana',
  'Enrique','Veronica','Alberto','Raquel','Eduardo','Silvia','Pablo','Monica','Sergio','Nuria',
  'Adrian','Andrea','Oscar','Irene','Roberto','Teresa','Jorge','Victor','Lydia','Hector',
  'James','Sarah','Michael','Emma','David','Olivia','Robert','Emily','William','Isabella',
  'Richard','Sophia','Thomas','Ava','Daniel','Mia','Matthew','Charlotte','Christopher','Amelia',
  'Andrew','Joshua','Ryan','Nathan','Samuel','Alexander','Benjamin','Henry','Sebastian','Jack',
  'Owen','Luke','Levi','Isaac','Elijah','Gabriel','Anthony','Dylan','Leo','Julian',
  'Marco','Sofia','Luca','Giulia','Alessandro','Francesca','Matteo','Valentina','Lorenzo','Chiara',
  'Filippo','Camilla','Leonardo','Gaia','Giovanni','Riccardo','Alessia','Davide','Aurora','Federico',
  'Simone','Ginevra','Alessio','Beatrice','Tommaso',
  'Pierre','Antoine','Claire','Louis','Camille','Nicolas','Lea','Philippe','Manon','Sebastien',
  'Juliette','Laurent','Ines','Olivier','Elise','Chloe','Patrick','Jade','Alain','Pauline',
  'Hans','Anna','Karl','Eva','Petra','Stefan','Sabine','Wolfgang','Claudia','Jurgen',
  'Monika','Dieter','Brigitte','Frank','Peter','Kerstin','Andreas','Susanne','Martin','Verena',
  'Joao','Ricardo','Ines','Manuel','Beatriz','Luis','Andre','Joana','Bruno','Mariana',
  'Tiago','Vasco','Leonor','Afonso','Renato',
  'Omar','Fatima','Ali','Leila','Ahmad','Yasmin','Nadia','Salma','Layla','Farid',
  'Amira','Samir','Hana','Rami','Nour',
  'Yuki','Kenji','Sakura','Hiroshi','Akiko','Takeshi','Mika','Ryo','Haruki','Mei',
  'Sota','Yui','Takumi','Hana','Kaito',
  'Minjun','Seoyun','Jiwon','Taehyun','Eunji','Sungmin','Hayoon','Dowoo','Soojin','Jaeho',
  'Arjun','Priya','Rahul','Ananya','Vikram','Deepa','Rohan','Meera','Aditya','Kavya',
  'Kwame','Ama','Kofi','Aisha','Tendai','Nia','Amara','Jabari','Zuri','Chinua'
] AS a),
ln AS (SELECT ARRAY[
  'Garcia','Lopez','Martinez','Rodriguez','Hernandez','Fernandez','Gonzalez','Perez',
  'Sanchez','Ramirez','Torres','Flores','Rivera','Gomez','Diaz','Cruz','Morales','Reyes',
  'Ortiz','Gutierrez','Chavez','Ramos','Ruiz','Alvarez','Mendoza','Castillo','Vargas',
  'Romero','Herrera','Medina','Aguilar','Vega','Castro','Jimenez','Navarro','Dominguez',
  'Munoz','Santos','Alonso','Rubio','Gallego','Marin','Leon','Serrano','Iglesias',
  'Cordoba','Nunez','Cabrera','Prieto','Vidal','Molina','Campos','Ferrer','Pena',
  'Lorenzo','Hidalgo','Arias','Montero','Lara','Rey','Cortes','Delgado','Bravo','Leal',
  'Salazar','Barrera','Ibarra','Acosta','Paredes','Suarez','Valencia','Carrillo','Rangel','Ospina',
  'Smith','Johnson','Williams','Brown','Jones','Davis','Miller','Wilson','Moore','Taylor',
  'Anderson','Thomas','Jackson','White','Harris','Martin','Thompson','Robinson','Clark','Lewis',
  'Lee','Walker','Hall','Allen','Young','King','Wright','Scott','Green','Baker',
  'Adams','Nelson','Hill','Campbell','Mitchell','Roberts','Carter','Phillips','Evans','Turner',
  'Parker','Collins','Edwards','Stewart','Morris','Murphy','Reed','Cook','Morgan','Bell',
  'Rossi','Russo','Ferrari','Esposito','Bianchi','Romano','Colombo','Ricci','Marino','Greco',
  'Bruno','Gallo','Conti','De Luca','Mancini','Costa','Giordano','Rizzo','Lombardi','Moretti',
  'Barbieri','Fontana','Caruso','Messori','Rinaldi','Lombardi','Pellegrini','Costa','Rizzo','Moretti',
  'Martin','Bernard','Dubois','Moreau','Laurent','Lefevre','Michel','Lefebvre','Antoine','Robert',
  'Richard','Durand','Leroy','Mercier','Blanc','Guerin','Boyer','Garnier','Chevalier','Francois',
  'Legrand','Gauthier','Perrin','Robin','Clement','Girard','Bonnet','Schmitt','Bonnet','Clement',
  'Muller','Schmidt','Schneider','Fischer','Weber','Meyer','Wagner','Becker','Hoffmann','Schaefer',
  'Koch','Bauer','Richter','Klein','Wolf','Neumann','Zimmermann','Braun','Krueger','Schulz',
  'Hoffman','Schulz','Lang','Fuchs','Hartmann','Koenig','Werner','Krause','Lehr','Huber',
  'Silva','Santos','Souza','Oliveira','Pereira','Costa','Rodrigues','Almeida','Nascimento','Lima',
  'Araujo','Fernandes','Carvalho','Gomes','Martins','Rocha','Ribeiro','Alves','Monteiro','Cardoso',
  'Barbosa','Nogueira','Teixeira','Lopes','Vieira',
  'Al-Rashid','Hassan','Ibrahim','Khalil','Mansour','Farah','Nasser','Saleh','Youssef','Hamdi',
  'Benali','Tazi','Fassi','Amrani','Berrada','Chakir','Ouazzani','Tahiri','Filali','Guedira',
  'Tanaka','Suzuki','Watanabe','Takahashi','Yamamoto','Nakamura','Kobayashi','Saito','Kato','Yoshida',
  'Yamada','Sasaki','Ito','Kimura','Hayashi','Shimizu','Matsumoto','Inoue','Kato','Yoshimoto',
  'Kim','Lee','Park','Choi','Jung','Kang','Cho','Yoon','Jang','Lim',
  'Han','Seo','Bae','Kwon','Hwang',
  'Sharma','Patel','Kumar','Singh','Reddy','Nair','Gupta','Desai','Mehta','Joshi',
  'Verma','Chauhan','Pandey','Saxena','Bhat',
  'Mensah','Okafor','Diallo','Traore','Moyo','Ndlovu','Adeyemi','Obi','Kamara','Sesay',
  'Toure','Diop','Ba','Sy','Cisse'
] AS a),
base AS (
  SELECT
    i,
    fa.a[1 + (i % array_length(fa.a,1))] AS nombre,
    la.a[1 + ((i / array_length(fa.a,1)) % array_length(la.a,1))] AS apellido,
    ROW_NUMBER() OVER (PARTITION BY fa.a[1 + (i % array_length(fa.a,1))] ORDER BY i) AS rn_por_nombre
  FROM generate_series(0, 499999) AS i, fn fa, ln la
)
INSERT INTO clientes (sucursal_id, nombre_completo, documento_identidad, email, telefono, password_hash, genero, estado, fecha_registro)
SELECT
    1 + (i % 27),
    nombre || ' ' || apellido,
    lpad((10000000 + i * 3)::text, 10, '0'),
    CASE WHEN rn_por_nombre = 1
         THEN lower(nombre) || '@powerfit.com'
         ELSE lower(nombre) || (rn_por_nombre - 1) || '@powerfit.com'
    END,
    '+593' || lpad((900000000 + i * 7)::text, 9, '0'),
-- Hash bcrypt pre-calculado de 'nombre123'
    '$2b$10$gQndCHPnJ8Txu1liSf8tm.h7GRP5/MAd73jQUuFIHQidLodwVuCca',
    (ARRAY['M','F','O','M','F'])[1 + (i % 5)],
    'ACTIVO'::estado_general,
    ('2025-06-01'::timestamp + ((i * 17) % 400)::int * INTERVAL '1 day')
FROM base;

-- =============================================
-- 5. MEMBRESIAS (~450K = 90% clientes)
-- =============================================
WITH clientes_s AS (
    SELECT cliente_id, sucursal_id,
           ROW_NUMBER() OVER (ORDER BY cliente_id) AS rn
    FROM clientes
),
planes_s AS (
    SELECT plan_id, duracion_dias,
           ROW_NUMBER() OVER (ORDER BY plan_id) AS pn
    FROM planes
)
INSERT INTO membresias (cliente_id, plan_id, sucursal_id, fecha_inicio, fecha_fin, estado, fecha_creacion, fecha_actualizacion)
SELECT
    c.cliente_id,
    p.plan_id,
    c.sucursal_id,
    (CURRENT_DATE - ((c.rn * 13) % 180)::int)::date,
    (CURRENT_DATE - ((c.rn * 13) % 180)::int + p.duracion_dias)::date,
    CASE WHEN (CURRENT_DATE - ((c.rn * 13) % 180)::int + p.duracion_dias) >= CURRENT_DATE THEN 'ACTIVA'::estado_membresia
         ELSE 'VENCIDA'::estado_membresia END,
    (CURRENT_DATE - ((c.rn * 13) % 180)::int)::timestamp + '10:00:00'::time,
    (CURRENT_DATE - ((c.rn * 13) % 180)::int)::timestamp + '10:00:00'::time
FROM clientes_s c
JOIN planes_s p ON p.pn = 1 + ((c.rn / 1000) % 3)
WHERE c.rn <= 450000;

-- =============================================
-- 6. PAGOS (~450K) - EFECTIVO, TARJETA, TRANSFERENCIA
-- =============================================
INSERT INTO pagos (membresia_id, cliente_id, sucursal_id, monto, metodo_pago, estado, referencia_transaccion, fecha_pago)
SELECT
    m.membresia_id,
    m.cliente_id,
    m.sucursal_id,
    pl.precio,
    (ARRAY['EFECTIVO'::metodo_pago, 'TARJETA'::metodo_pago, 'TRANSFERENCIA'::metodo_pago])[1 + (abs(hashtext(m.membresia_id::text)) % 3)],
    'COMPLETADO'::estado_pago,
    CASE (abs(hashtext(m.membresia_id::text)) % 3)
        WHEN 0 THEN NULL
        WHEN 1 THEN 'TAR-' || lpad((abs(hashtext(m.membresia_id::text)) % 999999)::text, 6, '0')
        WHEN 2 THEN 'TRF-' || lpad((abs(hashtext(m.membresia_id::text)) % 999999)::text, 6, '0')
    END,
    m.fecha_inicio::timestamp
        + ((abs(hashtext(m.membresia_id::text)) % 5))::int * INTERVAL '1 day'
        + ((8 + abs(hashtext(m.membresia_id::text)) % 12))::int * INTERVAL '1 hour'
        + ((abs(hashtext(m.membresia_id::text)) % 60))::int * INTERVAL '1 minute'
FROM membresias m
JOIN planes pl ON pl.plan_id = m.plan_id;

-- =============================================
-- 7. EQUIPOS (~300 = ~11 por sucursal)
-- =============================================
WITH eq_data AS (
    SELECT i,
        1 + (i % 27) AS suc_id,
        (ARRAY['Cardio','Fuerza','Funcional','Libre'])[1 + (i % 4)] AS cat,
        (ARRAY['Cinta de Correr','Bicicleta Estatica','Elliptical','Remo','Prensa de Pierna',
          'Cable Cross','Smith Machine','Leg Press','Lat Pulldown','Chest Press',
          'Shoulder Press','Hack Squat','Leg Curl','Leg Extension','Abs Crunch',
          'Rowing Machine','Air Bike','Rower','Functional Rig','Kettlebell Rack',
          'Battle Ropes','Plyo Box','TRX Station','Barbell Set','Dumbbell Rack'])[1 + (i % 25)] AS nombre,
        (ARRAY['Life Fitness','Technogym','Precor','Rogue','York','Hoist','Cybex','Star Trac','Body Solid','Matrix'])[1 + (i % 10)] AS marca
    FROM generate_series(0, 299) AS i
)
INSERT INTO equipos (sucursal_id, categoria_id, nombre, marca, modelo, numero_serie, estado, fecha_adquisicion, valor_adquisicion)
SELECT
    e.suc_id,
    (SELECT categoria_id FROM categorias_equipo WHERE nombre = e.cat),
    e.nombre,
    e.marca,
    'Modelo-' || (abs(hashtext(e.nombre || e.marca)) % 50 + 1),
    'EQ-' || lpad((i + 1)::text, 5, '0'),
    'OPERATIVO'::estado_equipo,
    ('2022-01-01'::date + ((i * 3) % 900))::date,
    (500 + (abs(hashtext(e.nombre)) % 9500))::numeric(10,2)
FROM eq_data e
ON CONFLICT (numero_serie) DO NOTHING;

-- =============================================
-- VERIFICAR RESULTADO FINAL
-- =============================================
SELECT 'DESPUES' AS momento, 'sucursales' AS tabla, COUNT(*) AS total FROM sucursales
UNION ALL SELECT 'DESPUES', 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'DESPUES', 'membresias', COUNT(*) FROM membresias
UNION ALL SELECT 'DESPUES', 'pagos', COUNT(*) FROM pagos
UNION ALL SELECT 'DESPUES', 'entrenadores', COUNT(*) FROM entrenadores
UNION ALL SELECT 'DESPUES', 'equipos', COUNT(*) FROM equipos
UNION ALL SELECT 'DESPUES', 'planes', COUNT(*) FROM planes;
