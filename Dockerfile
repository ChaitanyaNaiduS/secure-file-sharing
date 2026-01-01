# 1. Start with a Java 17 base image (same version you use locally)
FROM openjdk:17-jdk-slim

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the compiled Java app (JAR file) into the container
# Note: Make sure you have run 'mvn clean package' first!
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose the port the app runs on
EXPOSE 8080

# 5. The command to run the app when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]