FROM openjdk:21-jdk-slim

LABEL authors="white"

COPY build/libs/goodbite-0.0.1-SNAPSHOT.jar /app/goodbite.jar
COPY src/main/resources/api.goodbite.site.p12 /app/api.goodbite.site.p12

EXPOSE 443

ENV DB_HOST=${DB_HOST} \
    DB_PORT=${DB_PORT} \
    DB_NAME=${DB_NAME} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    JWT_SECRET_KEY=${JWT_SECRET_KEY} \
    PUBLIC_DATA_KEY=${PUBLIC_DATA_KEY} \
    EC2_HOST=${EC2_HOST} \
    DOMAIN_URL=${DOMAIN_URL} \
    SUBDOMAIN_URL=${SUBDOMAIN_URL} \
    KAKAO_API_KEY=${KAKAO_API_KEY} \
    S3_BUCKET_NAME=${S3_BUCKET_NAME} \
    S3_REGION=${S3_REGION} \
    S3_ACCESS_KEY=${S3_ACCESS_KEY} \
    S3_SECRET_KEY=${S3_SECRET_KEY} \
    REDIS_SERVER=${REDIS_SERVER} \
    REDIS_PASSWORD=${REDIS_PASSWORD} \
    REDIS_PORT=${REDIS_PORT} \
    SSL_KEY=${SSL_KEY}

ENTRYPOINT ["java", "-jar", "/app/goodbite.jar"]
