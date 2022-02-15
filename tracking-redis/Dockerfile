ARG TRACKING_REDIS="tracking-redis"
ARG TRACKING_CORE="tracking-core"

FROM gradle:7.4-jdk17-alpine as builder

ARG TRACKING_REDIS
ARG TRACKING_CORE

COPY $TRACKING_CORE/build.gradle.kts ./$TRACKING_CORE/build.gradle.kts
COPY $TRACKING_CORE/settings.gradle.kts ./$TRACKING_CORE/settings.gradle.kts
COPY $TRACKING_CORE/src ./$TRACKING_CORE/src

COPY $TRACKING_REDIS/build.gradle.kts ./$TRACKING_REDIS/build.gradle.kts
COPY $TRACKING_REDIS/settings.gradle.kts ./$TRACKING_REDIS/settings.gradle.kts
COPY $TRACKING_REDIS/src ./$TRACKING_REDIS/src


# Build a release artifact.
RUN gradle -p ./$TRACKING_REDIS clean build -x test --no-daemon --stacktrace --info

# actual container
FROM openjdk:17-alpine

ARG TRACKING_REDIS

ENV JAR_FILE="tracking-redis-1.0.jar"

COPY --from=builder /home/gradle/$TRACKING_REDIS/build/libs/$JAR_FILE app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]