# PowerFit — Sistema de Gestión de Gimnasio

## Descripción de la idea

PowerFit es una aplicación web de administración para gimnasios. Permite gestionar clientes, membresías y pagos desde un panel centralizado, con registro de auditoría de todas las operaciones realizadas en el sistema.

## Problema que resuelve

Los gimnasios pequeños y medianos suelen llevar el control de sus clientes y cobros en hojas de cálculo o en papel, lo que genera errores, pérdida de información y dificultad para hacer seguimiento de membresías vencidas o pagos pendientes. PowerFit centraliza toda esa gestión en una interfaz web clara y accesible.

## Público objetivo

Administradores y recepcionistas de gimnasios que necesitan una herramienta sencilla para registrar clientes, asignar membresías, registrar cobros y consultar el historial de actividad del sistema.

## Framework seleccionado

**Vue 3** — seleccionado al inicio de la actividad por su curva de aprendizaje accesible, su sistema de componentes reactivos y su excelente integración con herramientas modernas como Vite y Pinia.

## Tecnologías utilizadas

| Tecnología | Versión | Rol |
|---|---|---|
| Vue 3 | ^3.5 | Framework frontend (Composition API) |
| Vite | ^8.0 | Bundler y servidor de desarrollo |
| Tailwind CSS | ^4.3 | Estilos utilitarios |
| Pinia | ^3.0 | Manejo de estado global |
| Vue Router | ^5.0 | Enrutamiento SPA |
| TypeScript | ~6.0 | Tipado estático |
| Axios | ^1.16 | Cliente HTTP |
| ESLint + Oxlint | — | Linting |
| Prettier | 3.8 | Formateo de código |

## Pasos de instalación y ejecución

### Requisitos previos

- Node.js `^20.19.0` o `>=22.12.0`
- npm

### Instalación

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd proyecto-ATW/frontend

# Instalar dependencias
npm install
```

### Ejecución en desarrollo

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`.

### Credenciales de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | 1234 | Administrador |
| juanperez | 1234 | Cliente |

### Build para producción

```bash
npm run build
```

### Verificación de tipos y lint

```bash
npm run type-check
npm run lint
```

## Estructura básica del proyecto

```
frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── assets/
│   │   └── main.css              # Estilos globales + Tailwind
│   ├── components/
│   │   ├── admin/
│   │   │   ├── ActionCard.vue    # Tarjeta de acción rápida
│   │   │   └── StatCard.vue      # Tarjeta de estadística
│   │   ├── auditoria/
│   │   │   ├── AccionBadge.vue   # Badge INSERT / UPDATE / DELETE
│   │   │   └── DatosAuditoria.vue# Visualizador de datos JSON
│   │   ├── login/
│   │   │   └── LoginForm.vue     # Formulario de autenticación
│   │   ├── membresias/
│   │   │   ├── ClienteInfoCard.vue
│   │   │   ├── MembresiaOption.vue
│   │   │   ├── MembresiaTable.vue
│   │   │   └── StatsCard.vue
│   │   ├── pagos/
│   │   │   ├── EstadoBadge.vue
│   │   │   ├── MembresiaCard.vue
│   │   │   ├── MetodoBadge.vue
│   │   │   ├── PagoStatCard.vue
│   │   │   └── StepIndicator.vue
│   │   └── shared/
│   │       ├── Footer.vue        # Pie de página global
│   │       └── Navbar.vue        # Barra de navegación global
│   ├── router/
│   │   └── index.ts              # Definición de rutas
│   ├── stores/
│   │   └── authStore.js          # Estado de autenticación (Pinia)
│   ├── views/
│   │   ├── AdminDashboard.vue    # Panel principal
│   │   ├── AsignarMembresia.vue  # Asignación de membresía a cliente
│   │   ├── Auditorias.vue        # Historial de auditorías
│   │   ├── Login.vue             # Pantalla de inicio de sesión
│   │   ├── Membresias.vue        # Gestión de membresías
│   │   ├── Pagos.vue             # Registro y listado de pagos
│   │   └── RegistrarPago.vue     # Flujo de registro de pago (stepper)
│   ├── App.vue                   # Componente raíz
│   └── main.ts                   # Punto de entrada
├── index.html
├── vite.config.ts
├── tsconfig.json
└── package.json
```

## Capturas de pantalla

### Login
![Login](./screenshots/login.png)

### Dashboard
![Dashboard](./screenshots/dashboard.png)

### Gestión de Membresías
![Membresías](./screenshots/membresias.png)

### Registro de Pagos
![Pagos](./screenshots/pagos.png)

### Auditorías
![Auditorías](./screenshots/auditorias.png)

> Las capturas se encuentran en la carpeta `screenshots/` en la raíz del repositorio.

## Integrantes y asignatura

| Nombre | Rol |
|---|---|
| [Nombre Integrante 1] | Frontend / Vue |
| [Nombre Integrante 2] | Backend / Base de datos |
| [Nombre Integrante 3] | Diseño / Documentación |

**Asignatura:** Aplicaciones y Tecnologías Web (ATW)

**Institución:** [Nombre de la institución]

**Período académico:** 2026
