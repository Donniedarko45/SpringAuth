# Use OpenJDK 17 as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the Maven configuration files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy the source code
COPY src src

# Make the mvnw script executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Create a volume for logs
VOLUME /app/logs

# Command to run the application
CMD ["java", "-jar", "target/auth-system-0.0.1-SNAPSHOT.jar"] 