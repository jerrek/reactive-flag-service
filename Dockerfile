FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/FlagService-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]