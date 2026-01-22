# springboot-test-fintech

## Respuestas teóricas

Las respuestas de la **Sección 1 (Conocimientos Teóricos y Conceptuales)** y la **Sección 3 (Resolución de Problemas y Arquitectura)** están documentadas en `ANSWERS.md`.

## Requisitos

- **Java**: JDK **21** (recomendado).
- **Git**.

> Nota: este repo incluye **Maven Wrapper** (`mvnw`) para que no necesites instalar Maven manualmente.

## Compilar

```bash
cd /home/crivero/repo/github/springboot-test-fintech
chmod +x mvnw
./mvnw clean package
```

## Ejecutar (dev)

```bash
cd /home/crivero/repo/github/springboot-test-fintech
./mvnw spring-boot:run
```

Por defecto levanta en `http://localhost:8080`.

## Documentación API (Swagger / OpenAPI)

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

> En este punto el proyecto es un **template**: la aplicación levanta y la documentación carga, pero los endpoints del ejercicio se implementarán en la siguiente sección.

## Tests

```bash
cd /home/crivero/repo/github/springboot-test-fintech
./mvnw test
```

