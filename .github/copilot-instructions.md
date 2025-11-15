<!--
이 파일은 MiniBuskingBig 저장소 전용 Copilot/AI 에이전트 지침입니다.
로컬에 코드가 추가되면 이 파일을 우선적으로 참고하세요.
-->
# MiniBuskingBig AI 에이전트 안내 (간결)

개요
- 이 저장소는 `frontend/`(React + Vite)와 `backend/`(Spring Boot, Maven)로 구성된 모노리포입니다.

핵심 발견 작업(처음 접근 시)
- 루트에서 다음 폴더를 먼저 확인: `frontend/`, `backend/`.
- `backend/pom.xml`로 Java 설정(버전, 의존성) 확인.
- `frontend/package.json`로 Node 스크립트와 dev 도구 확인.

빌드/실행/테스트(권장 명령, PowerShell)
- 백엔드(루트 `backend/`):
  - 개발 실행: `mvn spring-boot:run`
  - 빌드: `mvn -DskipTests package`
  - 테스트: `mvn test`
- 프론트엔드(루트 `frontend/`):
  - 의존성 설치: `npm install`
  - 개발 서버: `npm run dev`
  - 빌드: `npm run build`
  - 테스트: `npm test` (설정된 경우)

프로젝트 규약(이 저장소 전용)
- Java:
  - Maven 표준 디렉터리 구조 사용(`src/main/java`, `src/test/java`).
  - 패키지 네임스페이스는 `com.minibuskingbig`를 기본으로 사용.
  - Controller, Service, Repository 계층을 명확히 분리.
  - 코드 스타일은 Google Java Style 또는 회사 규약을 따르되, 포맷터 설정이 없으면 `mvn -DskipTests package`로 빌드 확인.
- React:
  - Vite + React(ESModules) 사용.
  - 컴포넌트는 `src/` 내에 기능별 폴더로 구성(예: `src/components`, `src/pages`).
  - 상태 관리는 초기에는 React 훅(`useState`, `useEffect`) 사용; 필요 시 Redux/RTK 도입 권장.
  - CSS는 모듈 또는 Tailwind 같은 유틸리티 프레임워크를 선택해서 일관되게 사용.

커밋 / PR 규칙
- 커밋 메시지: `type(scope): 메시지` 형식 권장(e.g. `feat(auth): add login endpoint`).
- PR에는 변경 요약과 테스트/로컬 실행 방법을 포함.

통합 포인트 및 배포 힌트
- 백엔드는 일반적으로 REST API를 제공하고, 프론트엔드는 이 API를 호출합니다. 기본 CORS는 개발 편의를 위해 백엔드에서 허용해 놓았습니다(예시 소스 참고).
- 배포 시에는 백엔드와 프론트엔드를 별도 컨테이너로 빌드하거나, 프론트엔드 정적 빌드를 백엔드의 정적 리소스로 포함할 수 있습니다.

작업이 막히면 물어볼 내용(예시)
- 어떤 Java 또는 Node 버전을 선호하시나요?
- CI/CD(예: GitHub Actions) 설정을 추가할까요?
- 코드 스타일 포맷터(예: Spotless, Prettier) 설정을 추가할까요?
