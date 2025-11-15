# 버스킹 플랫폼 아키텍처 설계

## 1. 시스템 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────┐
│                     클라이언트 레이어                          │
├─────────────────────────────────────────────────────────────┤
│  Next.js Web App          │      React Native Mobile        │
│  - SSR/SSG/CSR            │      - iOS / Android           │
│  - WebPush                │      - Push Notification       │
└─────────────────────────────────────────────────────────────┘
                            ▼ HTTPS/WSS
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                            │
│                   (Spring Boot Backend)                     │
├─────────────────────────────────────────────────────────────┤
│  Auth    │ Event │ Chat  │ Venue │  Ad   │ Admin │ Payment │
│  Service │Service│Service│Service│Service│Service│ Service │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    데이터 레이어                              │
├─────────────────────────────────────────────────────────────┤
│  MySQL/PlanetScale  │  Redis Cache  │  Cloudflare R2       │
│  (핵심 데이터)        │  (세션/캐시)   │  (이미지/영상)        │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                채팅 컨테이너 레이어 (AWS Fargate)              │
├─────────────────────────────────────────────────────────────┤
│  [Event 1]  │  [Event 2]  │  [Event 3]  │     ...         │
│  WebSocket  │  WebSocket  │  WebSocket  │                 │
│  Container  │  Container  │  Container  │                 │
│  (자동 생성/삭제)                                              │
└─────────────────────────────────────────────────────────────┘
```

## 2. 핵심 설계 원칙

### 2.1 운영비 최소화
- **DB 필드 최소화**: 핵심 필드만 정의, 확장은 JSON 컬럼 활용
- **서버리스 채팅**: 공연 시간에만 Docker 컨테이너 실행 (AWS Fargate)
- **CDN 활용**: 정적 리소스는 Cloudflare CDN
- **캐싱 전략**: Redis로 자주 조회되는 데이터 캐싱

### 2.2 확장성
- **마이크로서비스 지향**: 서비스별 모듈 분리
- **JSON 기반 확장**: 새 필드 추가 시 스키마 변경 없이 JSON 활용
- **수평 확장**: 채팅 컨테이너 Auto Scaling

### 2.3 성능
- **Next.js SSG**: 공연 목록 등 정적 페이지는 빌드 시 생성
- **Next.js ISR**: 공연 정보는 Incremental Static Regeneration
- **WebSocket**: 실시간 채팅은 WebSocket 프로토콜

## 3. 기술 스택 상세

### 3.1 프론트엔드
```yaml
Framework: Next.js 14 (App Router)
Language: TypeScript
Styling: Tailwind CSS
State: Zustand (경량 상태관리)
Real-time: Socket.io-client
Maps: Kakao Maps API
QR: qrcode.react
Payment:
  - KakaoPay SDK
  - NaverPay SDK
  - Apple Pay (iOS)
Push:
  - Web Push API
  - FCM (Firebase Cloud Messaging)
```

### 3.2 백엔드
```yaml
Framework: Spring Boot 3.1.4
Language: Java 21
ORM: Spring Data JPA (Hibernate)
Security: Spring Security + JWT
WebSocket: Spring WebSocket (STOMP)
Validation: Jakarta Validation
Migration: Flyway
API Docs: SpringDoc OpenAPI
```

### 3.3 인프라
```yaml
Database:
  - MySQL 8.0 (PlanetScale 권장)
  - Redis (캐싱/세션)
Storage: Cloudflare R2 (S3 호환)
Container:
  - Docker
  - AWS ECS + Fargate (채팅 서버)
CI/CD: GitHub Actions
Monitoring:
  - Spring Boot Actuator
  - CloudWatch (AWS)
```

## 4. 모듈 구조

### 4.1 Spring Boot 모듈
```
backend/
├── src/main/java/com/minibuskingbig/
│   ├── common/              # 공통 모듈
│   │   ├── config/         # 설정
│   │   ├── exception/      # 예외 처리
│   │   ├── util/          # 유틸리티
│   │   └── dto/           # 공통 DTO
│   │
│   ├── auth/               # 인증/인가
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   └── entity/
│   │
│   ├── user/               # 사용자 관리
│   ├── singer/             # 가수 프로필
│   ├── team/               # 팀 관리
│   ├── event/              # 공연 관리
│   ├── song/               # 곡 관리
│   ├── chat/               # 채팅 (컨테이너 관리)
│   ├── venue/              # 장소 관리
│   ├── ad/                 # 광고 관리
│   ├── subscription/       # 구독 관리
│   ├── payment/            # 결제 관리
│   └── admin/              # 관리자
│
└── resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    └── db/migration/       # Flyway 마이그레이션
```

### 4.2 Next.js 구조
```
frontend-web/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── (auth)/            # 인증 그룹
│   │   │   ├── login/
│   │   │   └── signup/
│   │   ├── (main)/            # 메인 그룹
│   │   │   ├── events/        # 공연 목록
│   │   │   ├── singers/       # 가수 목록
│   │   │   └── venues/        # 장소 목록
│   │   ├── (singer)/          # 가수 그룹
│   │   │   ├── dashboard/     # 공연 대시보드
│   │   │   ├── events/        # 공연 관리
│   │   │   └── songs/         # 곡 관리
│   │   ├── (audience)/        # 관객 그룹
│   │   │   ├── favorites/
│   │   │   └── subscriptions/
│   │   ├── (venue)/           # 장소 제공자
│   │   ├── (advertiser)/      # 광고주
│   │   └── (admin)/           # 관리자
│   │
│   ├── components/            # 공통 컴포넌트
│   │   ├── ui/               # UI 컴포넌트
│   │   ├── layout/           # 레이아웃
│   │   ├── event/            # 공연 관련
│   │   ├── chat/             # 채팅
│   │   └── map/              # 지도
│   │
│   ├── features/             # 기능별 컴포넌트
│   │   ├── auth/
│   │   ├── event/
│   │   ├── chat/
│   │   └── payment/
│   │
│   ├── hooks/                # 커스텀 훅
│   ├── lib/                  # 라이브러리
│   ├── store/                # Zustand 스토어
│   └── types/                # TypeScript 타입
│
└── public/
    ├── avatars/              # 아바타 에셋
    └── icons/
```

## 5. API 설계

### 5.1 인증 API
```
POST   /api/auth/social/login      # 소셜 로그인
POST   /api/auth/logout             # 로그아웃
POST   /api/auth/refresh            # 토큰 갱신
GET    /api/auth/me                 # 현재 사용자 정보
POST   /api/auth/verify-email       # 이메일 인증
```

### 5.2 공연 API
```
GET    /api/events                  # 공연 목록
GET    /api/events/{id}             # 공연 상세
POST   /api/events                  # 공연 등록
PUT    /api/events/{id}             # 공연 수정
DELETE /api/events/{id}             # 공연 삭제
GET    /api/events/map              # 지도용 공연 목록
```

### 5.3 곡 API
```
GET    /api/events/{eventId}/songs          # 곡 목록
POST   /api/events/{eventId}/songs          # 곡 등록
PUT    /api/events/{eventId}/songs/{id}     # 곡 수정
DELETE /api/events/{eventId}/songs/{id}     # 곡 삭제
PATCH  /api/events/{eventId}/songs/reorder  # 순서 변경
```

### 5.4 채팅 API
```
POST   /api/chat/rooms                      # 채팅방 생성 (컨테이너 기동)
GET    /api/chat/rooms/{id}                 # 채팅방 정보
DELETE /api/chat/rooms/{id}                 # 채팅방 종료
POST   /api/chat/rooms/{id}/join            # 채팅 참여
POST   /api/chat/rooms/{id}/leave           # 채팅 나가기
POST   /api/chat/rooms/{id}/kick            # 강제퇴장
GET    /api/chat/rooms/{id}/export          # 채팅 내역 다운로드
```

### 5.5 결제 API
```
POST   /api/payments/chat                   # 채팅 결제
POST   /api/payments/subscription           # 구독 결제
POST   /api/payments/ad-free                # 광고 제거 구매
POST   /api/payments/verify                 # 결제 검증
```

## 6. 채팅 컨테이너 아키텍처

### 6.1 컨테이너 생명주기
```
1. 공연 등록 시 채팅 옵션 선택
2. 결제 완료 후 chatRoom 레코드 생성
3. 공연 시작 10분 전: Fargate 태스크 실행
   - Docker 이미지 pull
   - WebSocket 서버 시작
   - chatRoom.websocket_url 업데이트
4. 공연 진행 중: WebSocket 연결 유지
5. 공연 종료 후:
   - 채팅 로그 저장
   - 컨테이너 종료
   - Fargate 태스크 삭제
```

### 6.2 컨테이너 구조
```dockerfile
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .

EXPOSE 8081

CMD ["node", "server.js"]
```

### 6.3 WebSocket 서버 (Node.js)
```javascript
// chat-server/server.js
const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

const MAX_PARTICIPANTS = process.env.MAX_PARTICIPANTS || 50;
let currentParticipants = 0;

io.on('connection', (socket) => {
  if (currentParticipants >= MAX_PARTICIPANTS) {
    socket.emit('error', { message: '채팅 인원 초과' });
    socket.disconnect();
    return;
  }

  currentParticipants++;

  socket.on('message', (data) => {
    io.emit('message', {
      userId: data.userId,
      nickname: data.nickname,
      avatar: data.avatar,
      message: data.message,
      senderType: data.senderType,
      timestamp: Date.now()
    });
  });

  socket.on('disconnect', () => {
    currentParticipants--;
  });
});

server.listen(8081);
```

## 7. 결제 플로우

### 7.1 채팅 결제
```
1. 가수가 공연 등록 시 채팅 옵션 선택
2. 예상 금액 계산 (시간당 1,000원)
3. 결제 요청:
   - 사전 등록된 카드
   - KakaoPay/NaverPay/ApplePay
4. PG사 결제 승인
5. Payment 레코드 생성
6. Event.chat_payment_status = 'PAID'
7. 공연 10분 전 컨테이너 자동 기동
```

### 7.2 VIP 구독 결제
```
1. 관객이 특정 가수에게 등급 상승 요청
2. 가수 승인
3. 월 구독료 결제 (자동 갱신)
4. Subscription 레코드 생성
5. User.audience_tier = 'VIP'
6. 수익 분배: 플랫폼 50% / 가수 50%
```

## 8. 알림 시스템

### 8.1 WebPush (웹)
```javascript
// Frontend
if ('serviceWorker' in navigator && 'PushManager' in window) {
  const registration = await navigator.serviceWorker.register('/sw.js');
  const subscription = await registration.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: VAPID_PUBLIC_KEY
  });

  // 서버에 subscription 전송
  await fetch('/api/push/subscribe', {
    method: 'POST',
    body: JSON.stringify(subscription)
  });
}
```

### 8.2 알림 트리거
```java
// Backend
@Scheduled(cron = "0 * * * * *") // 매분 실행
public void checkUpcomingEvents() {
    List<Event> events = eventRepository.findUpcomingIn30Minutes();

    for (Event event : events) {
        pushService.sendNotification(
            event.getSinger().getUser(),
            "30분 후 공연 시작",
            event.getTitle()
        );
    }
}
```

## 9. 보안

### 9.1 인증/인가
- **JWT**: Access Token (1시간) + Refresh Token (7일)
- **소셜 로그인**: OAuth 2.0
- **권한 체계**: ROLE_AUDIENCE, ROLE_SINGER, ROLE_BUSINESS, ROLE_ADMIN

### 9.2 API 보안
- **CORS**: 허용된 도메인만 접근
- **Rate Limiting**: Redis 기반 요청 제한
- **Input Validation**: Jakarta Validation
- **SQL Injection 방지**: Prepared Statement (JPA)

### 9.3 데이터 보안
- **비밀번호**: 없음 (소셜 로그인만)
- **개인정보**: 이메일, 연락처 암호화 저장
- **결제정보**: PG사에 위임 (카드번호 미저장)

## 10. 성능 최적화

### 10.1 데이터베이스
- **인덱스**: 자주 조회되는 컬럼에 인덱스
- **연관 관계**: Lazy Loading 기본, 필요시 Fetch Join
- **페이징**: Offset 대신 Cursor 기반 페이징

### 10.2 캐싱
```java
@Cacheable(value = "events", key = "#eventId")
public Event getEvent(Long eventId) {
    return eventRepository.findById(eventId);
}

@CacheEvict(value = "events", key = "#event.id")
public Event updateEvent(Event event) {
    return eventRepository.save(event);
}
```

### 10.3 Next.js 최적화
```typescript
// 공연 목록: Static Generation
export const generateStaticParams = async () => {
  const events = await fetch('/api/events').then(r => r.json());
  return events.map(e => ({ id: e.id.toString() }));
};

// 공연 상세: ISR (1분마다 재생성)
export const revalidate = 60;
```

## 11. 모니터링

### 11.1 메트릭
- **서버**: CPU, 메모리, 요청 수, 응답 시간
- **채팅 컨테이너**: 동시 접속자, 메시지 처리량
- **결제**: 성공률, 실패 원인
- **사용자**: DAU, MAU, 체류 시간

### 11.2 로깅
```yaml
Logging:
  - Application: Logback (JSON 포맷)
  - Access: Nginx Access Log
  - Error: Sentry
  - Audit: 주요 이벤트 DB 저장
```

## 12. 배포 전략

### 12.1 환경 분리
- **dev**: 개발 환경 (로컬)
- **staging**: 스테이징 (실 서버와 동일 구성)
- **prod**: 프로덕션

### 12.2 CI/CD
```yaml
GitHub Actions:
  - PR 생성: 테스트 실행
  - main 병합:
    - Docker 이미지 빌드
    - ECR 푸시
    - ECS 배포 (롤링 업데이트)
  - 태그 생성: 프로덕션 배포
```

## 다음 단계

1. ✅ 데이터베이스 스키마 설계 완료
2. ✅ 아키텍처 설계 완료
3. 🔄 Spring Boot Entity 작성
4. 🔄 Repository & Service 구현
5. 🔄 REST API 구현
6. 🔄 Next.js 프론트엔드 구현
