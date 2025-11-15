# 프로젝트 템플릿 사용 가이드

이 리포지토리는 **Spring Boot + Next.js + React Native** 풀스택 프로젝트 템플릿입니다.

## 📋 템플릿 특징

### 기술 스택
- **백엔드**: Java 21 + Spring Boot 3.1.4
- **프론트엔드 Web**: Next.js 14 + TypeScript + App Router
- **프론트엔드 Mobile**: React Native + Expo
- **공유 코드**: TypeScript API 클라이언트
- **컨테이너화**: Docker + Docker Compose
- **Git 워크플로우**: Main/Develop/Feature 브랜치 전략

### 주요 기능
- ✅ Docker 기반 개발 환경
- ✅ Next.js SSR/SSG 지원
- ✅ API 프록시 설정 (CORS 해결)
- ✅ 커스텀 훅 (useFetch, useApi)
- ✅ Git 브랜치 전략 및 자동화 스크립트
- ✅ 실무 중심의 코딩 규칙

## 🚀 새 프로젝트 시작하기

### 방법 1: GitHub 템플릿으로 사용

1. GitHub에서 이 리포지토리 페이지로 이동
2. 우측 상단의 **"Use this template"** 버튼 클릭
3. 새 리포지토리 이름 입력 (예: `MyNewProject`)
4. **"Create repository from template"** 클릭

### 방법 2: 수동으로 복사

```bash
# 1. 템플릿 리포지토리 클론
git clone https://github.com/jhm9595/MiniBuskingBig.git MyNewProject
cd MyNewProject

# 2. Git 히스토리 초기화 (선택사항)
rm -rf .git
git init
git add .
git commit -m "Initial commit from template"

# 3. 새 리모트 리포지토리 연결
git remote add origin https://github.com/your-username/MyNewProject.git
git push -u origin main
```

## 🔧 프로젝트 커스터마이징

### 1. 프로젝트 이름 변경

다음 파일들에서 `MiniBuskingBig`를 새 프로젝트 이름으로 변경하세요:

#### Backend (Java)
- `backend/pom.xml`
  ```xml
  <groupId>com.yournewproject</groupId>
  <artifactId>yournewproject-backend</artifactId>
  <name>YourNewProject Backend</name>
  ```

- `backend/src/main/java/` 디렉토리 구조
  ```
  com/minibuskingbig/ → com/yournewproject/
  ```

- `backend/src/main/resources/application.yml`
  ```yaml
  spring:
    application:
      name: yournewproject-backend
  ```

#### Frontend (Next.js)
- `frontend-web/package.json`
  ```json
  {
    "name": "yournewproject-frontend-web",
    "version": "1.0.0"
  }
  ```

#### Docker
- `docker-compose.yml`
  ```yaml
  services:
    backend:
      container_name: yournewproject-backend
    frontend-web:
      container_name: yournewproject-frontend-web
  ```

#### 문서
- `README.md` 상단 타이틀 수정
- `docs/GIT_WORKFLOW.md` 예시 경로 수정

### 2. 패키지 구조 변경

```bash
# Backend 패키지 이름 변경
cd backend/src/main/java
mv com/minibuskingbig com/yournewproject

# 모든 Java 파일에서 패키지 import 수정
find . -name "*.java" -exec sed -i 's/com.minibuskingbig/com.yournewproject/g' {} +
```

### 3. 데이터베이스 설정 (필요 시)

`backend/src/main/resources/application.yml` 또는 `.env` 파일에 추가:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yourdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

`docker-compose.yml`에 PostgreSQL 서비스 추가:

```yaml
services:
  database:
    image: postgres:15-alpine
    container_name: yournewproject-db
    environment:
      POSTGRES_DB: yourdb
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - app-network

volumes:
  postgres-data:
```

### 4. 환경 변수 설정

```bash
# 환경 세팅 스크립트 실행
# Linux/Mac
./scripts/setup-env.sh

# Windows
.\scripts\setup-env.ps1
```

생성된 `.env` 파일들을 프로젝트에 맞게 수정하세요.

## 📂 템플릿 구조 이해하기

```
YourNewProject/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/yournewproject/
│   │   ├── controller/        # REST API 엔드포인트
│   │   ├── service/           # 비즈니스 로직
│   │   ├── repository/        # 데이터 레이어
│   │   ├── dto/              # 데이터 전송 객체
│   │   └── config/           # 설정 (CORS, Security 등)
│   └── Dockerfile
│
├── frontend-web/              # Next.js 웹 프론트엔드
│   ├── src/
│   │   ├── app/              # Next.js App Router
│   │   ├── hooks/            # 재사용 가능한 커스텀 훅
│   │   └── components/       # React 컴포넌트
│   └── next.config.js        # Next.js 설정
│
├── frontend-mobile/          # React Native 모바일
│   └── ...
│
├── shared/                   # 공유 코드
│   └── api-client.ts        # API 클라이언트
│
├── docs/                    # 문서
│   ├── GIT_WORKFLOW.md     # Git 워크플로우
│   └── SETUP.md            # 환경 설정
│
├── scripts/                # 자동화 스크립트
│   ├── setup-env.sh
│   └── setup-env.ps1
│
└── docker-compose.yml      # Docker 오케스트레이션
```

## 🎯 개발 시작하기

### 1. Docker로 실행 (권장)

```bash
# 전체 스택 실행
docker-compose up

# 백그라운드 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 종료
docker-compose down
```

**접속:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

### 2. 로컬 개발 환경

#### Backend
```bash
cd backend
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend-web
npm install
npm run dev
```

### 3. Git 워크플로우

```bash
# Develop 브랜치 생성 (최초 1회)
git checkout -b develop
git push -u origin develop

# 새 기능 개발
git checkout develop
git checkout -b feature/your-feature-name

# 작업 후 커밋
git add .
git commit -m "feat(scope): 기능 설명

- 상세 내용

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"

# Push 및 PR
git push -u origin feature/your-feature-name
```

자세한 내용은 [docs/GIT_WORKFLOW.md](docs/GIT_WORKFLOW.md)를 참조하세요.

## 🛠️ 확장 가능한 기능

이 템플릿은 다음 기능들을 쉽게 추가할 수 있도록 설계되었습니다:

### 인증/인가
- Spring Security + JWT
- OAuth 2.0 (Google, GitHub 등)
- Role-based Access Control (RBAC)

### 데이터베이스
- PostgreSQL, MySQL, MongoDB 연동
- JPA/Hibernate ORM
- Flyway/Liquibase 마이그레이션

### 상태 관리
- Redux Toolkit
- Zustand
- React Query

### API 문서화
- Swagger/OpenAPI
- Spring REST Docs

### 테스트
- JUnit 5 (Backend)
- Jest + React Testing Library (Frontend)
- Testcontainers (통합 테스트)

### CI/CD
- GitHub Actions (이미 설정됨)
- Jenkins
- GitLab CI

### 모니터링
- Spring Boot Actuator
- Prometheus + Grafana
- ELK Stack (Elasticsearch, Logstash, Kibana)

## 📚 코딩 규칙

이 템플릿은 실무 중심의 코딩 규칙을 따릅니다:

### Java (Backend)
- 생성자 주입 필수 (`@Autowired` 필드 주입 금지)
- DTO와 Entity 분리
- 예외 처리 명확히 (커스텀 예외 사용)
- `@Slf4j` 로깅 (`System.out.println()` 금지)

자세한 내용은 [README.md](README.md#java-개발-규칙-실무-체크리스트)를 참조하세요.

### TypeScript (Frontend)
- 명시적 타입 정의
- 커스텀 훅 재사용
- 컴포넌트 단일 책임 원칙

## 🆘 문제 해결

### CORS 에러
- Next.js `next.config.js`에서 API rewrites 확인
- Backend `CorsConfig.java`에서 허용 도메인 확인

### Docker 빌드 실패
```bash
# 캐시 없이 재빌드
docker-compose build --no-cache

# 볼륨 삭제 후 재시작
docker-compose down -v
docker-compose up
```

### 포트 충돌
```bash
# Windows
netstat -ano | findstr :5173
taskkill /PID [PID번호] /F

# Linux/Mac
lsof -ti:5173 | xargs kill -9
```

## 🤝 기여 방법

이 템플릿을 개선하고 싶으시다면:

1. 원본 리포지토리 Fork
2. Feature 브랜치 생성
3. 변경사항 커밋
4. Pull Request 생성

## 📄 라이선스

이 템플릿은 자유롭게 사용 가능합니다.

## 🎉 다음 단계

1. ✅ 프로젝트 이름 변경
2. ✅ 환경 변수 설정
3. ✅ 데이터베이스 연동 (필요 시)
4. ✅ 첫 번째 기능 개발 시작!

Happy Coding! 🚀
