# Dockerfile (place at backend root)

# ---------- Stage 1: Build the app ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy maven wrapper + pom first (leverage Docker cache)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x ./mvnw

# Copy source and build
COPY src src
RUN ./mvnw -B -DskipTests package

# ---------- Stage 2: Run the app ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the fat jar produced by the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the Spring Boot app listens on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
