FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew

RUN --mount=type=cache,target=/home/gradle/.gradle ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/home/gradle/.gradle ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-jar", "app.jar"]