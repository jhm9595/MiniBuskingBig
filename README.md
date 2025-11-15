# MiniBuskingBig

이 저장소는 React(프론트엔드)와 Spring Boot(백엔드)를 함께 사용하는 기본 템플릿입니다.

목표

- 프로젝트 구조와 빌드/테스트 규칙을 제공하여 모든 개발자가 동일한 규칙을 따르도록 돕습니다.

디렉터리 구조

- `backend/` : Java Spring Boot 애플리케이션 (Maven)
- `frontend/` : React 애플리케이션 (Vite)

빠른 시작 (PowerShell)

- 백엔드 실행:

```powershell
cd C:\secjob\MiniBuskingBig\backend
mvn spring-boot:run
```

- 프론트엔드 개발 서버 실행:

```powershell
cd C:\secjob\MiniBuskingBig\frontend
npm install
npm run dev
```

빌드

- 백엔드: `mvn -DskipTests package`
- 프론트엔드: `npm run build` (프론트 폴더에서 실행)

테스트

- 백엔드: `mvn test`
- 프론트엔드: `npm test` (설정된 테스트가 있을 경우)

다음 단계

- 원하시면 이 로컬 변경을 원격 `origin`에 푸시하도록 도와드릴게요.
