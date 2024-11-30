FROM openjdk:21-jdk-slim

COPY build/libs/goodbite-0.0.1-SNAPSHOT.jar /app/goodbite.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/goodbite.jar"]
