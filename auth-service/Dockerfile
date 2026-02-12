FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9191

ENTRYPOINT ["java", "-jar", "app.jar"]
