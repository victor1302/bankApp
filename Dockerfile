# Etapa de build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
# Etapa de execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar bankApp.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","bankApp.jar"]