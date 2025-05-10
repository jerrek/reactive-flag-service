FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/feature-toggle-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]