# Etapa 1: Construcción (Java 25)
FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos el pom y la carpeta libs
COPY pom.xml .
COPY libs ./libs

# Instalamos el jar local en el repositorio de Maven del contenedor
RUN mvn install:install-file -Dfile=./libs/guali-dev-1.0.0.jar -DgroupId=edu.umg.guali -DartifactId=guali-dev -Dversion=1.0.0 -Dpackaging=jar

# Compilamos
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Java 25)
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/guali-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]