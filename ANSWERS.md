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


