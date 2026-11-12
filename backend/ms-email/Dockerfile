# Etapa 1: Build da aplicação com Maven Wrapper
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache ca-certificates

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]