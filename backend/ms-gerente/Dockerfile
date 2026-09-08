FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

RUN chmod +x mvnw
##Lembrete, precisa ter o -DskipTests pq quando ele ta rodando os teste ele tenta conectar em db que ainda não ta na rede
RUN ./mvnw clean package -DskipTests  

EXPOSE 8084

CMD ["sh", "-c", "java -jar target/*.jar"]