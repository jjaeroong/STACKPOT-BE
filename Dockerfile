# OpenJDK 17 이미지를 기반으로 빌드
FROM eclipse-temurin:17

# 빌드 시 사용할 ARG 선언
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG JWT_SECRET
ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET
ARG MAIL_PASSWORD
ARG MAIL_USERNAME
ARG PASSWORD
ARG URL
ARG OPEN_API_KEY
ARG USERNAME

# 런타임 환경변수로 설정 (ENV로 선언)
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
    AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
    JWT_SECRET=$JWT_SECRET \
    KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID \
    KAKAO_CLIENT_SECRET=$KAKAO_CLIENT_SECRET \
    MAIL_PASSWORD=$MAIL_PASSWORD \
    MAIL_USERNAME=$MAIL_USERNAME \
    PASSWORD=$PASSWORD \
    URL=$URL \
    OPEN_API_KEY=$OPEN_API_KEY \
    USERNAME=$USERNAME

# JAR 파일 경로 설정
ARG JAR_FILE=build/libs/stackpot-0.0.1-SNAPSHOT.jar

# JAR 파일을 컨테이너에 복사
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
