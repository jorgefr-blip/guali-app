# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y la carpeta libs (donde está guali-dev.jar)
COPY pom.xml .
COPY libs ./libs

# Compilamos directamente (sin go-offline)
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/guali-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]