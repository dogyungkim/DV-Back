FROM amazoncorretto:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일과 설정 파일 복사
ARG JAR_FILE=build/libs/DV-Back-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

#서버 환경 설정
ARG ENVIRONMENT=dev
ENV ENVIRONMENT=${ENVIRONMENT}

# 로그 디렉토리 생성
RUN mkdir /app/logs

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${ENVIRONMENT}"]