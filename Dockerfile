# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Mise à jour du nom du JAR
COPY --from=build /app/target/emotional-guard-v2-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]