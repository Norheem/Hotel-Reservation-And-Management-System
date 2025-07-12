# -------- Build stage --------
FROM maven:3.8.5-eclipse-temurin-17 AS build

# Copy all project files and build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# -------- Run stage --------
FROM eclipse-temurin:17-jdk-alpine

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose port (default Spring Boot port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
