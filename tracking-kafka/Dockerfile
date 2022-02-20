ARG TRACKING_KAFKA="tracking-kafka"
ARG TRACKING_CORE="tracking-core"

FROM gradle:7.4-jdk17-alpine as builder

ARG TRACKING_KAFKA
ARG TRACKING_CORE

COPY $TRACKING_CORE/build.gradle.kts ./$TRACKING_CORE/build.gradle.kts
COPY $TRACKING_CORE/settings.gradle.kts ./$TRACKING_CORE/settings.gradle.kts
COPY $TRACKING_CORE/src ./$TRACKING_CORE/src

COPY $TRACKING_KAFKA/build.gradle.kts ./$TRACKING_KAFKA/build.gradle.kts
COPY $TRACKING_KAFKA/settings.gradle.kts ./$TRACKING_KAFKA/settings.gradle.kts
COPY $TRACKING_KAFKA/src ./$TRACKING_KAFKA/src


# Build a release artifact.
RUN gradle -p ./$TRACKING_KAFKA clean build -x test --no-daemon --stacktrace --info

# actual container
FROM openjdk:17-alpine

ARG TRACKING_KAFKA

ENV JAR_FILE="tracking-kafka-1.0.jar"

COPY --from=builder /home/gradle/$TRACKING_KAFKA/build/libs/$JAR_FILE app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]