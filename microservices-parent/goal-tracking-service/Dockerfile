# ------
# Build Stage
# ------

# Gradle is our build tool
FROM gradle:8-jdk24 AS builder

# Copy all the source code for this microservice here
COPY --chown=gradle:gradle . /home/gradle/src

# Go inside your working directory
WORKDIR /home/gradle/src

# Run our Build - Call gradle to execute your build
# I want to compile my code, create an image, but not run the tests, that's a waste of time
# A docker image is a lightweight package that contains everything needed to run your app
# -x test means I am goign to exclude all tests - don't run any tests

# Clear Gradle’s local config that references Windows JDK
RUN sed -i '/org.gradle.java.home/d' gradle.properties || true
RUN ./gradlew build -x test

# ------
# Package Stage
# ------

FROM eclipse-temurin:24-jdk

# can be any name
RUN mkdir /app

# After the build there should be an artifact here, the .jar file inside the libs folder
# *.jar becasue we dont want to hardcode what the name of the jar file might be
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/goal-tracking-service.jar

# Set enviroment variables (should be overwritten)
ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PASSWORD=password

# Set a default port
EXPOSE 8084

# Tell java, run a jar file, the one specified here
ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]