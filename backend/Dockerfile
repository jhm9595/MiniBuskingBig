# 빌드 스테이지
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Maven Wrapper 복사
COPY mvnw .
COPY .mvn .mvn

# pom.xml 먼저 복사 (의존성 캐싱)
COPY pom.xml .

# 의존성 다운로드
RUN ./mvnw dependency:go-offline

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./mvnw clean package -DskipTests

# 실행 스테이지
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/target/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
