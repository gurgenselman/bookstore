# Use a base image with Java and Maven installed
FROM openjdk:17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven executable JAR to the container
COPY target/*.jar /app/app.jar

# Expose the port that the application runs on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "app.jar"]