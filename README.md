# PowerFit - Sistema de Gestión de Gimnasio

Sistema web integral para la administración de centros de fitness con múltiples sucursales. Desarrollado como proyecto integrador de la asignatura de Arquitectura de Tecnologías Web.

## Características Principales

- **Gestión de Sucursales**: Administración de múltiples sedes con datos de contacto y ubicación
- **Control de Clientes**: Registro, edición y seguimiento de miembros del gimnasio
- **Entrenadores**: Asignación de entrenadores personales por sucursal y especialidad
- **Planes y Membresías**: Creación de planes de entrenamiento y control de vigencia de membresías
- **Pagos**: Registro de pagos con múltiples métodos y estadísticas financieras
- **Equipos**: Inventario de equipos deportivos por categoría con control de estado y transferencia entre sucursales
- **Sesiones**: Programación de clases grupales y entrenamiento personal
- **Auditoría**: Registro completo de acciones en el sistema con trazabilidad
- **Chat IA**: Asistente inteligente con acceso a datos reales de la base de datos
- **Dashboard**: Panel de métricas en tiempo real por rol de usuario

## Tecnologías

### Backend
- **Java 17** con **Spring Boot 3.4**
- **Spring Security** con autenticación JWT
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** como base de datos
- **Springdoc OpenAPI** (Swagger UI)
- **Lombok** para reducción de código boilerplate
- **Ollama** para IA local (modelo Qwen 2.5)

### Frontend
- **Vue 3** con Composition API
- **Pinia** para gestión de estado
- **Vue Router** con protección de rutas
- **Axios** para comunicación HTTP
- **Tailwind CSS v4** con modo oscuro
- **Vite** como bundler

## Estructura del Proyecto

```
proyecto-ATW/
├── backend/                    # API REST con Spring Boot
│   ├── src/main/java/com/powerfit/
│   │   ├── config/            # Configuración (CORS, OpenAPI, Security)
│   │   ├── controller/        # Controladores REST (14 endpoints)
│   │   ├── dto/               # Data Transfer Objects (26 DTOs)
│   │   ├── entity/            # Entidades JPA (12 entidades)
│   │   ├── exception/         # Excepciones personalizadas
│   │   ├── repository/        # Repositorios JPA
│   │   ├── security/          # Filtros JWT y utilidades
│   │   └── service/           # Lógica de negocio
│   └── src/main/resources/
│       └── application.properties
├── frontend/                   # SPA con Vue 3
│   └── src/
│       ├── assets/            # Estilos globales
│       ├── components/        # Componentes reutilizables
│       ├── composables/       # Composables (usePagination, useToast)
│       ├── router/            # Rutas con guards
│       ├── services/          # Servicios HTTP (Axios)
│       ├── stores/            # Stores Pinia
│       └── views/             # Vistas por rol
├── database/
│   └── schema.sql             # Script de creación de tablas
└── .env.example               # Variables de entorno de ejemplo
```

## Requisitos Previos

- **Java 17+**
- **Node.js 18+** con **npm**
- **PostgreSQL 14+**
- **Ollama** (opcional, para chat IA)

## Instalación

### 1. Base de datos

```bash
# Conectarse a PostgreSQL como superuser y ejecutar:
psql -U postgres -f database/schema.sql
```

### 2. Backend

```bash
cd backend

# Copiar y configurar variables de entorno
cp ../.env.example .env

# Compilar y ejecutar
./mvnw spring-boot:run
```

El backend estará disponible en `http://localhost:8081`

### 3. Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev
```

El frontend estará disponible en `http://localhost:5173`

### 4. IA (Opcional)

```bash
# Instalar Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Descargar modelo
ollama pull qwen2.5:0.5b
```

## Credenciales por Defecto

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin@powerfit.com | admin123 | Administrador |

## Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| ADMIN | Acceso total al sistema |
| ADMIN_SUCURSAL | Gestión de su sucursal asignada |
| ENTRENADOR | Gestión de sesiones y clientes asignados |
| CLIENTE | Consulta de información personal y sesiones |

## Documentación API

Una vez iniciado el backend, la documentación Swagger está disponible en:
```
http://localhost:8081/swagger-ui.html
```

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/entrenadores` | Listar entrenadores |
| GET | `/api/equipos` | Listar equipos |
| GET | `/api/membresias` | Listar membresías |
| GET | `/api/pagos` | Listar pagos |
| GET | `/api/sesiones` | Listar sesiones |
| GET | `/api/sucursales` | Listar sucursales |
| GET | `/api/planes` | Listar planes |
| POST | `/api/ia/chat` | Chat con IA |
