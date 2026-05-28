# InmoHub - Cliente Multiplataforma (MVP)

Este proyecto contiene la interfaz de usuario y la lógica de cliente de **InmoHub**. Está desarrollado utilizando **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, lo que permite compartir la lógica de negocio, el consumo de red y el diseño visual entre plataformas usando una sola base de código en Kotlin.

Actualmente, este MVP cuenta con soporte funcional para dos entornos:

- **Android** — Aplicación móvil optimizada para clientes y agentes en movimiento.
- **Desktop (JVM)** — Aplicación de escritorio optimizada para la gestión en oficina.

## Roles de Usuario

La aplicación soporta cuatro roles, cada uno con funcionalidades específicas:

| Rol | Funcionalidades |
|---|---|
| **CLIENT** | Explorar catálogo de propiedades, buscar/filtrar, ver detalles, crear leads de interés. |
| **OWNER** | Todo lo de CLIENT, más: publicar propiedades, editar/eliminar publicaciones, ver leads de sus propiedades, carga masiva CSV (FSBO). |
| **AGENT** | Dashboard de gestión: listar clientes y propietarios, ver todas las propiedades, gestionar leads (bolsa global y asignados), cambiar estados. |
| **ADMIN** | Mismas funcionalidades que AGENT. |

> **Nota:** El registro como AGENT requiere un código secreto (`INMO-AGENT-2026`) definido del lado del cliente.

## Stack Tecnológico

| Categoría | Tecnología | Versión |
|---|---|---|
| **Lenguaje** | Kotlin | 2.2.21 |
| **UI Framework** | Compose Multiplatform (Material 3) | 1.9.3 |
| **Navegación** | [Voyager](https://voyager.adriel.cafe/) | 1.0.0 |
| **Networking** | Ktor Client | 3.0.0 |
| **Serialización** | Kotlinx Serialization (JSON) | — |
| **Autenticación** | JWT (decodificación manual) + Ktor Auth (Bearer + refresh automático) | — |
| **Imágenes** | [Coil](https://coil-kt.github.io/coil/) (con Coil Network Ktor) | 3.4.0 |
| **Almacenamiento local** | DataStore Preferences | 1.1.1 |
| **Hot Reload** | Compose Hot Reload | 1.0.0 |
| **Arquitectura** | Patrón Repository + expect/actual para abstracción de plataforma | — |
| **Android** | AGP 8.11.2 · compileSdk 36 · minSdk 24 · targetSdk 36 | — |
| **JVM** | Target JVM 11 · JDK 21 requerido para Gradle | — |
| **Testing** | Kotlin Test + Ktor Client Mock | — |

### Dependencias clave de Ktor

- `ktor-client-core` — Cliente HTTP base.
- `ktor-client-okhttp` — Engine HTTP para Android.
- `ktor-client-content-negotiation` — Negociación de contenido JSON.
- `ktor-client-auth` — Autenticación Bearer con refresh automático de tokens.
- `ktor-client-websockets` — Soporte para WebSockets.
- `ktor-client-logging` — Logging de peticiones HTTP.
- `ktor-serialization-kotlinx-json` — Serialización/deserialización JSON.

## Estructura del Proyecto

```
composeApp/
└── src/
    ├── commonMain/kotlin/com/inmohub/frontend/
    │   ├── App.kt                    # Punto de entrada compartido (MaterialTheme, inicialización, navegación raíz)
    │   ├── Platform.kt               # Interfaz expect para información de plataforma
    │   ├── core/
    │   │   ├── network/
    │   │   │   ├── NetworkClient.kt  # Cliente HTTP singleton con auth, refresh, y content negotiation
    │   │   │   └── PlatformConfig.kt # Configuración de URL base (expect/actual)
    │   │   ├── utils/
    │   │   │   └── JwtUtils.kt       # Decodificación de JWT (userId, roles, expiración)
    │   │   ├── themes/
    │   │   │   ├── Colors.kt         # Paleta de colores corporativa
    │   │   │   ├── Typography.kt     # Tipografía personalizada
    │   │   │   └── Theme.kt          # MaterialTheme con color scheme
    │   │   └── components/
    │   │       ├── InmoButton.kt     # Botón reutilizable (primario/secundario)
    │   │       ├── InmoInput.kt      # Campo de texto con estilos de la marca
    │   │       └── InmoAvatar.kt     # Avatar circular con iniciales
    │   └── features/
    │       ├── auth/
    │       │   ├── data/
    │       │   │   ├── AuthRepository.kt       # CRUD de autenticación y usuarios
    │       │   │   └── local/
    │       │   │       ├── SessionManager.kt    # Persistencia de tokens (DataStore)
    │       │   │       └── DataStorePlatform.kt # Factory de DataStore (expect/actual)
    │       │   ├── domain/
    │       │   │   └── User.kt                  # Modelo de usuario
    │       │   ├── requests/                    # DTOs de petición (Login, Register, RefreshToken, UpdateProfile)
    │       │   ├── responses/                   # DTOs de respuesta (LoginResponse, UserSummaryResponse)
    │       │   └── presentation/
    │       │       ├── LoginScreen.kt           # Pantalla de inicio de sesión
    │       │       ├── RegisterScreen.kt        # Pantalla de registro con selección de rol
    │       │       └── shared/
    │       │           └── ProfileScreen.kt     # Perfil de usuario (edición, eliminación de cuenta)
    │       ├── property/
    │       │   ├── data/
    │       │   │   └── PropertyRepository.kt   # CRUD completo de propiedades + carga CSV/imágenes
    │       │   ├── dtos/                        # DTOs (PropertyDto, PropertySummaryDto, CreateProperty, etc.)
    │       │   ├── responses/
    │       │   │   └── PagedListResponse.kt     # Respuesta paginada genérica
    │       │   └── presentation/
    │       │       ├── mobile/
    │       │       │   ├── HomeScreen.kt          # Catálogo con búsqueda paginada y filtros
    │       │       │   ├── PropertyDetailScreen.kt # Detalle con carrusel, features y leads
    │       │       │   └── components/
    │       │       │       └── LeadInterestCard.kt # Tarjeta de lead en detalle de propiedad
    │       │       └── shared/
    │       │           ├── PropertyCard.kt          # Tarjeta de propiedad reutilizable
    │       │           ├── OwnerPropertyCard.kt     # Tarjeta con acciones de propietario
    │       │           ├── FilterBottomSheet.kt     # Bottom sheet de filtros
    │       │           ├── CreatePropertyScreen.kt  # Formulario de creación de propiedad
    │       │           ├── EditPropertyScreen.kt    # Formulario de edición de propiedad
    │       │           ├── OwnerDashboardScreen.kt  # Panel de propietario (publicaciones + CSV)
    │       │           ├── FsboUploadTab.kt         # Pestaña de carga masiva CSV
    │       │           └── FilePicker.kt            # Selector de archivos (expect/actual)
    │       └── lead/
    │           ├── data/
    │           │   └── LeadRepository.kt     # CRUD de leads (crear, asignar, cambiar estado)
    │           ├── dtos/                      # DTOs (LeadSummaryDto, LeadDetailDto)
    │           ├── requests/                  # DTOs de petición (CreateLead, AssignLead, ChangeStatus)
    │           ├── responses/
    │           │   └── LeadAssignmentResponse.kt
    │           └── presentation/
    │               └── desktop/
    │                   ├── DashboardScreen.kt  # Dashboard multi-pestaña para agentes/admin
    │                   └── components/
    │                       ├── LeadCard.kt           # Tarjeta de lead con badge de estado
    │                       ├── LeadDetailDialog.kt   # Diálogo de detalle de lead
    │                       ├── LeadsBagTab.kt        # Pestaña de leads sin asignar
    │                       ├── MyLeadsTab.kt         # Pestaña de leads asignados al agente
    │                       └── StatusDropdown.kt     # Dropdown de cambio de estado
    ├── commonTest/                        # Tests compartidos (Kotlin Test + Ktor Mock)
    ├── androidMain/                       # Punto de entrada Android + implementaciones actual
    │   ├── MainActivity.kt               # Activity principal
    │   └── AndroidManifest.xml           # Permisos y configuración
    └── jvmMain/                           # Punto de entrada Desktop + implementaciones actual
        └── main.kt                        # Función main() para JVM
```

### Patrón expect/actual

La abstracción de plataforma se logra mediante el patrón `expect`/`actual` en las siguientes áreas:

| Interfaz | Android | Desktop (JVM) |
|---|---|---|
| `getBaseUrl()` | `http://192.168.1.36:8080/api/v1` | `http://localhost:8080/api/v1` |
| `createDataStore()` | `Context.filesDir` | `~/.inmohub/session.preferences_pb` |
| `pickCsvFile()` | `ActivityResultContracts.OpenDocument` | `JFileChooser` (Swing) |
| `pickImageFiles()` | `ActivityResultContracts.GetMultipleContents` | `JFileChooser` (Swing) |

## Funcionalidades Principales

### Autenticación

- Inicio de sesión con email y contraseña.
- Registro con selección de rol (CLIENT, OWNER, AGENT — este último requiere código secreto).
- Persistencia de sesión mediante tokens JWT almacenados en DataStore.
- Renovación automática de access token al recibir un 401.
- Perfil de usuario editable (nombre, apellido, teléfono).
- Eliminación de cuenta con confirmación explícita.

### Catálogo de Propiedades

- Listado paginado con scroll infinito.
- Búsqueda por ciudad, rango de precio y estado (disponible, vendida, alquilada).
- Tarjeta de propiedad con foto principal, precio, título y badge de estado.
- Detalle de propiedad con carrusel de imágenes, grid de características y descripción.

### Panel de Propietario

- Gestión de propiedades propias (crear, editar, eliminar).
- Visualización de leads recibidos por cada propiedad.
- Carga masiva de propiedades mediante archivo CSV (FSBO — *For Sale By Owner*).
- Subida de imágenes para cada propiedad (multipart).

### Dashboard de Agente/Admin

- **Clientes:** Listado de usuarios con rol CLIENT.
- **Propietarios:** Listado de usuarios con rol OWNER.
- **Propiedades:** Catálogo completo de todas las propiedades.
- **Bolsa de Leads:** Leads sin asignar disponibles para cualquier agente.
- **Mis Leads:** Leads asignados al agente actual, con cambio de estado (NUEVO → CONTACTADO → NEGOCIACIÓN → CERRADO/GANADO → PERDIDO).

### Gestión de Leads

- Creación de leads desde el detalle de una propiedad (clientes y visitantes).
- Asignación de leads a agentes desde la bolsa global.
- Seguimiento del ciclo de vida del lead con cambio de estado.
- Visualización de leads por propiedad (propietarios y agentes).

## Requisitos Previos

- **JDK 21** o superior (requerido por Gradle; el código compila a JVM target 11).
- **Android Studio** (recomendado) o IntelliJ IDEA con el plugin de Kotlin Multiplatform.
- Emulador de Android o dispositivo físico con API 24+ (para el target móvil).
- El **Backend de InmoHub** debe estar en ejecución para que las funcionalidades operen correctamente.

## Configuración del Backend

La URL base de la API se configura mediante el patrón `expect`/`actual` en `core/network/PlatformConfig.kt`:

- **Android:** `http://192.168.1.36:8080/api/v1` — Usa la IP local de tu red. Ajusta esta IP según tu entorno de desarrollo (si usas emulador, `10.0.2.2` apunta al `localhost` de la máquina host).
- **Desktop (JVM):** `http://localhost:8080/api/v1`

El backend debe exponer los siguientes endpoints (prefijo `/api/v1`):

| Recurso | Endpoints |
|---|---|
| `/auth/**` | Login, registro, refresh de token, perfil, gestión de usuarios |
| `/properties/**` | CRUD de propiedades, búsqueda, carga CSV, subida de imágenes |
| `/leads/**` | Creación, asignación, cambio de estado, listado por agente/propiedad |

## Ejecución del Proyecto

### Ejecutar en Desktop (JVM)

```bash
./gradlew :composeApp:run
```

### Generar Distribuibles Nativos de Escritorio

Genera instaladores nativos para macOS (`.dmg`), Windows (`.msi`) y Linux (`.deb`):

```bash
./gradlew :composeApp:createDistributable
```

Los archivos generados se encuentran en `composeApp/build/compose/binaries/`.

### Ejecutar en Android

Puedes usar la configuración de ejecución de Android Studio o compilar mediante la terminal:

**macOS / Linux:**
```bash
./gradlew :composeApp:assembleDebug
```

**Windows (PowerShell):**
```powershell
.\gradlew.bat :composeApp:assembleDebug
```

Una vez compilado, instala el APK generado (`composeApp/build/outputs/apk/debug/`) en tu dispositivo o emulador.

### Ejecutar Tests

```bash
./gradlew :composeApp:check
```

## Arquitectura

La aplicación sigue una arquitectura en capas con repositorios singleton:

```
┌──────────────────────────────────────────────┐
│  Presentación (Compose UI + Voyager)          │
│  Pantallas, tarjetas, diálogos, bottom sheets │
├──────────────────────────────────────────────┤
│  Datos (Repositorios singleton)               │
│  AuthRepository / PropertyRepository /        │
│  LeadRepository                               │
├──────────────────────────────────────────────┤
│  Red (NetworkClient singleton)                │
│  Ktor HttpClient + Auth Bearer + JSON         │
├──────────────────────────────────────────────┤
│  Plataforma (expect/actual)                   │
│  DataStore, FilePicker, Base URL              │
└──────────────────────────────────────────────┘
```

- **NetworkClient** (`core/network/NetworkClient.kt`) actúa como singleton que configura Ktor con content negotiation JSON, autenticación Bearer, y refresh automático de tokens al detectar respuestas 401.
- **JwtUtils** (`core/utils/JwtUtils.kt`) decodifica manualmente el payload del JWT para extraer `userId`, `roles` y verificar expiración.
- **SessionManager** (`features/auth/data/local/SessionManager.kt`) persiste los tokens de acceso y refresco usando DataStore Preferences, exponiendo el estado de sesión como `Flow<Boolean>`.
- **Repositorios** encapsulan toda la lógica de comunicación con el backend y exponen funciones suspend para las capas superiores.
- El estado de UI se gestiona directamente en los composables mediante `remember`, `mutableStateOf` y `LaunchedEffect`.
