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


