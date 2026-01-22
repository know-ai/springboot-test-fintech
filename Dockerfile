#
# Producción: build multi-stage (compile + runtime)
#

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Mejor cache: primero metadata de dependencias
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Ahora código fuente
COPY src ./src

RUN mvn -q -DskipTests clean package


FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuario no-root
RUN useradd -r -u 10001 -g root appuser
USER 10001

COPY --from=build /app/target/springboot-test-fintech-*.jar /app/app.jar

EXPOSE 8080

# Perfil postgres por defecto para contenedores; override por env si se requiere.
ENV SPRING_PROFILES_ACTIVE=postgres

ENTRYPOINT ["java","-jar","/app/app.jar"]


