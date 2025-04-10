FROM openjdk:21-jdk-slim AS builder
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM alpine/java:21-jdk
WORKDIR /app
COPY --from=builder /app/target/zlotowka-backend-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
