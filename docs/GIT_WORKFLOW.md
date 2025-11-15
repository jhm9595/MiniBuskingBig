# Git 브랜치 전략 및 워크플로우

## 📋 브랜치 구조

```
main (프로덕션)
  ↑
develop (개발 통합)
  ↑
feature/* (기능 개발)
```

## 🌿 브랜치 종류

### 1. `main` 브랜치
- **용도**: 프로덕션 환경에 배포 가능한 안정적인 코드
- **규칙**:
  - 직접 커밋 금지
  - `develop` 브랜치에서만 병합
  - 병합 시 반드시 태그 생성 (버전 관리)
- **보호 규칙**: PR 리뷰 필수

### 2. `develop` 브랜치
- **용도**: 다음 릴리스를 위한 개발 통합 브랜치
- **규칙**:
  - 기능 개발 완료 후 `feature/*` 브랜치에서 병합
  - 항상 빌드 가능한 상태 유지
  - CI/CD 자동 테스트 통과 필수

### 3. `feature/*` 브랜치
- **용도**: 새로운 기능 개발
- **네이밍**: `feature/기능명` (예: `feature/user-auth`, `feature/payment`)
- **규칙**:
  - `develop` 브랜치에서 분기
  - 작업 완료 후 `develop`으로 PR
  - 병합 후 브랜치 삭제

## 🚀 작업 시작하기

### 1. 환경 세팅 브랜치 생성 (최초 1회)

```bash
# 현재 main 브랜치 상태를 develop으로 복사
git checkout -b develop
git push -u origin develop
```

### 2. 새로운 기능 개발 시작

```bash
# develop 브랜치에서 최신 코드 가져오기
git checkout develop
git pull origin develop

# 새로운 feature 브랜치 생성
git checkout -b feature/기능명

# 예시
git checkout -b feature/user-login
```

### 3. 작업 중 커밋

```bash
# 변경사항 확인
git status

# 파일 스테이징
git add .

# 커밋 (한국어 메시지)
git commit -m "feat(사용자): 로그인 기능 구현

- JWT 토큰 기반 인증 추가
- 로그인 API 엔드포인트 생성
- 프론트엔드 로그인 폼 컴포넌트 추가

🤖 Claude Code로 생성됨
Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 4. 원격 저장소에 푸시

```bash
# 처음 푸시할 때
git push -u origin feature/기능명

# 이후 푸시
git push
```

### 5. Pull Request (PR) 생성

```bash
# GitHub CLI 사용
gh pr create --base develop --head feature/기능명 --title "feat(사용자): 로그인 기능 구현" --body "
## 변경 사항
- JWT 토큰 기반 인증 시스템
- 로그인 API 엔드포인트
- 프론트엔드 UI 컴포넌트

## 테스트 완료 여부
- [x] 단위 테스트
- [x] 통합 테스트
- [x] 로컬 환경 테스트

## 스크린샷
(스크린샷 첨부)
"
```

### 6. 코드 리뷰 및 병합

```bash
# PR이 승인되면 develop 브랜치로 병합 (GitHub에서 수행)
# 병합 후 로컬 브랜치 정리

git checkout develop
git pull origin develop
git branch -d feature/기능명  # 로컬 브랜치 삭제
```

## 📝 커밋 메시지 컨벤션

### 형식
```
type(scope): 제목 (한국어)

- 상세 설명 1
- 상세 설명 2

🤖 Claude Code로 생성됨
Co-Authored-By: Claude <noreply@anthropic.com>
```

### Type 종류
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `refactor`: 코드 리팩토링
- `docs`: 문서 수정
- `style`: 코드 포맷팅 (기능 변경 없음)
- `test`: 테스트 코드 추가
- `chore`: 빌드 설정, 패키지 업데이트 등

### Scope 예시
- `백엔드`, `프론트엔드`, `공유`, `설정`, `배포` 등

### 예시
```bash
git commit -m "feat(백엔드): 사용자 회원가입 API 추가

- POST /api/users/register 엔드포인트 구현
- 이메일 중복 검증 로직 추가
- 비밀번호 암호화 처리 (BCrypt)

🤖 Claude Code로 생성됨
Co-Authored-By: Claude <noreply@anthropic.com>"
```

## 🔄 주요 시나리오

### 시나리오 1: 새로운 요구사항 개발

```bash
# 1. develop 브랜치 최신화
git checkout develop
git pull origin develop

# 2. feature 브랜치 생성
git checkout -b feature/결제-시스템

# 3. 작업 및 커밋
# ... 코드 작성 ...
git add .
git commit -m "feat(백엔드): 결제 시스템 구현"

# 4. 푸시 및 PR
git push -u origin feature/결제-시스템
gh pr create --base develop
```

### 시나리오 2: 긴급 버그 수정

```bash
# 1. main에서 hotfix 브랜치 생성
git checkout main
git pull origin main
git checkout -b hotfix/로그인-버그

# 2. 버그 수정 및 커밋
git add .
git commit -m "fix(프론트엔드): 로그인 실패 시 에러 처리 개선"

# 3. main과 develop 모두에 병합
git checkout main
git merge hotfix/로그인-버그
git push origin main

git checkout develop
git merge hotfix/로그인-버그
git push origin develop

git branch -d hotfix/로그인-버그
```

### 시나리오 3: develop → main 릴리스

```bash
# 1. develop 브랜치 최종 확인
git checkout develop
git pull origin develop

# 2. GitHub에서 develop → main PR 생성
gh pr create --base main --head develop --title "release: v1.0.0"

# 3. PR 병합 후 태그 생성
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## 🛡️ 보호 규칙

### main 브랜치
- 직접 푸시 금지
- PR 리뷰 1명 이상 필수
- CI/CD 테스트 통과 필수
- 관리자만 병합 가능

### develop 브랜치
- 직접 푸시 가능 (긴급 상황)
- PR 권장
- CI/CD 테스트 통과 필수

## 📌 주의사항

1. **절대 main에 직접 커밋하지 않기**
2. **feature 브랜치는 작업 완료 후 반드시 삭제**
3. **커밋 메시지는 명확하고 구체적으로**
4. **대규모 변경은 여러 개의 작은 PR로 분할**
5. **develop 브랜치는 항상 빌드 가능한 상태 유지**

## 🔧 환경 세팅 완료 후 Git 상태

현재 프로젝트는 **환경 세팅 완료** 상태입니다.

- `main` 브랜치: 환경 세팅 완료된 깨끗한 상태
- 다음 작업: `develop` 브랜치 생성 및 새로운 기능 개발 시작

```bash
# develop 브랜치 생성 (최초 1회)
git checkout -b develop
git push -u origin develop

# 이후 모든 기능은 feature 브랜치에서 개발
git checkout -b feature/첫번째-기능
```
