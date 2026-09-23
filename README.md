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

---

## Parcial 1 — Decisiones de diseño

### Relación Turno–Medicamento

Modelé la relación como un muchos a muchos: en un turno se pueden recetar varios medicamentos y un mismo medicamento aparece en muchos turnos. En lugar de usar un `@ManyToMany` directo, armé una entidad intermedia `Prescripcion`, con un `@ManyToOne` hacia `Turno` y otro hacia `Medicamento`. La razón principal es que la relación tiene datos propios que no van en ninguna de las otras entidades: la `cantidad` recetada y las `indicaciones` de administración. Además ya venía usando el mismo patrón con `Participacion` entre `Turno` y `Veterinario` (que guarda el `rol`), así que mantuve la consistencia del modelo. 

### Validación de stock

El control de stock lo hice en la capa de servicio, dentro de `TurnoServiceImpl.asociarMedicamento`, que está marcado con `@Transactional`. Primero busco el turno y el medicamento (si alguno no existe respondo 404), y después tomo la cantidad del body; si no viene, uso 1 por defecto. Antes de crear la prescripción comparo el stock actual del medicamento contra la cantidad pedida, y si no alcanza lanzo `StockInsuficienteException`. El `GlobalExceptionHandler` traduce esa excepción a un 422 con un `ErrorResponse` que dice cuántas unidades se pidieron y cuántas hay disponibles. Si el stock alcanza, guardo la prescripción con esa cantidad y descuento exactamente esas unidades del medicamento dentro de la misma transacción, así nunca queda una prescripción guardada sin su descuento. 

### Solapamiento

Para detectar solapamientos uso la consulta derivada `findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc`, que hace un join con `participaciones` y trae todos los turnos de un veterinario en la fecha del turno nuevo. No hice solo la comparación de la hora exacta, porque dos turnos pueden pisarse aunque arranquen a horas distintas (por ejemplo 10:00 de 30 minutos y otro a las 10:15). Por eso cada turno tiene el duración minutos y se toma como `[hora, hora + duracionMinutos)`, considero que hay conflicto cuando `inicioExistente < finNuevo` y `inicioNuevo < finExistente`. Cuando encuentro un conflicto lanzo `TurnoSuperpuestoException`, que se devuelve como 409 indicando el ID del turno conflictivo, su fecha y su horario de inicio y fin.

### Cupo de mascotas

El límite de 5 mascotas por dueño lo controlo en `MascotaServiceImpl` con una constante `MAX_MASCOTAS_POR_DUENIO`. Para contar uso el método `countByDuenioId` en `MascotaRepository`, que se traduce en un `SELECT COUNT(*)` sobre `mascotas` filtrando por `id_duenio`. En cuanto al criterio de "mascotas activas", la entidad `Mascota` no tiene un campo de estado y el borrado es físico (pensé la implementación de estados en todas las entidades y implementar un delete lógico pero me llevaba mucho tiempo para el parcial), así que toda mascota que existe en la base la considero activa. Si el dueño ya tiene 5, lanzo `CupoMascotasExcedidoException`, que se devuelve como 422 con un mensaje que indica el dueño, cuántas mascotas tiene y cuál es el límite. Además de validar al registrar una mascota, también valido cuando se edita una mascota y se le cambia el dueño, porque si no se podía esquivar el límite transfiriendo mascotas.

### Decisión más difícil

Lo más difícil de todo fue definir la entidad intermedia de prescripción porque me llevo un tiempo pensarla sin empezar a desarrollar, tuve que asegurarme de que todos los aspectos de la relación entre turnos y medicamentos estuvieran bien modelados. También porque había pensado en que el endpoint general de Listar los turnos iba a devolver la lista de medicamentos, hasta que vi el endpoint específico que se planteaba en el parcial y me di cuenta de que no era necesario porque si alguien necesita la lista de turnos, no deberían estar los medicamentos prescriptos tan a la vista.

### Esquema de base de datos

Para crear las tablas nuevas uso `spring.jpa.hibernate.ddl-auto=update`, sin scripts de migración. El cambio de este parcial solo agrega las tablas `medicamentos` y `prescripciones` (con las FKs `id_turno` e `id_medicamento`) y no modifica ni elimina columnas de las tablas existentes. Ese es justamente el caso que `update` resuelve bien, porque al reiniciar la aplicación una base existente se actualiza sola sin perder datos.

### Endpoints nuevos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/medicamentos` | Listar todos los medicamentos |
| GET | `/medicamentos/{id}` | Obtener medicamento por ID |
| POST | `/medicamentos` | Registrar nuevo medicamento |
| PUT | `/medicamentos/{id}` | Actualizar medicamento |
| DELETE | `/medicamentos/{id}` | Eliminar medicamento |
| GET | `/turnos/{id}/medicamentos` | Listar los medicamentos recetados en un turno |
| POST | `/turnos/{turnoId}/medicamentos/{medicamentoId}` | Recetar un medicamento en un turno. Body opcional: `cantidad` (por defecto 1) e `indicaciones`. Descuenta la cantidad del stock |

Códigos de respuesta nuevos o ampliados:

| Código | Caso |
|---|---|
| `409` | Turno superpuesto: el mensaje indica el ID y el horario del turno conflictivo |
| `422` | Stock insuficiente para la cantidad recetada, o dueño con el cupo de 5 mascotas completo |

En el frontend agregué la pestaña **Medicamentos** y, en cada turno, un botón **Medicamentos** que muestra lo recetado y permite recetar indicando cantidad e indicaciones.
