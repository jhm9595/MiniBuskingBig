# 💻 개발 환경 설정 가이드

## 📥 프로젝트 클론 후 초기 설정

### 1️⃣ 저장소 클론

```bash
git clone https://github.com/jhm9595/MiniBuskingBig.git
cd MiniBuskingBig
```

### 2️⃣ 의존성 설치

```bash
# Backend (Maven)
cd backend
mvn clean install

# Frontend Web
cd ../frontend-web
npm install

# Frontend Mobile
cd ../frontend-mobile
npm install
```

### 3️⃣ 프로젝트 실행

#### Backend

```bash
cd backend
mvn spring-boot:run
# 또는
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

#### Frontend Web

```bash
cd frontend-web
npm run dev
# http://localhost:5173 접속
```

#### Frontend Mobile

```bash
cd frontend-mobile
npm start
# i = iOS, a = Android, w = Web
```

## 📦 왜 node_modules를 올리지 않나?

### ✅ 올바른 방식 (현재 프로젝트)

```
repository/ (GitHub)
├── backend/
├── frontend-web/
│   ├── src/
│   ├── package.json      ✓ 올라감
│   └── node_modules/     ✗ .gitignore로 제외
├── frontend-mobile/
└── .gitignore           (node_modules 제외 설정)

개발자 로컬
└── npm install 명령어로 자동 생성
```

### ❌ 잘못된 방식 (피할 것)

```
repository/ (GitHub)
├── ...
└── frontend-web/
    ├── node_modules/    ✗ 325MB+ 낭비!

문제점:
- 저장소 크기 폭증 → 느린 속도
- 플랫폼 호환성 문제 (Windows/Mac/Linux)
- CI/CD 빌드 시간 증가
- 불필요한 용량 낭비
```

## 🔍 .gitignore 설정 확인

```bash
# node_modules이 제외되는지 확인
git check-ignore frontend-web/node_modules
# 결과: frontend-web/node_modules

# 추적 중인 파일 확인
git ls-files | grep node_modules
# 결과: (없음 = 올바른 설정)
```

## 💾 용량 비교

| 상황                         | 저장소 크기   |
| ---------------------------- | ------------- |
| **node_modules 제외** (현재) | ~150 MB       |
| **node_modules 포함**        | ~475 MB+      |
| **저장소 용량 절감**         | **70% 감소!** |

## 🚀 새로운 팀원 온보딩 시간

### ✅ 올바른 방식 (현재)

```
git clone → npm install → 완료 (1~2분)
클론 시간: ~30초, 의존성 설치: ~1분
```

### ❌ node_modules 포함 시

```
git clone → 완료 (5~10분)
클론 시간: ~5분 이상 (네트워크에 따라 10분 이상)
```

## 📌 핵심 포인트

- **package.json**: 필요한 패키지 명시 ✓
- **package-lock.json**: 정확한 버전 지정 ✓
- **node_modules**: 로컬에서만 생성 ✓

이렇게 하면 모든 개발자가 동일한 환경에서 작업할 수 있습니다! 🎯
