FROM gradle:7.4-jdk17-alpine as builder

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

# Build a release artifact.
RUN gradle clean build --no-daemon

# actual container
FROM openjdk:17-alpine
ENV JAR_FILE="service-discovery-1.0.jar"

COPY --from=builder /home/gradle/build/libs/$JAR_FILE app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]