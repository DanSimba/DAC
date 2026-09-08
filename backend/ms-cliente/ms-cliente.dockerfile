#Toda vez que a dockerFile for chamada ela recompila o projeto em um jar

FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

#Copiamos os arquivos locais do maven

COPY .mvn/ .mvn

COPY mvnw pom.xml ./

#Permissao de execucao
RUN chmod +x mvnw


#instalamos as dependencias do projeto novamente
RUN ./mvnw dependency:go-offline


#copiamos o src do nosso projeto
COPY src ./src


#compilamos o projeto para um jar
RUN ./mvnw clean package -DskipTests


#aqui tudo isso foi salvo dentro de "build"


#recriamos o ambiente agora usando a JRE
FROM eclipse-temurin:17-jre

WORKDIR /app

#copiamos o resultado do ambiente anterior para o novo ambeiente
COPY --from=build /app/target/*.jar app.jar

#expondo a porta da api (PRECISAR MAPEAR NO COMPOSER)
EXPOSE 8080

#Inicia a API
ENTRYPOINT [ "java", "-jar", "app.jar" ]
