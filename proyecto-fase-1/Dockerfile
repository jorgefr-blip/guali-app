# Etapa 1: Construcción (Java 25)
FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos el pom, la carpeta libs y el código
COPY pom.xml .
COPY libs ./libs
COPY src ./src

# Copiamos el jar a la carpeta local de Maven (así Maven lo encuentra)
RUN mkdir -p /root/.m2/repository/edu/umg/guali/guali-dev/1.0.0 && \
    cp ./libs/guali-dev-1.0.0.jar /root/.m2/repository/edu/umg/guali/guali-dev/1.0.0/guali-dev-1.0.0.jar

# Compilamos
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Java 25)
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/guali-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]