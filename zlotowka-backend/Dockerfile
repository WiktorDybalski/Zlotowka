FROM alpine/java:21-jdk
ENV PORT=8080
COPY target/zlotowka-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]


