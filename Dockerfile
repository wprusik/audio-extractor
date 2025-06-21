FROM eclipse-temurin:21-jdk-alpine AS builder

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY *.properties ./

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache ffmpeg

ENV FFMPEG_EXECUTABLE_COMMAND=ffmpeg
ENV MICRONAUT_SERVER_HOST=0.0.0.0
ENV MICRONAUT_SERVER_PORT=8080
ENV MICRONAUT_ROUTER_STATIC_RESOURCES_SWAGGER_ENABLED=true

RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /app/target/audioextractor-*.jar app.jar

USER appuser

EXPOSE 8080

CMD ["java", "-Dmicronaut.server.host=0.0.0.0", "-jar", "app.jar"]
