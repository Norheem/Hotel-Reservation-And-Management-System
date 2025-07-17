## -------- Build stage --------
#FROM maven:3.8.5-eclipse-temurin-17 AS build
#
#WORKDIR /app
#COPY . .
#RUN ./mvnw clean package -DskipTests
#
## -------- Run stage --------
#FROM eclipse-temurin:17-jdk-alpine
#
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# -------- Build stage --------
FROM maven:3.8.5-eclipse-temurin-17 AS build
COPY . /workspace
WORKDIR /workspace
RUN mvn clean package -DskipTests

# -------- Run stage --------
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /workspace/target/hotel-reservation-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
