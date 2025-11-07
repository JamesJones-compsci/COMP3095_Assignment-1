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
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/event-service.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://event-postgres:5433/event-serviceDB
ENV SPRING_DATASOURCE_USERNAME=omar
ENV SPRING_DATASOURCE_PASSWORD=eventservice
ENV SPRING_PROFILES_ACTIVE=docker

# Expose service port
EXPOSE 8033

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/event-service.jar"]