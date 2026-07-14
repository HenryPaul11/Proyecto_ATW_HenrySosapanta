# PowerFit - Sistema de Gestión para Gimnasio (Backend)

Este repositorio contiene la API REST para el **Sistema de Gestión de Gimnasio PowerFit**, desarrollada sobre el framework Java Spring Boot. Proporciona todas las funcionalidades backend para administrar clientes, entrenadores, membresías, pagos, inventario de equipos, sesiones de entrenamiento, auditoría automática de cambios y un dashboard administrativo.

---

## 🚀 Tecnologías Principales
- **Java 17** (JDK)
- **Spring Boot 3.2.5**
  - **Spring Web** (Desarrollo de API REST)
  - **Spring Data JPA** (Persistencia y ORM)
  - **Spring Validation** (Validación de solicitudes)
- **MySQL Connector J** (Driver de base de datos)
- **Lombok** (Generación automática de getters, setters, constructores y builders)
- **Springdoc OpenAPI (Swagger UI)** (Documentación interactiva y especificación de endpoints)
- **Maven** (Gestor de dependencias y construcción)

---

## 📋 Requisitos Previos
Para compilar y ejecutar este proyecto de forma local, necesitas tener instalado:
1. **Java JDK 17 o superior** (configurado en tus variables de entorno `JAVA_HOME`).
2. **MySQL Server** (versión 8.0 o superior). Puede usarse a través de WampServer, XAMPP o como un servicio local independiente.
3. **Maven 3.8+** (opcional, ya que el proyecto incluye el Maven Wrapper `./mvnw`).
4. Una herramienta para pruebas de API como **Postman**, **Insomnia**, o utilizar la interfaz interactiva de **Swagger UI**.

---

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio
Clona este repositorio remoto en tu máquina local:
```bash
git clone https://github.com/HenryPaul11/Proyecto_ATW_HenrySosapanta_BACKEND.git
cd Proyecto_ATW_HenrySosapanta_BACKEND
```

### 2. Configurar la Base de Datos
1. Inicia tu servidor MySQL (por ejemplo, a través de WampServer o XAMPP).
2. Abre tu cliente MySQL favorito (phpMyAdmin, DBeaver, MySQL Workbench, o desde la consola).
3. Importa o ejecuta el script de base de datos adjunto en la raíz del proyecto:
   [schema_sistema_gimnasio.sql](schema_sistema_gimnasio.sql)
   
   *Nota: El script crea la base de datos llamada `sistema_gimnasio`, define las 10 tablas con sus restricciones de integridad referencial (claves foráneas) e inserta un conjunto completo de datos semilla (usuarios de prueba, entrenadores, membresías y equipos).*

### 3. Configurar Variables de Entorno y Propiedades
El archivo de configuración principal se encuentra en [src/main/resources/application.properties](src/main/resources/application.properties). Las propiedades y variables clave configuradas son:

| Propiedad / Variable | Descripción | Valor por Defecto |
|---|---|---|
| `server.port` | Puerto en el que se expone el servidor local. | `8080` |
| `spring.datasource.url` | URL de conexión JDBC a MySQL. | `jdbc:mysql://localhost:3306/sistema_gimnasio` |
| `spring.datasource.username` | Usuario de la base de datos MySQL. | `root` |
| `spring.datasource.password` | Contraseña del usuario MySQL. | *(vacía)* |
| `spring.jpa.hibernate.ddl-auto`| Política de actualización de esquema de Hibernate. | `update` |
| `spring.jpa.show-sql` | Mostrar consultas SQL en la consola. | `true` |

Si tu base de datos local utiliza un usuario o contraseña diferente, edita esas líneas en el archivo `application.properties` antes de continuar.

---

## ⚙️ Instrucciones de Ejecución

### Ejecución en Modo Desarrollo
Para ejecutar la aplicación localmente mediante la terminal, usa los siguientes comandos en la raíz del proyecto:

**En Windows (PowerShell/cmd):**
```powershell
./mvnw spring-boot:run
```

**En Linux / macOS:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Una vez que la aplicación se inicie con éxito, podrás visualizar en la consola que el servidor Spring Boot está escuchando en el puerto `8080`.

### Construcción del Archivo Jar para Producción
Para compilar y empaquetar la aplicación en un único archivo executable `.jar`:
```bash
./mvnw clean package
```
El archivo compilado se generará en la ruta `target/powerfit-backend-1.0.0.jar` y se puede iniciar directamente con:
```bash
java -jar target/powerfit-backend-1.0.0.jar
```

---

## 📖 Documentación de Endpoints (API)

Toda la API REST está documentada interactivamente usando Swagger UI. Con la aplicación en ejecución, puedes ingresar a:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Documentación OpenAPI JSON:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Resumen de Endpoints Disponibles:

#### 🔐 Autenticación (`/api/auth`)
- `POST /api/auth/login` - Iniciar sesión (retorna token simple y datos de rol).
- `POST /api/auth/logout` - Cerrar sesión.

#### 👥 Clientes (`/api/clientes`)
- `GET /api/clientes` - Listar todos los clientes activos.
- `GET /api/clientes/paginado` - Búsqueda y listado paginado (parámetros: `page`, `size`, `busqueda`).
- `GET /api/clientes/{id}` - Buscar un cliente por ID.
- `GET /api/clientes/cedula/{cedula}` - Buscar cliente por su cédula de identidad.
- `GET /api/clientes/sin-membresia` - Listar clientes sin ninguna membresía activa actual.
- `POST /api/clientes` - Registrar un nuevo cliente.
- `PUT /api/clientes/{id}` - Actualizar los datos de un cliente existente.
- `DELETE /api/clientes/{id}` - Desactivar lógicamente a un cliente (baja lógica, `activo = false`).

#### 🏋️ Entrenadores (`/api/entrenadores`)
- `GET /api/entrenadores` - Listar todos los entrenadores.
- `GET /api/entrenadores/{id}` - Buscar un entrenador por ID.
- `GET /api/entrenadores/usuario/{usuarioId}` - Buscar entrenador por ID de usuario.
- `POST /api/entrenadores` - Registrar un nuevo entrenador.
- `PUT /api/entrenadores/{id}` - Actualizar datos del entrenador.
- `DELETE /api/entrenadores/{id}` - Eliminar un entrenador por ID.

#### 🏷️ Tipos de Membresía (`/api/tipos-membresia`)
- `GET /api/tipos-membresia` - Listar tipos de membresía activos.
- `GET /api/tipos-membresia/{id}` - Buscar tipo de membresía por ID.
- `POST /api/tipos-membresia` - Crear un tipo de membresía (ej. Mensual, Anual).
- `PUT /api/tipos-membresia/{id}` - Actualizar precios o detalles.
- `DELETE /api/tipos-membresia/{id}` - Desactivar lógicamente un tipo de membresía.

#### 💳 Membresías del Cliente (`/api/membresias`)
- `GET /api/membresias` - Listar todas las membresías asignadas.
- `GET /api/membresias/{id}` - Buscar membresía por ID.
- `POST /api/membresias` - Asignar/comprar una membresía para un cliente (calcula automáticamente las fechas de inicio y fin).
- `PUT /api/membresias/{id}` - Editar los datos de una membresía.
- `DELETE /api/membresias/{id}` - Eliminar una membresía.

#### 💰 Pagos (`/api/pagos`)
- `GET /api/pagos` - Listar el historial de todos los pagos registrados.
- `GET /api/pagos/{id}` - Buscar un pago específico por ID.
- `GET /api/pagos/cliente/{clienteId}` - Historial de pagos realizados por un cliente específico.
- `GET /api/pagos/estadisticas` - Estadísticas y sumatoria de ingresos agrupadas por método de pago.
- `POST /api/pagos` - Registrar un nuevo pago de membresía.

#### 🗓️ Sesiones de Entrenamiento (`/api/sesiones`)
- `GET /api/sesiones` - Listar todas las sesiones agendadas.
- `GET /api/sesiones/{id}` - Buscar sesión por ID.
- `POST /api/sesiones` - Agendar una nueva sesión (cliente y entrenador).
- `PUT /api/sesiones/{id}` - Actualizar el estado (`pendiente`, `completada`, `cancelada`) y notas de la sesión.
- `DELETE /api/sesiones/{id}` - Cancelar/eliminar sesión.

#### 🔧 Inventario de Equipos (`/api/equipos`)
- `GET /api/equipos` - Listar todos los equipos de gimnasio.
- `GET /api/equipos/{id}` - Buscar equipo por ID.
- `POST /api/equipos` - Registrar un nuevo equipo.
- `PUT /api/equipos/{id}` - Modificar datos o cambiar estado del equipo (`disponible`, `mantenimiento`, `fuera_de_servicio`).
- `DELETE /api/equipos/{id}` - Eliminar un equipo del inventario.

#### 👤 Usuarios del Sistema (`/api/usuarios`)
- `GET /api/usuarios` - Listar los usuarios del sistema.
- `GET /api/usuarios/{id}` - Obtener detalles de un usuario por ID.
- `POST /api/usuarios` - Crear un nuevo usuario administrador, entrenador o cliente.
- `PUT /api/usuarios/{id}` - Actualizar datos del usuario.
- `DELETE /api/usuarios/{id}` - Eliminar cuenta de usuario.

#### 📊 Dashboard Administrativo (`/api/admin`)
- `GET /api/admin/stats` - Obtiene estadísticas consolidadas del dashboard (ej. total de clientes, membresías activas, entrenadores activos y recaudación total).

#### 🔍 Auditoría (`/api/auditoria`)
- `GET /api/auditoria` - Retorna el historial de auditoría automática de transacciones (INSERT/UPDATE/DELETE) del sistema, detallando el usuario ejecutor, la tabla modificada, la IP, y los datos anteriores/nuevos en formato JSON/texto.

---

## 📝 Ejemplos de Solicitudes JSON (Payloads)

A continuación se muestran ejemplos reales de solicitudes a la API para facilitar pruebas rápidas en Postman:

### 1. Iniciar Sesión (Autenticación)
- **Endpoint:** `POST /api/auth/login`
- **Request Body:**
```json
{
  "usuario": "admin",
  "contrasena": "admin123"
}
```
- **Response (Ejemplo exitoso):**
```json
{
  "status": "SUCCESS",
  "message": "Login exitoso",
  "data": {
    "id": 1,
    "usuario": "admin",
    "nombre": "Administrador General",
    "correo": "admin@powerfit.com",
    "rol": "admin",
    "token": "MTphZG1pbg=="
  }
}
```

### 2. Registrar un Nuevo Cliente
- **Endpoint:** `POST /api/clientes`
- **Request Body:**
```json
{
  "nombre": "Andrea",
  "apellido": "Salazar",
  "cedula": "1758493021",
  "telefono": "0998877665",
  "email": "andrea.salazar@outlook.com",
  "usuarioId": null
}
```

### 3. Asignar Membresía a Cliente
- **Endpoint:** `POST /api/membresias`
- **Request Body:**
```json
{
  "clienteId": 2,
  "tipoMembresiaId": 3
}
```
*(Nota: El sistema busca el Tipo de Membresía 3 "Semestral Full", calcula automáticamente la fecha de inicio como el día actual, y la fecha de fin sumándole los 180 días de duración).*

### 4. Registrar Pago de Membresía
- **Endpoint:** `POST /api/pagos`
- **Request Body:**
```json
{
  "membresiaId": 3,
  "clienteId": 2,
  "monto": 120.00,
  "metodoPago": "transferencia",
  "registradoPorId": 1
}
```

### 5. Agendar Sesión de Entrenamiento Personalizado
- **Endpoint:** `POST /api/sesiones`
- **Request Body:**
```json
{
  "entrenadorId": 1,
  "clienteId": 1,
  "fecha": "2026-06-25",
  "hora": "10:30:00",
  "tipo": "Fuerza / Hipertrofia",
  "notas": "Enfoque en tren superior. Traer hidratación."
}
```
