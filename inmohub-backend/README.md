# 🏡 InmoHub Backend - Guía Pruebas

Este documento detalla los pasos necesarios para desplegar, ejecutar y probar la arquitectura de microservicios de **InmoHub**. El sistema está construido con **Spring Boot 4** y **Java 21**, utilizando una arquitectura distribuida con Spring Cloud.

---

## 📋 Requisitos Previos

* **Docker & Docker Compose** (Instalados y en ejecución).
* **Java 21 JDK (Eclipse Temurin)**.
* **Maven 4**.
* **IntelliJ IDEA** (Recomendado para la ejecución manual).
* **Postman** o cualquier cliente HTTP para pruebas.

---

## 🚀 Opciones de Despliegue

Existen dos formas de levantar el proyecto dependiendo de si se desea depurar el código o ver el sistema completo funcionando.

### Opción A: Despliegue Completo con Docker (Recomendado para revisión rápida)
Esta opción levanta toda la infraestructura (Bases de datos, Eureka, Config Server, Gateway y Microservicios) en contenedores.

1.  Abre una terminal en la raíz del proyecto.
2.  Ejecuta el siguiente comando para construir y levantar todo:
    ```bash
    docker compose --profile dev up --build
    ```
    *Nota: El perfil `--profile dev` es necesario para activar los contenedores de los microservicios, de lo contrario solo arrancarán las bases de datos.*

3.  Para detener el sistema:
    ```bash
    docker compose --profile dev down
    ```

### Opción B: Híbrido (Bases de Datos en Docker + Microservicios en IntelliJ)
Esta opción es ideal para **depuración** o si se prefiere ejecutar los servicios manualmente desde el IDE.

1.  **Levantar solo la infraestructura de datos:**
    Ejecuta el siguiente comando (sin perfil):
    ```bash
    docker compose up
    ```
    *Esto levantará únicamente MySQL (`auth-db`) y PostgreSQL (`property-db`).*

2.  **Arrancar los Microservicios:**
    Desde IntelliJ IDEA, ejecuta las clases `Main` de los servicios en el siguiente orden estricto para evitar fallos de conexión:
    1.  **Service Registry** (Eureka).
    2.  **Config Service**.
    3.  **Auth Service** y **Property Service**.
    4.  **API Gateway**.

---

## 🛠 Verificación de la Infraestructura

Una vez levantado el sistema (por cualquiera de las dos opciones), verifica que todo funcione correctamente:

### 1. Dashboard de Eureka (Service Registry)
Accede a la siguiente URL para comprobar que los microservicios (`AUTH-SERVICE`, `PROPERTY-SERVICE`, `API-GATEWAY`) se han registrado exitosamente:

* 🌐 **URL:** [http://localhost:8761](http://localhost:8761)

### 2. Documentación de la API (Swagger / OpenAPI)
La documentación interactiva de los endpoints está disponible individualmente para cada microservicio.

> ⚠️ **Nota Importante:** El acceso a la documentación a través de estas URLs **solo está disponible si utilizas la Opción B (Híbrido)**, es decir, levantando solo las bases de datos con Docker y ejecutando los microservicios localmente desde tu IDE.

* 🔐 **Auth Service API:** [http://localhost:8081/swagger-ui/index.html#/](http://localhost:8081/swagger-ui/index.html#/)
* 🏠 **Property Service API:** [http://localhost:8082/swagger-ui/index.html#/](http://localhost:8082/swagger-ui/index.html#/)

---

## 🧪 Guía de Pruebas con Postman (API Gateway)

Todas las peticiones deben realizarse a través del **API Gateway** en el puerto **8080**.

### Flujo de Prueba: Crear Propietario y Asignar Propiedad

Para probar la integración entre microservicios, seguiremos este flujo:
1.  Registrar un usuario con rol `OWNER`.
2.  Obtener su `ID` generado.
3.  Crear una propiedad asignada a ese `ID`.

#### Paso 1: Crear Usuario (Propietario)

* **Método:** `POST`
* **URL:** `http://localhost:8080/api/v1/auth/register`
* **Body (JSON):**

```json
{
    "username": "elena_propietaria",
    "email": "elena.gomez@inmohub.com",
    "password": "password123",
    "firstName": "Elena",
    "lastName": "Gómez",
    "phone": "655444333",
    "role": "OWNER"
}
```

#### Paso 2: Crear Propiedad

Una vez tengas el **ID** del usuario creado en el paso anterior, úsalo para asignar la propiedad.

* **Método:** `POST`
* **URL:** `http://localhost:8080/api/v1/properties/create`
* **Body (JSON):**
    * *Sustituye el valor de `ownerId` con el ID que copiaste en el paso anterior.*

```json
{
    "title": "Casa Rural en la Sierra",
    "description": "Casa de piedra rehabilitada con chimenea, vigas de madera y un pequeño huerto. Ideal para escapadas de fin de semana o turismo rural.",
    "price": 195000.00,
    "areaM2": 150.5,
    "address": "Camino del Río s/n, Sierra de Gredos",
    "ownerId": "PEGAR_AQUI_EL_ID_DEL_USUARIO_CREADO"
}
```

### Otros Endpoints Útiles

* **Login de Usuario:**
    * **Método:** `POST`
    * **URL:** `http://localhost:8080/api/v1/auth/login`
    * **Body:**
      ```json
      {"email": "elena.gomez@inmohub.com", "password": "password123"}
      ```

* **Listar todas las propiedades:**
    * **Método:** `GET`
    * **URL:** `http://localhost:8080/api/v1/properties/all`

* **Ver propiedades de un propietario:**
    * **Método:** `GET`
    * **URL:** `http://localhost:8080/api/v1/properties/search-by-owner-id/{ownerId}`

---

## ℹ️ Notas

* Si utiliza la **Opción A (Docker completo)**, espere aprox. **60 segundos** tras el inicio para asegurar que todos los servicios se han conectado a Eureka y Config Server correctamente.
* Los datos se persisten en volúmenes Docker. Para reiniciar las pruebas desde cero, use:
  ```bash
  docker compose --profile dev down -v