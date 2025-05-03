FROM openjdk:17-jdk-slim

RUN groupadd --system spring && useradd --system --create-home --gid spring spring
RUN mkdir -p /var/log/app/spring && chown -R spring:spring /var/log/app/spring

WORKDIR /app

ARG JAR_FILE=build/libs/NARI-Backend-0.0.1-SNAPSHOT.jar
COPY --chown=spring:spring ${JAR_FILE} app.jar

USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
