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


