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

### 🐳 Docker로 실행 (권장)

```bash
# 전체 애플리케이션 실행
docker-compose up

# 백그라운드에서 실행
docker-compose up -d

# 종료
docker-compose down
```

**접속:**
- 프론트엔드: http://localhost
- 백엔드 API: http://localhost:8080

### 💻 로컬 환경에서 실행

#### 1. 백엔드 실행

```powershell
cd backend
mvn spring-boot:run
```

#### 2. Web 프론트엔드 실행

```powershell
cd frontend-web
npm install
npm run dev
```

**접속:** http://localhost:5173

#### 3. 모바일 프론트엔드 실행

```powershell
cd frontend-mobile
npm install
npm start
```

## 프론트엔드 커스텀 훅

### 📌 개요

Web/Mobile에서 API 호출과 상태 관리를 일관되게 처리하기 위해 커스텀 훅을 제공합니다.

### `useApi<T>` - 수동 API 호출

API를 직접 제어해야 할 때 사용합니다.

```tsx
// Web / Mobile 동일 사용
import { useApi } from "./hooks";

export function MyComponent() {
  const { data, loading, error, refetch } = useApi<UserData>("/api/users/1");

  return (
    <div>
      {loading && <span>로딩 중...</span>}
      {error && <span>에러: {error}</span>}
      {data && <p>사용자: {data.name}</p>}
      <button onClick={refetch}>다시 불러오기</button>
    </div>
  );
}
```

**API**:

- `data: T | null` - 응답 데이터
- `loading: boolean` - 로딩 상태
- `error: string | null` - 에러 메시지
- `refetch: () => Promise<void>` - 수동 재요청

**옵션**:

```tsx
{
  skip?: boolean;           // true면 요청 건너뜀
  onSuccess?: (data: T) => void;  // 성공 콜백
  onError?: (error: Error) => void;  // 에러 콜백
}
```

### `useFetch<T>` - 자동 데이터 페칭

컴포넌트 마운트 시 자동으로 데이터를 가져옵니다.

```tsx
// Web
import { useFetch } from "./hooks";

export function App() {
  const { data: msg, loading, error } = useFetch<string>("/api/hello");

  return (
    <div>
      {loading && <p>Loading...</p>}
      {error && <p style={{ color: "red" }}>Error: {error}</p>}
      {!loading && msg && <p>Backend says: {msg}</p>}
    </div>
  );
}
```

```tsx
// Mobile
import { useFetch } from "./hooks";
import { View, Text, ActivityIndicator } from "react-native";

export function App() {
  const { data: msg, loading, error } = useFetch<string>("/api/hello");

  return (
    <View>
      {loading && <ActivityIndicator size="large" />}
      {error && <Text style={{ color: "red" }}>Error: {error}</Text>}
      {!loading && msg && <Text>Backend says: {msg}</Text>}
    </View>
  );
}
```

**콜백 예시**:

```tsx
const { data, loading, error } = useFetch<User>("/api/user", {
  skip: false,
  onSuccess: (user) => {
    console.log("사용자 로드됨:", user.name);
  },
  onError: (err) => {
    console.error("실패:", err.message);
  },
});
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

## 🚀 환경 세팅

### 초기 환경 설정 (최초 1회)

```bash
# Linux/Mac
chmod +x scripts/setup-env.sh
./scripts/setup-env.sh

# Windows PowerShell
.\scripts\setup-env.ps1
```

이 스크립트는 다음을 자동으로 설정합니다:
- Git develop 브랜치 생성
- Docker 환경 확인
- 환경 변수 파일 생성
- .gitignore 업데이트

### Git 브랜치 전략

자세한 내용은 [docs/GIT_WORKFLOW.md](docs/GIT_WORKFLOW.md)를 참조하세요.

```bash
# develop 브랜치에서 새로운 기능 개발
git checkout develop
git checkout -b feature/기능명

# 작업 후 커밋
git add .
git commit -m "feat(스코프): 기능 설명"

# Push 및 PR
git push -u origin feature/기능명
```

## 🐳 Docker 사용법

### 프로덕션 빌드

```bash
# 이미지 빌드
docker-compose build

# 실행
docker-compose up
```

### 개발 모드 (핫 리로드)

```bash
# 개발 환경으로 실행
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### Docker 명령어

```bash
# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f

# 특정 서비스만 재시작
docker-compose restart backend

# 컨테이너 및 볼륨 삭제
docker-compose down -v
```

## 📁 프로젝트 구조 (상세)

```
MiniBuskingBig/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/java/com/minibuskingbig/
│   │   │   ├── controller/    # REST API 컨트롤러
│   │   │   ├── service/       # 비즈니스 로직
│   │   │   ├── repository/    # 데이터베이스 레이어
│   │   │   ├── dto/          # Data Transfer Objects
│   │   │   └── config/       # 설정 (CORS 등)
│   │   └── resources/
│   ├── Dockerfile            # 프로덕션 Dockerfile
│   └── pom.xml              # Maven 설정
│
├── frontend-web/              # React Web 프론트엔드
│   ├── src/
│   │   ├── hooks/           # 커스텀 훅 (useApi, useFetch)
│   │   ├── components/      # React 컴포넌트
│   │   └── App.tsx
│   ├── Dockerfile           # 프로덕션 Dockerfile
│   ├── nginx.conf           # Nginx 설정 (프록시)
│   ├── vite.config.ts       # Vite 설정 (프록시)
│   └── package.json
│
├── frontend-mobile/          # React Native 모바일
│   └── ...
│
├── shared/                   # 공유 코드
│   └── api-client.ts        # API 클라이언트
│
├── docs/                    # 문서
│   ├── GIT_WORKFLOW.md     # Git 브랜치 전략
│   └── SETUP.md            # 환경 설정 가이드
│
├── scripts/                # 유틸리티 스크립트
│   ├── setup-env.sh       # 환경 세팅 (Linux/Mac)
│   └── setup-env.ps1      # 환경 세팅 (Windows)
│
├── docker-compose.yml      # Docker Compose (프로덕션)
├── docker-compose.dev.yml  # Docker Compose (개발)
└── .gitignore
```

## 다음 단계

- 상태 관리 (Redux, Zustand 등)
- 데이터베이스 연동
- 인증/인가 구현
- 로깅 시스템 (Slf4j + Logback) 통합
