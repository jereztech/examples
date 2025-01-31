# Stage 1: Build the application using Gradle
FROM gradle:7.6.2-jdk17 AS build

# Set the working directory inside the container
WORKDIR /home/gradle/project

# Copy Gradle wrapper and build scripts
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Copy the application source code
COPY src src

# Build the application
RUN ./gradlew clean build --no-daemon

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /home/gradle/project/build/libs/orders-spring-kafka-0.0.1-SNAPSHOT.jar .

# Enable remote debugging
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

EXPOSE 8080 5005

# Set the entry point to run the application
ENTRYPOINT java $JAVA_OPTS -jar orders-spring-kafka-0.0.1-SNAPSHOT.jar
