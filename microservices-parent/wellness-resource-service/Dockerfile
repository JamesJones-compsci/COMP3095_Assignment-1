# ------------------------------------------------------
# Build Stage
# ------------------------------------------------------
FROM gradle:8-jdk24 AS builder

# Copy source code into the image and set correct ownership
COPY --chown=gradle:gradle . /home/gradle/src

# Set the working directory
WORKDIR /home/gradle/src

# Build the project and skip tests for faster build times
RUN ./gradlew clean build -x test

# ------------------------------------------------------
# Package Stage
# ------------------------------------------------------
FROM eclipse-temurin:24-jdk

# CREATE application directory inside container
RUN mkdir /app

# Copy the built JAR file from the builder stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/wellness-service.jar

# Set environment variables for PostgreSQL and Redis
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/wellness-resource-serviceDB \
    SPRING_DATASOURCE_USERNAME=jamesjones \
    SPRING_DATASOURCE_PASSWORD=100898394 \
    SPRING_DATA_REDIS_HOST=redis \
    SPRING_DATA_REDIS_PORT=6379 \
    SPRING_PROFILES_ACTIVE=docker

# Expose service port
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/wellness-service.jar"]