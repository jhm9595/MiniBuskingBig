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

## 다음 단계

- 상태 관리 (Redux, Zustand 등)
- 데이터베이스 연동
- 인증/인가 구현
