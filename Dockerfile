# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY backend-springboot/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY backend-springboot/src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/pos-system-1.0.0.jar app.jar

ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "app.jar"]
