# 🏡 InmoHub

Sistema de gestión inmobiliaria (CRM) de modelo híbrido que conecta a agencias tradicionales con propietarios particulares (FSBO — *For Sale By Owner*). InmoHub resuelve la fragmentación del mercado inmobiliario unificando en una sola plataforma la publicación de inmuebles, la captación de clientes potenciales y la gestión del ciclo de venta, tanto para profesionales como para particulares que desean vender sin intermediarios.

## 🎯 Alcance

En esta fase, el sistema implementa los flujos core de la aplicación:

- **Gestión de Identidad:** Registro y autenticación de usuarios con JWT (access + refresh tokens). Cuatro roles: CLIENT, OWNER, AGENT y ADMIN.
- **Catálogo de Propiedades:** CRUD completo de inmuebles con búsqueda paginada por filtros (ciudad, precio, estado), subida de imágenes y carga masiva desde archivos CSV.
- **Gestión de Leads:** Captación de clientes potenciales con pipeline de estados (NUEVO → CONTACTADO → NEGOCIACIÓN → CERRADO/PERDIDO), asignación a agentes y bolsa global de leads.
- **Arquitectura Distribuida:** Microservicios independientes comunicados mediante API Gateway, registro de servicios (Eureka), configuración centralizada y mensajería asíncrona con Apache Kafka.
- **Interfaz Multiplataforma:** Cliente en Kotlin Multiplatform con navegación declarativa (Voyager) para Android y Desktop (JVM) desde una única base de código.

## 👥 Roles de Usuario

| Rol | Funcionalidades |
|---|---|
| **CLIENT** | Explorar catálogo, buscar/filtrar propiedades, ver detalles, crear leads de interés |
| **OWNER** | Todo lo de CLIENT + publicar/editar/eliminar propiedades, ver leads de sus inmuebles, carga masiva CSV (FSBO) |
| **AGENT** | Dashboard de gestión: listar clientes y propietarios, ver todas las propiedades, gestionar leads (bolsa global y asignados), cambiar estados |
| **ADMIN** | Mismas funcionalidades que AGENT |

## 🏗️ Arquitectura

El proyecto está dividido en dos grandes bloques independientes:

### Backend (Microservicios)

Desarrollado en **Java 21** y **Spring Boot 4.0.1** con **Spring Cloud 2025.1.0**.

| Servicio | Puerto | Base de Datos | Descripción |
|---|---|---|---|
| **service-registry** | 8761 | — | Registro y descubrimiento (Netflix Eureka) |
| **config-service** | 8888 | — | Configuración centralizada (Spring Cloud Config) |
| **api-gateway** | 8080 | — | Puerta de entrada única con balanceo de carga y validación JWT |
| **auth-service** | 8081 | MySQL (`auth_db`) | Autenticación JWT, registro de usuarios y gestión de roles |
| **property-service** | 8082 | PostgreSQL (`property_db`) | Catálogo de inmuebles: CRUD, fotos (Firebase Storage), búsqueda con filtros |
| **lead-service** | 8083 | PostgreSQL (`lead_db`) | Gestión de leads con arquitectura hexagonal (DDD) |
| **fsbo-ingestor-service** | 8084 | PostgreSQL (`fsbo_db`) | Ingesta masiva de inmuebles desde archivos CSV |

**Infraestructura de soporte:**
- **Kafka + Zookeeper** — Mensajería asíncrona para eventos de dominio entre microservicios
- **Firebase Storage** — Almacenamiento de fotos de inmuebles

**Tópicos Kafka:**

| Tópico | Productor | Consumidor | Evento |
|---|---|---|---|
| `user.lifecycle.events` | auth-service | property-service | Usuario eliminado → borrado en cascada de sus propiedades |
| `property.bulk.create` | fsbo-ingestor-service | property-service | Lote de inmuebles desde CSV |
| `lead.events` | fsbo-ingestor-service | lead-service | Propietario FSBO registrado como lead |
| `property.events` | property-service | lead-service | Propiedad creada/eliminada → gestión de leads asociados |

### Frontend (KMP)

Cliente construido con **Kotlin Multiplatform** y **Compose Multiplatform (Material 3)**:

| Categoría | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.21 |
| UI | Compose Multiplatform 1.9.3 |
| Navegación | Voyager 1.0.0 |
| Networking | Ktor Client 3.0.0 (OkHttp en Android, CIO en JVM) |
| Serialización | Kotlinx Serialization (JSON) |
| Autenticación | JWT (decodificación manual) + Ktor Auth (Bearer con refresh automático) |
| Imágenes | Coil 3.4.0 |
| Persistencia local | DataStore Preferences 1.1.1 |
| Arquitectura | Patrón Repository + expect/actual para abstracción de plataforma |
| Targets | Android (minSdk 24, targetSdk 36) y Desktop JVM (target 11) |

## 📂 Estructura del Repositorio

```
inmohub/
├── inmohub-backend/               # Microservicios Spring Boot
│   ├── api-gateway/               # Spring Cloud Gateway + validación JWT
│   ├── service-registry/          # Netflix Eureka
│   ├── config-service/            # Spring Cloud Config
│   ├── auth-service/              # Usuarios, roles, JWT, refresh tokens
│   ├── property-service/          # Inmuebles, fotos, búsquedas, Firebase
│   ├── lead-service/              # Leads con arquitectura hexagonal (DDD)
│   ├── fsbo-ingestor-service/     # Ingesta CSV con arquitectura hexagonal
│   ├── docker-compose.yml         # Infraestructura completa (2 perfiles)
│   └── pom.xml                    # POM padre (Spring Boot 4.0.1, Java 21)
├── inmohub-frontend/              # Cliente Kotlin Multiplatform
│   ├── composeApp/                # Código compartido + targets Android/JVM
│   ├── build.gradle.kts           # Configuración de plugins y dependencias
│   └── settings.gradle.kts        # Gestión de módulos
├── docs/                          # Análisis de mercado, diseño e implementación
└── README.md
```

## 🚀 Cómo probar el proyecto

### Backend

**Opción A — Todo en Docker:**
```bash
cd inmohub-backend
docker compose --profile dev up --build
```
Levanta todas las bases de datos, Kafka, Eureka, Config Server, los 4 microservicios y el API Gateway. El sistema estará listo en ~60 segundos en `http://localhost:8080`.

**Opción B — Solo infraestructura en Docker + servicios en IDE:**
```bash
cd inmohub-backend
docker compose up    # Solo BBDD y Kafka
```
Luego arranca los servicios desde IntelliJ en orden: ServiceRegistry → ConfigService → microservicios → ApiGateway.

Consulta la [Guía completa del Backend](./inmohub-backend/README.md) para documentación de API, ejemplos de Postman y testing.

### Frontend

**Desktop (JVM):**
```bash
cd inmohub-frontend
./gradlew :composeApp:run
```

**Android:**
```bash
cd inmohub-frontend
./gradlew :composeApp:assembleDebug
```

Consulta la [Guía completa del Frontend](./inmohub-frontend/README.md) para estructura del proyecto, roles y arquitectura.

### Tests

```bash
# Backend
cd inmohub-backend && ./mvnw test

# Frontend
cd inmohub-frontend && ./gradlew :composeApp:check
```
