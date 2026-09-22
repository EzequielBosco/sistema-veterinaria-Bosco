# Sistema de Gestión Veterinaria · Patitas Felices

API REST para la gestión de una clínica veterinaria: dueños, mascotas, veterinarios y turnos. Incluye un frontend web liviano que consume la API directamente.

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración del entorno](#configuración-del-entorno)
- [Cómo correr el proyecto](#cómo-correr-el-proyecto)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Endpoints de la API](#endpoints-de-la-api)
- [Frontend](#frontend)
- [Tests](#tests)

---

## Descripción

Sistema backend desarrollado con Spring Boot que expone una API REST para administrar:

- **Dueños**: registro, búsqueda por DNI, email y nombre
- **Mascotas**: asociadas a un dueño, con especie, raza, sexo y fecha de nacimiento
- **Veterinarios**: con matrícula y especialidad
- **Turnos**: con fecha, hora, duración, mascota y uno o más veterinarios asignados con rol (Principal, Asistente, Consultor). Valida superposición de horarios por veterinario

La documentación interactiva de la API está disponible vía Swagger UI una vez levantado el servidor.

---

## Tecnologías

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.1.1 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | MySQL | 8+ |
| Validación | Jakarta Bean Validation | — |
| Mapeo | MapStruct | 1.6.3 |
| Documentación | Springdoc OpenAPI (Swagger) | 2.8.9 |
| Reducción boilerplate | Lombok | — |
| Build | Maven Wrapper | — |
| Frontend | HTML + Bootstrap 5 + JS Vanilla | Bootstrap 5.3.3 |

---

## Requisitos previos

- **Java 21** instalado y configurado en el `PATH`
- **MySQL 8+** corriendo localmente
- **Maven** (incluido como wrapper `mvnw`, no requiere instalación)
- **VS Code** con la extensión [Live Server](https://marketplace.visualstudio.com/items?itemName=ritwickdey.LiveServer) para el frontend (opcional)

Verificar versiones:

```bash
java -version
mysql --version
```

---

## Configuración del entorno

### 1. Crear la base de datos

```sql
CREATE DATABASE veterinaria_db;
```

### 2. Variables de entorno

El proyecto lee la configuración desde `src/main/resources/application-local.properties`. Este archivo **no se versiona** (está en `.gitignore`). Crearlo con las siguientes properties:

```
DB_URL=...
DB_USER=tu_user
DB_PASS=tu_password
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_PORT=8080
```

> Las tablas se crean automáticamente al iniciar gracias a `spring.jpa.hibernate.ddl-auto=update`.

---

## Cómo correr el proyecto

### Backend

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

En Windows sin bash:

```bash
mvnw.cmd spring-boot:run
```

El servidor queda disponible en: `http://localhost:8080`

**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

### Frontend

1. Abrir VS Code en la raíz del proyecto
2. Click derecho sobre `frontend/index.html`
3. Seleccionar **"Open with Live Server"**
4. Acceder a: `http://127.0.0.1:5500/frontend/index.html`

> El backend debe estar corriendo antes de abrir el frontend. CORS está configurado para `localhost:5500` y `127.0.0.1:5500`.

---

## Estructura del proyecto

```
sistema-veterinaria-Bosco/
├── frontend/
│   └── index.html              # Frontend con Bootstrap 5 + JS Vanilla
├── src/
│   ├── main/
│   │   ├── java/com/veterinaria/
│   │   │   ├── Config/         # CORS (WebConfig)
│   │   │   ├── Controller/     # Controladores REST
│   │   │   ├── DTO/            # Data Transfer Objects (Request y Response)
│   │   │   ├── Entity/         # Entidades JPA
│   │   │   │   └── enums/      # EstadoTurno, RolVeterinario, SexoMascota
│   │   │   ├── Exception/      # Excepciones personalizadas y GlobalExceptionHandler
│   │   │   ├── Mapper/         # Mappers MapStruct
│   │   │   ├── Repository/     # Interfaces JPA Repository
│   │   │   └── Service/        # Interfaces e implementaciones de servicios
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-local.properties   # No versionado
│   └── test/
│       └── java/com/veterinaria/
│           ├── Controller/     # Tests de controladores (MockMvc)
│           └── Service/        # Tests de servicios (Mockito)
└── pom.xml
```

---

## Endpoints de la API

Base URL: `http://localhost:8080/api`

### Dueños `/duenios`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/duenios` | Listar todos los dueños |
| GET | `/duenios/{id}` | Obtener dueño por ID |
| GET | `/duenios/cedula/{cedula}` | Buscar por DNI |
| GET | `/duenios/email/{email}` | Buscar por email |
| GET | `/duenios/search?nombre=&apellido=` | Buscar por nombre y apellido |
| GET | `/duenios/{id}/mascotas` | Listar mascotas de un dueño |
| POST | `/duenios` | Registrar nuevo dueño |
| POST | `/duenios/{id}/mascotas` | Registrar mascota para un dueño |
| PUT | `/duenios/{id}` | Actualizar dueño |
| DELETE | `/duenios/{id}` | Eliminar dueño |

### Mascotas `/mascotas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/mascotas` | Listar todas las mascotas |
| GET | `/mascotas/{id}` | Obtener mascota por ID |
| PUT | `/mascotas/{id}` | Actualizar mascota |
| DELETE | `/mascotas/{id}` | Eliminar mascota |

### Veterinarios `/veterinarios`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/veterinarios` | Listar todos los veterinarios |
| GET | `/veterinarios/{id}` | Obtener veterinario por ID |
| GET | `/veterinarios/{id}/turnos` | Listar turnos de un veterinario |
| POST | `/veterinarios` | Registrar nuevo veterinario |
| PUT | `/veterinarios/{id}` | Actualizar veterinario |
| DELETE | `/veterinarios/{id}` | Eliminar veterinario |

### Turnos `/turnos`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/turnos` | Listar todos los turnos |
| GET | `/turnos?veterinarioId=&fecha=` | Consultar agenda por veterinario y fecha |
| GET | `/turnos/{id}` | Obtener turno por ID |
| GET | `/turnos/{id}/veterinarios` | Listar veterinarios del turno |
| POST | `/turnos` | Registrar nuevo turno |
| PUT | `/turnos/{id}` | Actualizar turno |
| DELETE | `/turnos/{id}` | Eliminar turno |

### Códigos de respuesta

| Código | Significado |
|---|---|
| `200` | OK |
| `201` | Recurso creado |
| `204` | Eliminado correctamente |
| `400` | Datos inválidos (validación fallida) |
| `404` | Recurso no encontrado |
| `409` | Conflicto (email/DNI/matrícula duplicados, turno superpuesto) |
| `500` | Error interno del servidor |

Los errores devuelven el siguiente cuerpo:

```json
{
  "timestamp": "2026-10-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "El DNI ya está registrado",
  "path": "/api/duenios"
}
```

---

## Frontend

Interfaz web minimalista incluida en `frontend/index.html`. No requiere instalación ni build.

**Tecnologías:** Bootstrap 5.3.3 (CDN) + JavaScript Vanilla (Fetch API)

**Funcionalidades:**
- ABM completo de Dueños, Mascotas, Veterinarios y Turnos
- Carga lazy por tab (cada sección carga sus datos al activarse)
- Formularios modales con validación visual
- Mensajes de error directamente del `ErrorResponse` del backend
- Notificaciones de éxito mediante toast

---

## Tests

El proyecto incluye tests unitarios e de integración para controllers y servicios.

```bash
# Correr todos los tests
./mvnw test

# Correr solo los tests de un módulo
./mvnw test -Dtest=DuenioServiceImplTest
```

**Cobertura:**
- `Controller/` — tests con MockMvc que validan códigos HTTP y respuestas JSON
- `Service/` — tests con Mockito que validan lógica de negocio y excepciones
