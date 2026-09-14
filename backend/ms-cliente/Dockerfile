# Etapa 1: Build da aplicação com Maven Wrapper
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven Wrapper e o pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Dá permissão de execução para o script do maven wrapper
RUN chmod +x mvnw

# Baixa as dependências offline
RUN ./mvnw dependency:go-offline

# Copia o restante do código fonte e faz o build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagem final para rodar a aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]