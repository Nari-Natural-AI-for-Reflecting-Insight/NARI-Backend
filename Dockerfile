FROM openjdk:17-jdk-slim
WORKDIR /app

ARG JAR_FILE=build/libs/NARI-Backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./app.jar

# 앱이 사용할 포트
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]