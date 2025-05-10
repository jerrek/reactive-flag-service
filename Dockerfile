FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/FlagService-1.0.0.jar app.jar
# Добавляем JDBC-драйвер (если нужен для Liquibase)
RUN apt-get update && apt-get install -y libpostgresql-jdbc-java
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]