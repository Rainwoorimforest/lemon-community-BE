# Stage 1: Build Spring Boot Jar
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle files
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle

# Copy source code
COPY src src

# Make gradle wrapper executable and build bootJar
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Stage 2: Run application with JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]