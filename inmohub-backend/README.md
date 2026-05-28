# 🏡 InmoHub Backend

Plataforma inmobiliaria basada en microservicios con **Spring Boot 4** y **Java 21**. Utiliza Spring Cloud para descubrimiento, configuración centralizada y enrutamiento, y Apache Kafka como bus de eventos entre servicios.

---

## 🏗 Arquitectura

```
                 ┌─────────────────┐
                 │   API Gateway   │  :8080
                 └───────┬─────────┘
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ Auth Service│  │Lead Service │  │FSBO Ingestor│
│    :8081    │  │    :8083    │  │    :8084    │
└──────┬──────┘  └─────────────┘  └──────┬──────┘
       │                                 │
       │          ┌─────────────┐        │
       └─────────►│   Kafka     │◄───────┘
                  └──────┬──────┘
                         │
       ┌─────────────────┼─────────────────┐
       ▼                 ▼                 ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Property   │  │Lead Service │  │FSBO Ingestor│
│   Service   │  │             │  │             │
│    :8082    │  │             │  │             │
└─────────────┘  └─────────────┘  └─────────────┘
```

### Microservicios

| Servicio | Puerto | Base de Datos | Descripción |
|---|---|---|---|
| **service-registry** | 8761 | — | Registro y descubrimiento de servicios (Eureka) |
| **config-service** | 8888 | — | Configuración centralizada (Spring Cloud Config) |
| **api-gateway** | 8080 | — | Puerta de entrada única con balanceo de carga |
| **auth-service** | 8081 | MySQL (`auth_db`) | Autenticación JWT, registro de usuarios y gestión de roles |
| **property-service** | 8082 | PostgreSQL (`property_db`) | Catálogo de inmuebles: CRUD, fotos (Firebase), búsqueda con filtros |
| **lead-service** | 8083 | PostgreSQL (`lead_db`) | Gestión de clientes potenciales con arquitectura hexagonal |
| **fsbo-ingestor-service** | 8084 | PostgreSQL (`fsbo_db`) | Ingesta masiva de inmuebles desde CSV (FSBO) |

### Infraestructura de soporte
- **Kafka + Zookeeper** — Mensajería asíncrona para eventos de dominio
- **Firebase Storage** — Almacenamiento de fotos de inmuebles

---

## 📋 Requisitos Previos

- **Docker & Docker Compose**
- **Java 21 JDK** (Eclipse Temurin recomendado)
- **Maven 4**
- **IntelliJ IDEA** (recomendado para ejecución manual)
- **Postman** o cualquier cliente HTTP para pruebas

---

## 🚀 Levantar el Backend con Docker

El archivo `docker-compose.yml` define la infraestructura completa del proyecto. Los microservicios están bajo el perfil `dev`, lo que te permite elegir entre dos modos de ejecución.

### Opción A: Todo en Docker (perfil `dev`)

Levanta **todo** en contenedores: bases de datos, Zookeeper, Kafka, Eureka, Config Server, los 4 microservicios y el API Gateway.

```bash
docker compose --profile dev up --build
```

Esto hace lo siguiente:
- `--profile dev` — Activa los contenedores de los microservicios, Eureka, Config Server y Gateway.
- `--build` — Reconstruye las imágenes desde los Dockerfile antes de arrancar (obligatorio la primera vez o si hay cambios en el código).

Una vez arrancado, el sistema estará listo en **~60 segundos** (Kafka y Eureka tardan en estabilizarse). Puedes hacer peticiones a través del Gateway en `http://localhost:8080`.

**Comandos útiles:**

```bash
# Detener todo
docker compose --profile dev down

# Reiniciar desde cero (borra datos de BD y volúmenes)
docker compose --profile dev down -v

# Reconstruir una imagen concreta sin parar el resto
docker compose --profile dev up -d --build api-gateway

# Ver logs de un servicio
docker compose logs -f auth-service
```

### Opción B: Solo infraestructura en Docker (sin perfil)

Levanta únicamente **bases de datos, Zookeeper y Kafka**. Los microservicios los ejecutas manualmente desde tu IDE.

```bash
docker compose up
```

Sin el perfil `dev`, solo arrancan estos contenedores:

| Contenedor | Puerto | Descripción |
|---|---|---|
| `auth-db` | 3307 | MySQL para auth-service |
| `property-db` | 5432 | PostgreSQL para property-service |
| `lead-db` | 5433 | PostgreSQL para lead-service |
| `fsbo-db` | 5434 | PostgreSQL para fsbo-ingestor-service |
| `zookeeper` | 2181 | Coordinación de Kafka |
| `broker` | 9092 | Apache Kafka |

Una vez que `docker compose up` termine, ejecuta las clases `main` desde IntelliJ en este orden:

1. **ServiceRegistryApplication** (Eureka)
2. **ConfigServiceApplication** (Spring Cloud Config)
3. **AuthServiceApplication**, **PropertyServiceApplication**, **LeadServiceApplication**, **FsboIngestorServiceApplication**
4. **ApiGatewayApplication**

Los microservicios locales usan estos puertos:

| Servicio | Puerto | Swagger |
|---|---|---|
| API Gateway | 8080 | — |
| Auth Service | 8081 | [Swagger UI](http://localhost:8081/swagger-ui/index.html) |
| Property Service | 8082 | [Swagger UI](http://localhost:8082/swagger-ui/index.html) |
| Lead Service | 8083 | [Swagger UI](http://localhost:8083/swagger-ui/index.html) |
| FSBO Ingestor | 8084 | [Swagger UI](http://localhost:8084/swagger-ui/index.html) |
| Eureka Dashboard | 8761 | [Dashboard](http://localhost:8761) |

Esta opción es la recomendada para **desarrollar y depurar**, ya que puedes poner breakpoints, reiniciar servicios individualmente y ver Swagger de cada microservicio por separado.

---

## 🧪 Guía de Pruebas con Postman

Todas las peticiones se realizan a través del **API Gateway** en `http://localhost:8080`.

### 1. Autenticación

#### Registrar usuario
```http
POST /api/v1/auth/register
```
```json
{
    "username": "elena_propietaria",
    "email": "elena.gomez@inmohub.com",
    "password": "password123",
    "firstName": "Elena",
    "lastName": "Gómez",
    "phone": "655444333",
    "roles": ["OWNER"]
}
```

#### Iniciar sesión
```http
POST /api/v1/auth/login
```
```json
{
    "email": "elena.gomez@inmohub.com",
    "password": "password123"
}
```
La respuesta contiene `accessToken` y `refreshToken`. Usa el `accessToken` como **Bearer Token** en las peticiones autenticadas.

#### Refrescar token
```http
POST /api/v1/auth/refresh
```
```json
{
    "refreshToken": "<refresh_token>"
}
```

### 2. Propiedades

```http
GET    /api/v1/properties/all                        # Listar todas
GET    /api/v1/properties/search-by-id/{id}          # Buscar por ID
GET    /api/v1/properties/search-by-owner-id/{id}    # Propiedades de un dueño
GET    /api/v1/properties/summary                    # Catálogo paginado
GET    /api/v1/properties/search?city=&minPrice=&maxPrice=&status=  # Búsqueda con filtros
POST   /api/v1/properties/create                     # Crear (multipart: property JSON + fotos)
PATCH  /api/v1/properties/update/{id}                # Actualizar parcialmente
DELETE /api/v1/properties/delete-by-id/{id}          # Eliminar propiedad
```

Ejemplo de creación de propiedad:
```http
POST /api/v1/properties/create
Content-Type: multipart/form-data
Authorization: Bearer <accessToken>

Part "property" (application/json):
{
    "title": "Casa Rural en la Sierra",
    "description": "Casa de piedra rehabilitada con chimenea y huerto.",
    "price": 195000.00,
    "areaM2": 150.5,
    "address": "Camino del Río s/n",
    "city": "Sierra de Gredos",
    "state": "Ávila",
    "country": "España",
    "features": [
        {"featureName": "Habitaciones", "featureValue": "4"},
        {"featureName": "Baños", "featureValue": "2"}
    ]
}

Part "photos" (opcional): archivos de imagen
```

### 3. Leads (Clientes Potenciales)

```http
POST   /api/v1/leads/create                          # Registrar un interesado
GET    /api/v1/leads/all?page=0&size=10              # Listar paginado
GET    /api/v1/leads/property/{propertyId}           # Leads de una propiedad
GET    /api/v1/leads/agent/{agentId}                 # Leads asignados a un agente
POST   /api/v1/leads/{leadId}/assign                 # Asignar a un agente
PATCH  /api/v1/leads/{leadId}/status                 # Cambiar estado
DELETE /api/v1/leads/property/{propertyId}           # Eliminar leads por propiedad
```

Ejemplo de creación de lead:
```json
{
    "name": "Carlos López",
    "email": "carlos.lopez@gmail.com",
    "phone": "612345678",
    "message": "Me interesa visitar la propiedad",
    "source": "WEB",
    "propertyId": "<property_uuid>"
}
```

Estados de un lead: `NEW` → `CONTACTED` → `NEGOTIATION` → `CLOSED` / `LOST`

### 4. FSBO — Ingesta Masiva de Inmuebles

```http
POST /api/v1/fsbo/properties/bulk
Content-Type: multipart/form-data
Authorization: Bearer <accessToken>

Part "file": archivo CSV
```

Formato del CSV:
```csv
title,description,price,area_m2,address,city,state,country,features
Chalet en Madrid,Amplio chalet con piscina,450000,250.5,Calle Mayor 123,Madrid,Comunidad de Madrid,España,Habitaciones:3;Baños:2;Piscina:Si
Apartamento Barcelona,Piso reformado,180000,80.0,Avenida Diagonal 456,Barcelona,Cataluña,España,
```

Las features se escriben como `clave:valor` separadas por `;`. El sistema detecta duplicados por dirección+ciudad y los marca como error.

---

## 🔄 Flujo de Eventos (Kafka)

El sistema usa Apache Kafka para comunicación asíncrona entre microservicios:

| Tópico | Productor | Consumidor | Evento |
|---|---|---|---|
| `user.lifecycle.events` | auth-service | property-service | Usuario eliminado → borrado en cascada de sus propiedades |
| `property.bulk.create` | fsbo-ingestor-service | property-service | Lote de inmuebles desde CSV |
| `lead.events` | fsbo-ingestor-service | lead-service | Propietario FSBO registrado como lead |
| `property.events` | property-service | lead-service | Propiedad creada/deleted → gestión de leads asociados |

---

## 🧪 Tests Unitarios

Cada microservicio incluye tests unitarios con **JUnit 5 + Mockito**, organizados por capa:

```bash
# Ejecutar tests de un servicio específico
./mvnw test -pl auth-service
./mvnw test -pl property-service
./mvnw test -pl lead-service
./mvnw test -pl fsbo-ingestor-service

# Ejecutar todos los tests
./mvnw test
```

La cobertura incluye modelos de dominio, casos de uso, servicios, mappers, controladores, manejo de excepciones y publicadores/consumidores de Kafka.

---

## 📁 Estructura del Proyecto

```
inmohub-backend/
├── api-gateway/              # Spring Cloud Gateway + JWT validation
├── service-registry/         # Netflix Eureka
├── config-service/           # Spring Cloud Config (configuración centralizada)
├── auth-service/             # Usuarios, roles, JWT, refresh tokens
├── property-service/         # Inmuebles, fotos, búsquedas, Firebase
├── lead-service/             # Leads con arquitectura hexagonal (DDD)
├── fsbo-ingestor-service/    # Ingesta CSV con arquitectura hexagonal
├── docker-compose.yml        # Infraestructura completa
└── pom.xml                   # POM padre (Spring Boot 4, Java 21)
```

---

## ℹ️ Notas

- Con **Opción A** (Docker completo), espera ~60 segundos tras el inicio para que todos los servicios se conecten a Eureka y Config Server.
- Las fotos de inmuebles se suben a Firebase Storage. Para entorno local, configura el archivo `firebase-adminsdk.json` en `property-service/src/main/resources/`.
- Los tests unitarios no requieren base de datos: usan mocks de Mockito.
- En Docker, las URLs internas de Kafka usan `broker:29092`. En local, `localhost:9092`.
