# MiniBuskingBig

iOS, Android, Web 크로스플랫폼 애플리케이션입니다.

- **백엔드**: Java Spring Boot (REST API)
- **프론트엔드 Web**: React + TypeScript + Vite
- **프론트엔드 Mobile**: React Native + Expo (iOS/Android)
- **공유**: API 클라이언트, 타입 정의

## 디렉터리 구조

```
MiniBuskingBig/
 backend/              # Java Spring Boot (포트 8080)
 frontend-web/         # React Web (포트 5173)
 frontend-mobile/      # React Native + Expo (iOS/Android)
 shared/              # 공유 API 클라이언트, 타입
 .github/workflows/   # CI/CD 파이프라인
```

## 빠른 시작

### 1. 백엔드 실행

```powershell
cd backend
mvn spring-boot:run
```

### 2. Web 프론트엔드 실행

```powershell
cd frontend-web
npm install
npm run dev
```

### 3. 모바일 프론트엔드 실행

```powershell
cd frontend-mobile
npm install
npm start
```

## Java 개발 규칙 (실무 체크리스트)

### 1️⃣ 코드 스타일 / 네이밍 규칙

- 클래스명: `PascalCase`
- 변수/메서드명: `camelCase`
- 상수: `UPPER_SNAKE_CASE`
- 패키지명: 소문자, 명사 기준 (e.g., `com.minibuskingbig.controller`)
- 한 파일에는 `public class` 1개만
- 들여쓰기: 스페이스 4칸 (탭 금지)

### 2️⃣ 객체지향(OOP) 원칙 준수

- **SRP**: 클래스는 단일 책임만
- **OCP**: 확장에는 열려있고, 수정에는 닫혀있어야 함
- **DIP**: 구현보다 인터페이스 의존

### 3️⃣ 예외 처리 규칙

- Exception 전체 catch 금지 → 구체적 예외 사용
- 비즈니스 에러와 시스템 에러 명확히 구분
- 커스텀 예외 클래스 사용 (RuntimeException 직접 사용 금지)
- 예외 메시지에는 상황 + 값 포함

### 4️⃣ Spring Boot 규칙

#### 의존성 주입(DI)

- `@Autowired` 필드 주입 금지
- **생성자 주입 필수** (불변성 보장, 테스트 용이)

#### Controller

- Request/Response DTO 분리 (Entity 직접 노출 금지)
- `@Valid`, `@NotNull`, `@NotBlank` 등 Validation 적용
- 요청 경로는 명확한 RESTful 규칙 준수

#### Service

- 비즈니스 로직만 포함
- 필요시 `@Transactional` 적용

#### Repository

- N+1 쿼리 문제 주의
- fetch join, entity graph 활용

### 5️⃣ 설계 / 구조 규칙

- Controller → Service → Repository 계층 분리
- DTO, Entity, VO 역할 구분
- 공통 로직은 AOP 또는 Util로 분리

### 6️⃣ 로깅 규칙

- `System.out.println()` 금지 → `@Slf4j` 사용
- 에러 시 스택트레이스 반드시 출력: `log.error("message", e)`
- 개인 정보(PII) 절대 로그에 남기지 않기

### 7️⃣ 보안 규칙

- `secrets` (.env, application.yml의 비밀번호) Git 커밋 금지
- 비밀번호는 반드시 해싱 (bcrypt, SHA-256 등)
- SQL injection 방지 → Prepared Statement 사용
- CORS 설정은 최소한만 허용

## 다음 단계

- 상태 관리 (Redux, Zustand 등)
- 데이터베이스 연동
- 인증/인가 구현
- 로깅 시스템 (Slf4j + Logback) 통합

```

```
