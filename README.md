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

## Documentación API (Swagger / OpenAPI)

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

> En este punto el proyecto es un **template**: la aplicación levanta y la documentación carga, pero los endpoints del ejercicio se implementarán en la siguiente sección.

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

