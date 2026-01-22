# Respuestas Teóricas (Spring Boot Test Fintech)

## Transacciones en Spring

### 1) Propósito de `@Transactional` y qué pasa si no se usa

`@Transactional` **delimita una transacción** alrededor de un método (o clase) para que un conjunto de operaciones contra la base de datos se ejecute como **una única unidad atómica**: o **se confirman todas** (*commit*) o **se revierten todas** (*rollback*) si ocurre un error.

En la práctica, esto garantiza propiedades **ACID**, especialmente:

- **Atomicidad**: evita estados intermedios persistidos.
- **Consistencia**: las reglas de negocio quedan aplicadas completamente o no se aplican.
- **Aislamiento** (según configuración): reduce anomalías bajo concurrencia.

Si **no** se utiliza `@Transactional` en operaciones que deben ser atómicas:

- **Cada operación puede confirmarse por separado** (autocommit o transacciones parciales), dejando el sistema en un **estado inconsistente** si algo falla a mitad (por ejemplo, se debita una cuenta pero no se acredita la otra).
- **El rollback no agrupa los cambios**: una excepción puede ocurrir después de que ya se hayan persistido cambios previos.
- Bajo concurrencia, se incrementa el riesgo de **condiciones de carrera** y resultados no deseados si no se aplica una estrategia transaccional/locking adecuada.

### 2) Niveles de aislamiento (Isolation Levels) y qué problema previene cada uno

El **aislamiento** define cuánto “ven” entre sí las transacciones concurrentes. A mayor aislamiento, menos anomalías; a cambio, suele haber más bloqueo/contención y menor throughput.

- **`READ_UNCOMMITTED`**: permite leer cambios **no confirmados** de otras transacciones.  
  - **Previene**: nada relevante.  
  - **Anomalía típica**: **lectura sucia (dirty read)**. Ej.: lees un saldo “debitado” por una transferencia que luego hace rollback.

- **`READ_COMMITTED`** (común por defecto en muchos motores): solo permite leer datos **confirmados**.  
  - **Previene**: **lecturas sucias**.  
  - **Aún permite**: **lecturas no repetibles**. Ej.: en la misma transacción consultas el saldo dos veces y entre medias otra transacción lo actualiza y confirma.

- **`REPEATABLE_READ`**: garantiza que si lees una fila, lecturas posteriores de esa fila dentro de la misma transacción no cambian.  
  - **Previene**: **lecturas sucias** y **lecturas no repetibles**.  
  - **Aún puede permitir**: **lecturas fantasma (phantom read)** (dependiendo del motor/implementación). Ej.: haces `SELECT` de “transacciones del día” y otra transacción inserta una nueva que aparece en una segunda lectura.

- **`SERIALIZABLE`**: el nivel más estricto; el resultado es equivalente a ejecutar transacciones **en serie**.  
  - **Previene**: **lecturas sucias**, **no repetibles** y **fantasma**.  
  - **Coste**: más bloqueos y mayor probabilidad de esperas/errores por contención bajo alta concurrencia.

### 3) Reglas de rollback por defecto en Spring y cómo personalizarlas

Por defecto, Spring hace **rollback** de una transacción declarada con `@Transactional` cuando el método termina lanzando:

- **`RuntimeException`** (excepciones no chequeadas) o cualquier subclase.
- **`Error`** (fallos graves de la JVM).

Y por defecto **NO** hace rollback (hace *commit*) cuando el método lanza una **excepción chequeada** (`checked exception`, subclase de `Exception` que no es `RuntimeException`), salvo que se configure explícitamente.

Cómo personalizarlo:

- **Con atributos de `@Transactional`**:
  - **`rollbackFor = { ... }`**: forzar rollback también para excepciones chequeadas específicas.
  - **`noRollbackFor = { ... }`**: evitar rollback para excepciones concretas (aunque sean `RuntimeException`).
  - (Equivalentes “por nombre”: `rollbackForClassName` / `noRollbackForClassName`).
- **Programático**:
  - **`TransactionTemplate`** (recomendado si quieres control explícito sin AOP).
  - Marcar rollback manualmente: `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()`.

## Spring Boot y Arquitectura

### 1) Inversión de control (IoC) e inyección de dependencias (DI) en Spring

En Spring, **IoC** significa que el framework (a través del **`ApplicationContext`**) es quien **crea, configura y gestiona** los objetos de la aplicación (**beans**) y su ciclo de vida, en vez de que tu código los instancie manualmente.

La **DI** es el mecanismo mediante el cual Spring **proporciona** a un objeto las dependencias que necesita (otros beans), normalmente mediante **inyección por constructor** (la recomendada), logrando **desacoplamiento**, **reutilización** y **testabilidad** (fácil reemplazo por mocks/stubs).

### 2) Singleton vs Prototype en Spring (cuándo usar cada uno)

En Spring, **Singleton** y **Prototype** se refieren al **scope del bean**:

- **Singleton (`@Scope("singleton")`, por defecto)**: Spring crea **una sola instancia por `ApplicationContext`** y la reutiliza en cada inyección/lookup.  
  - **Úsalo** para componentes **sin estado mutable por request**: `@Service`, `@Repository`, `@Controller`, clients HTTP, mappers, etc. (el estado debe ser thread-safe o inexistente).

- **Prototype (`@Scope("prototype")`)**: Spring crea **una nueva instancia cada vez** que se solicita el bean.  
  - **Úsalo** cuando necesitas un objeto **con estado por uso** (no thread-safe) o una construcción costosa que quieres materializar bajo demanda; p. ej., builders/handlers por operación.

Nota: el “singleton” de Spring **no** es un Singleton clásico global; es **singleton por contenedor** (por contexto de aplicación).

### 3) Comunicación entre microservicios (Spring Boot): síncrona y asíncrona

En una arquitectura con Spring Boot, la comunicación entre microservicios suele implementarse con:

- **Síncrona (request/response)**: **HTTP/REST** (Spring MVC + `WebClient`/`RestTemplate` o **OpenFeign**) o **gRPC**.  
  - **Útil cuando** necesitas respuesta inmediata (p. ej., consultar datos) y puedes tolerar acoplamiento temporal (dependencia de disponibilidad/latencia).

- **`WebClient` (Spring WebFlux)**: realiza llamadas HTTP de forma **no bloqueante** y devuelve `Mono<T>`/`Flux<T>`. Conceptualmente, se parece a trabajar con *promises* en Node: componer (`map/flatMap`) y encadenar sin bloquear.

También existe un enfoque **asíncrono basado en hilos** (no reactivo):

- **`@Async` + `CompletableFuture<T>`**: ejecuta la llamada HTTP en un *thread pool* y devuelve un futuro; el hilo que atiende la petición puede continuar. Es útil si tu app es Spring MVC “clásico” y no quieres adoptar WebFlux.


- **Asíncrona (event-driven)**: **mensajería/pub-sub** con **Kafka** o **RabbitMQ** (Spring for Apache Kafka / Spring AMQP), publicando **eventos** y consumiéndolos con listeners.  
  - **Útil cuando** priorizas **desacoplamiento**, **resiliencia** y absorción de picos (p. ej., notificaciones, auditoría, integración). Suele combinarse con **idempotencia** y patrones como **Outbox** para fiabilidad.



## Seguridad

### 1) Cómo proteger una API REST en Spring Boot (componentes clave de Spring Security)

En Spring Boot protegería la API con **Spring Security** configurando una cadena de filtros y reglas de autorización:

- **`SecurityFilterChain`**: define las reglas HTTP (qué endpoints requieren auth, roles/scopes, etc.).
- **Filtros de autenticación** (p. ej. un `OncePerRequestFilter`): extraen el token (por ejemplo `X-API-KEY` o `Authorization: Bearer ...`), lo validan y construyen el `Authentication` en el `SecurityContext`.
- **`AuthenticationManager` / `AuthenticationProvider`**: encapsulan la verificación de credenciales/token.
- **Autorización**: `authorizeHttpRequests(...)` (y opcionalmente **`@EnableMethodSecurity`** para `@PreAuthorize` a nivel de método).
- **Manejo de errores de seguridad**: `AuthenticationEntryPoint` (401) y `AccessDeniedHandler` (403) para respuestas consistentes.
- **Hardening típico para REST**: `SessionCreationPolicy.STATELESS` (sin sesión), **CSRF deshabilitado** si no hay cookies, y **CORS** configurado si aplica.

