# Stage 1: Build the application with a full JDK
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files to take advantage of Docker layer caching.
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy the Checkstyle config directory, which is needed by the build process.
COPY config ./config

# Copy the source code. This will invalidate the cache for this layer whenever a source file changes.
COPY src ./src

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Run the build, skipping tests as they are already run in the pipeline
RUN ./gradlew clean build -x test


# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/build/libs/*.jar ./app.jar

# Expose the application port
EXPOSE 8080

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]