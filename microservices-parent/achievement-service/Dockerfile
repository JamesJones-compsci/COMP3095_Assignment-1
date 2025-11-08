# -------------------------------------------------------------
# Build stage (JDK 21, Gradle 8) - builds the JAR inside Docker
# -------------------------------------------------------------
FROM gradle:8-jdk21 AS builder
WORKDIR /home/gradle/src

# Copy project files of achievement-service module only
COPY --chown=gradle:gradle . .

# Build the jar (skip tests for speed)
RUN gradle clean bootJar -x test

# -------------------------------------------------------------
# Runtime stage (JRE 24) - runs the built JAR
# -------------------------------------------------------------
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy the jar produced in the builder stage
# NOTE: no "achievement-service/" prefix here; we are already in module context
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/achievement-service.jar

# Spring profile for Docker
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/achievement-service.jar"]
