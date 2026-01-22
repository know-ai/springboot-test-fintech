# springboot-test-fintech
El objetivo es evaluar habilidades y conocimientos como desarrollador backend Java con Spring Boot en un entorno fintech. El objetivo es valorar la capacidad para diseñar, desarrollar y mantener aplicaciones seguras, escalables y robustas, con un enfoque particular en el manejo de transacciones, la seguridad y las buenas prácticas de programación.

# Sección 1: Conocimientos Teóricos y Conceptuales

Responde a las siguientes preguntas de la forma mas clara y concisa posible.

## Transacciones en Spring

- Describe el propósito de la anotación @Transactional en Spring. Qué sucede si no se utiliza al realizar operaciones de base de datos que deben ser atómicas?

- Explica los diferentes niveles de aislamiento de transacción (Isolation Levels) y da un ejemplo de un problema que cada nivel ayuda a prevenir (lecturas sucias, lecturas no repetibles, lecturas fantasma).

- Cuáles son las reglas de rollback por defecto para una transacción en Spring? ¿Cómo se puede personalizar este comportamiento?

## Spring Boot y Arquitectura

- Explica el concepto de "inversión de control" (IoC) y "inyección de dependencias" (DI) en el contexto de Spring.

- Describe la diferencia entre los patrones de diseño Singleton y Prototype en Spring. ¿Cuándo usarías cada uno?

- ¿Cómo se puede implementar la comunicación entre microservicios en una arquitectura basada en Spring Boot? Menciona al menos dos enfoques (síncrono y asíncrono).

## Seguridad

- Describe cómo protegerías una API RESTful creada con Spring Boot. Menciona los componentes clave de Spring Security que utilizarías.

- ¿Qué es un ataque de inyección SQL y cómo se puede prevenir en una aplicación Spring Boot con JPA/Hibernate?

- Explica la importancia de almacenar las contraseñas de los usuarios de forma segura. ¿Qué técnicas o algoritmos utilizarías?

# Sección 2: Ejercicio Práctico de Código

Problema: API para un Sistema de Cuentas Bancarias Simplificado

Debes desarrollar una API REST para gestionar un sistema de cuentas bancarias simple. La API debe permitir la creación de cuentas, la consulta de saldos y la realización de transferencias de dinero entre cuentas.

## Requisitos funcionales

1. Crear una cuenta:

    - POST /accounts : Debe permitir crear una nueva cuenta con un saldo inicial.

2. Consultar una cuenta:

    - GET /accounts/{accountId} : Debe devolver los detalles de una cuenta, incluyendo su
saldo.

3. Realizar una transferencia:

    - POST /transactions : Debe permitir transferir una cantidad de dinero desde una cuenta de origen a una cuenta de destino.

## Requisitos No Funcionales

1. Manejo de Transacciones: La operación de transferencia debe ser atómica. Si ocurre
un error en cualquier punto (por ejemplo, fondos insuficientes o la cuenta de destino
no existe), la transacción completa debe revertirse (rollback).

2. Concurrencia: El sistema debe ser capaz de manejar múltiples solicitudes de transferencia simultáneas sobre la misma cuenta sin generar inconsistencias en los
saldos (por ejemplo, evitar condiciones de carrera)

3. Validación y Manejo de Errores: La API debe validar las solicitudes (por ejemplo, que el monto de la transferencia sea positivo, que la cuenta de origen tenga fondos suficientes) y devolver códigos de estado HTTP y mensajes de error apropiados.

4. Seguridad: Implementa un mecanismo básico de seguridad para los endpoints. Puede ser una autenticación simple basada en un token API en la cabecera de la solicitud.

5. Pruebas: Escribe pruebas unitarias para la lógica de negocio y pruebas de integración para los endpoints de la API.

# Entregables

- El código fuente de la aplicación en un repositorio Git (puedes usar una plataforma
como GitHub o GitLab).

- Un fichero README.md con instrucciones claras sobre cómo compilar, ejecutar y probar
la aplicación. Debe incluir ejemplos de cómo consumir los endpoints de la API


# Sección 3: Resolución de Problemas y Arquitectura

## Escenario: Diseño de un Sistema de Notificaciones de Transacciones

Imagina que trabajas en una fintech que procesa un gran volumen de transacciones. Se te
ha encargado diseñar un sistema que notifique a los usuarios por correo electrónico y SMS
cada vez que se realiza una transacción en su cuenta.

Describe tu enfoque para diseñar este sistema, considerando los siguientes puntos:

- Arquitectura: ¿Cómo integrarías este sistema de notificaciones en la arquitectura
existente? ¿Sería un servicio independiente (microservicio)? ¿Cómo se comunicaría con
el servicio de transacciones?

- Escalabilidad y Rendimiento: El sistema debe ser capaz de manejar picos de miles de
notificaciones por minuto sin afectar el rendimiento del sistema principal de
transacciones. ¿Qué tecnologías o patrones usarías para lograrlo (colas de mensajes,
procesamiento asíncrono, etc.)?

- Fiabilidad: ¿Cómo te asegurarías de que las notificaciones se entreguen de manera
fiable? ¿Qué harías si un servicio de envío de correos electrónicos o SMS falla
temporalmente?

- Flexibilidad: El negocio quiere añadir nuevos canales de notificación en el futuro (por
ejemplo, notificaciones push). ¿Cómo diseñarías el sistema para que sea fácil de
extender?

# Criterios de Evaluación

- Calidad del Código: Claridad, organización, eficiencia y seguimiento de las buenas
prácticas de Java y Spring Boot.

- Funcionalidad: La solución cumple con todos los requisitos del problema práctico.

- Manejo de Transacciones y Concurrencia: Implementación correcta y robusta de la
lógica transaccional y de concurrencia.

- Seguridad: Aplicación de medidas de seguridad adecuadas.

- Pruebas: Cobertura y calidad de las pruebas implementadas.

- Diseño y Arquitectura: Coherencia, escalabilidad y justificación de las decisiones de diseño en la sección de arquitectura.

- Comunicación: Claridad en la documentación y en la explicación de tu solución.