# MiniBuskingBig

버스킹 공연 플랫폼 - 가수와 관객을 연결하는 라이브 공연 서비스

## 프로젝트 개요

MiniBuskingBig은 버스킹 가수들이 자신의 공연을 등록하고, 관객들이 실시간으로 공연을 탐색하고 참여할 수 있는 플랫폼입니다.

### 주요 기능

- 소셜 로그인 (Google, Kakao, Naver)
- 공연 등록 및 관리
- 실시간 공연 탐색 (라이브/예정/전체)
- 가수 프로필 관리
- JWT 기반 인증

## 기술 스택

### Backend
- **Framework**: Spring Boot 3.1.4
- **Language**: Java 21
- **Database**: MySQL (PlanetScale)
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + OAuth2 + JWT
- **Query**: QueryDSL
- **Migration**: Flyway
- **Cache**: Redis

### Frontend
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **State**: React Hooks

### Infra
- **Containerization**: Docker
- **Cloud**: AWS (ECS Fargate for chat)
- **Storage**: AWS S3

## 프로젝트 구조

```
MiniBuskingBig/
├── backend/                # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/minibuskingbig/
│   │       ├── auth/       # 인증 관련
│   │       ├── user/       # 사용자 관리
│   │       ├── singer/     # 가수 프로필
│   │       ├── event/      # 공연 관리
│   │       ├── team/       # 팀 관리
│   │       ├── song/       # 곡 관리
│   │       └── common/     # 공통 모듈
│   └── src/main/resources/
│       └── application.yml
├── frontend-web/           # Next.js 프론트엔드
│   ├── src/
│   │   ├── app/           # Next.js App Router
│   │   │   ├── page.tsx       # 홈 (공연 목록)
│   │   │   ├── login/         # 로그인
│   │   │   └── auth/callback/ # OAuth 콜백
│   │   └── lib/
│   │       └── api/           # API 클라이언트
│   └── public/
└── docs/                   # 문서
    ├── DATABASE_SCHEMA.md
    └── ARCHITECTURE.md
```

## 시작하기

### 사전 요구사항

- Java 21
- Node.js 18+
- MySQL 8.0
- Redis

### 백엔드 실행

```bash
cd backend

# application-dev.yml 설정
# - JWT secret key
# - OAuth2 client credentials (Google/Kakao/Naver)
# - Database connection

# 개발 모드 (H2 in-memory)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 프로덕션 모드 (MySQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### 프론트엔드 실행

```bash
cd frontend-web

# 환경 변수 설정
cp .env.local.example .env.local
# NEXT_PUBLIC_API_URL=http://localhost:8080

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

## API 문서

백엔드 서버 실행 후:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

## 주요 엔드포인트

### 인증
- `POST /api/v1/auth/refresh` - 토큰 갱신
- `GET /api/v1/auth/me` - 현재 사용자 정보

### 공연
- `GET /api/v1/events` - 전체 공연 목록
- `GET /api/v1/events/live` - 라이브 공연
- `GET /api/v1/events/upcoming` - 예정된 공연
- `POST /api/v1/events` - 공연 등록 (인증 필요)
- `PUT /api/v1/events/{id}` - 공연 수정
- `DELETE /api/v1/events/{id}` - 공연 취소

### 가수 프로필
- `POST /api/v1/singers/profile` - 프로필 생성
- `GET /api/v1/singers/{id}` - 프로필 조회
- `PUT /api/v1/singers/profile` - 프로필 수정

## 개발 현황

### ✅ 완료된 기능 (MVP)

- [x] Entity 설계 및 구현 (User, Event, SingerProfile, Team, Song)
- [x] Repository 레이어
- [x] 공통 예외 처리 (BusinessException, ErrorCode, GlobalExceptionHandler)
- [x] JWT 인증 시스템 (JwtTokenProvider, JwtAuthenticationFilter)
- [x] OAuth2 소셜 로그인 (Google/Kakao/Naver)
- [x] Security 설정
- [x] Event CRUD API
- [x] SingerProfile API
- [x] Auth API (refresh token, user info)
- [x] 프론트엔드 공연 목록 페이지
- [x] 소셜 로그인 UI
- [x] OAuth2 콜백 처리

### 📝 TODO (향후 구현)

#### Phase 2: 채팅 시스템
- [ ] AWS ECS Fargate 컨테이너 관리
- [ ] WebSocket STOMP 메시징
- [ ] 채팅방 생성/종료 자동화
- [ ] 채팅 결제 시스템

#### Phase 3: 결제 시스템
- [ ] VIP 구독 결제
- [ ] 광고 제거 결제
- [ ] 채팅 이용 결제
- [ ] PG 연동 (토스페이먼츠)

#### Phase 4: 관객 기능
- [ ] 공연 즐겨찾기
- [ ] 가수 팔로우
- [ ] 아바타 커스터마이징
- [ ] 알림 설정

#### Phase 5: 장소 제공자
- [ ] 장소 등록 및 관리
- [ ] 장소 예약 시스템
- [ ] 장소 수익 관리

#### Phase 6: 광고주 기능
- [ ] 광고 캠페인 생성
- [ ] 타겟팅 설정
- [ ] 광고 성과 분석

#### Phase 7: 관리자 기능
- [ ] 사용자 관리
- [ ] 신고 처리
- [ ] 통계 대시보드

## 데이터베이스 스키마

자세한 스키마 정보는 [DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md) 참고

## 아키텍처

자세한 아키텍처 정보는 [ARCHITECTURE.md](docs/ARCHITECTURE.md) 참고

## 라이선스

MIT License

## 기여

이 프로젝트는 개인 학습 프로젝트입니다.
