FROM openjdk:21-jre-slim

LABEL authors="white"

COPY build/libs/goodbite-0.0.1-SNAPSHOT.jar /app/goodbite.jar

ENV DB_HOST=${DB_HOST} \
    DB_PORT=${DB_PORT} \
    DB_NAME=${DB_NAME} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    JWT_SECRET_KEY=${JWT_SECRET_KEY} \
    PUBLIC_DATA_KEY=${PUBLIC_DATA_KEY}

ENTRYPOINT ["java", "-jar", "/app/goodbite.jar"]
