# springboot-test-fintech

## Respuestas teóricas

Las respuestas de la **Sección 1 (Conocimientos Teóricos y Conceptuales)** y la **Sección 3 (Resolución de Problemas y Arquitectura)** están documentadas en `ANSWERS.md`.

## Requisitos

- **Java**: JDK **21** (recomendado).
- **Git**.

> Nota: este repo incluye **Maven Wrapper** (`mvnw`) para que no necesites instalar Maven manualmente.

## Compilar

```bash
chmod +x mvnw
./mvnw clean package
```

## Ejecutar (dev)

```bash
./mvnw spring-boot:run
```

Por defecto levanta en `http://localhost:8080`.

## Docker (producción)

Levantar **PostgreSQL + App** listos para producción (build incluido):

```bash
docker compose up -d --build
```

La app expone `http://localhost:8080` y la DB `localhost:5432` (solo si lo necesitas fuera de Docker).

Variables importantes:

- **API key**: `FINTECH_API_KEY` (default `change-me`)
- **DB**: `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` (por defecto apuntan al servicio `postgres` del compose)

## PostgreSQL (persistencia real)

Para ejecutar con PostgreSQL local (via Docker):

```bash
docker compose up -d
```

Luego levanta la app con el perfil **postgres**:

```bash
SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
```

Variables (defaults en `application-postgres.yml`):

- `SPRING_DATASOURCE_URL` (default `jdbc:postgresql://localhost:5432/fintech`)
- `SPRING_DATASOURCE_USERNAME` (default `fintech`)
- `SPRING_DATASOURCE_PASSWORD` (default `fintech`)

## Migraciones (Flyway)

El esquema se gestiona con **Flyway** y se aplica automáticamente al iniciar la app.

## Documentación API (Swagger / OpenAPI)

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

> En este punto el proyecto es un **template**: la aplicación levanta y la documentación carga, pero los endpoints del ejercicio se implementarán en la siguiente sección.

## Seguridad (API Key)

Los endpoints del ejercicio están protegidos con una **API Key** en el header:

- Header: `X-API-KEY`
- Config: `fintech.security.api-key` en `application.yml`

Ejemplo:

```bash
curl -H "X-API-KEY: change-me" http://localhost:8080/accounts/1
```

Swagger/OpenAPI quedan accesibles sin API key.

## Modelo / Contratos (Sección 2)

- **Modelo de dominio**:
  - `Money` (value object, escala fija de 2 decimales).
  - `Account` (invariantes: no permite débito con fondos insuficientes).
  - `Transaction` (registro de transferencias).
- **Persistencia**: JPA con `AccountEntity` usando **`@Version`** (optimistic locking) y `TransactionEntity`.
- **Contratos (DTOs)**: definidos para `POST /accounts`, `GET /accounts/{id}`, `POST /transactions` (sin controllers todavía).

## Estrategia de concurrencia (transferencias)

Para evitar inconsistencias bajo concurrencia (doble gasto, carreras), la transferencia se implementa como:

- **`@Transactional`** para atomicidad.
- **Optimistic locking** (`@Version`) sobre la cuenta.
- **Retry acotado** ante `ObjectOptimisticLockingFailureException` (reintentos con backoff).

## Tests

```bash
./mvnw test
```

Tests de integración (incluye `*IT` y tests con Postgres vía Testcontainers):

```bash
./mvnw verify
```

### Tests usando PostgreSQL (Testcontainers)

El proyecto incluye tests de integración contra **PostgreSQL real** usando **Testcontainers** (Docker).  
No necesitas levantar PostgreSQL manualmente: el contenedor se crea y destruye automáticamente durante el test.

- **Requisito**: Docker funcionando (el comando `docker ps` debe responder).
- **Ejecutar solo el test de PostgreSQL**:

```bash
./mvnw verify -Dit.test=PostgresSmokeIT
```

#### Troubleshooting (si Testcontainers “no encuentra Docker”)

Si ves errores relacionados con `DOCKER_API_VERSION` o “client version too old”, prueba:

```bash
unset DOCKER_API_VERSION
./mvnw verify
```

Para omitir integration tests:

```bash
./mvnw verify -DskipITs=true
```

## Endpoints (Sección 2)

### Crear cuenta

- `POST /accounts`

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: change-me" \
  -d '{"initialBalance": 100.00}'
```

### Consultar cuenta

- `GET /accounts/{accountId}`

```bash
curl http://localhost:8080/accounts/1 \
  -H "X-API-KEY: change-me"
```

### Transferencia

- `POST /transactions`

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: change-me" \
  -d '{"sourceAccountId": 1, "destinationAccountId": 2, "amount": 10.00}'
```

