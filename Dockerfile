FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일과 설정 파일 복사
ARG JAR_FILE=build/libs/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar.

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]