# GitHub 템플릿 리포지토리 설정 가이드

이 문서는 MiniBuskingBig 리포지토리를 **GitHub 템플릿 리포지토리**로 설정하는 방법을 안내합니다.

## 📌 템플릿 리포지토리란?

GitHub 템플릿 리포지토리는 다른 사용자가 쉽게 복사하여 새 프로젝트를 시작할 수 있는 보일러플레이트 역할을 합니다.

### 일반 Fork vs 템플릿 사용

| 구분 | Fork | Template |
|------|------|----------|
| Git 히스토리 | 복사됨 | 복사 안됨 (깨끗한 상태) |
| 용도 | 기여를 위한 복사 | 새 프로젝트 시작 |
| 원본과의 관계 | 유지됨 | 독립적 |

## 🚀 GitHub에서 템플릿 리포지토리 설정하기

### 방법 1: GitHub 웹 UI 사용 (권장)

1. **GitHub 리포지토리 페이지로 이동**
   ```
   https://github.com/jhm9595/MiniBuskingBig
   ```

2. **Settings 탭 클릭**
   - 리포지토리 상단 메뉴에서 **Settings** 클릭

3. **Template repository 활성화**
   - "General" 섹션에서 스크롤 다운
   - **"Template repository"** 체크박스 찾기
   - ✅ **"Template repository"** 체크

4. **저장 완료**
   - 자동으로 저장됩니다
   - 이제 리포지토리 메인 페이지에 **"Use this template"** 버튼이 나타납니다

### 방법 2: GitHub CLI 사용

GitHub CLI가 설치되어 있다면:

```bash
# GitHub CLI 설치 확인
gh --version

# 템플릿 리포지토리로 설정
cd /c/secjob/MiniBuskingBig
gh repo edit --template

# 확인
gh repo view --json isTemplate
```

GitHub CLI 설치:
- Windows: `winget install GitHub.cli`
- Mac: `brew install gh`
- Linux: https://github.com/cli/cli#installation

## 📋 템플릿으로부터 새 프로젝트 만들기

템플릿 리포지토리 설정 후, 다음과 같이 사용할 수 있습니다:

### GitHub UI 사용

1. **MiniBuskingBig 리포지토리로 이동**
   ```
   https://github.com/jhm9595/MiniBuskingBig
   ```

2. **"Use this template" 버튼 클릭**
   - 우측 상단의 초록색 버튼

3. **새 리포지토리 정보 입력**
   - Repository name: `MyNewProject`
   - Description: 프로젝트 설명
   - Public/Private 선택
   - ✅ "Include all branches" (선택사항)

4. **"Create repository from template" 클릭**

### GitHub CLI 사용

```bash
# 템플릿으로부터 새 리포지토리 생성
gh repo create MyNewProject \
  --template jhm9595/MiniBuskingBig \
  --public \
  --clone

# 또는 private로 생성
gh repo create MyNewProject \
  --template jhm9595/MiniBuskingBig \
  --private \
  --clone
```

### Git 명령어로 수동 복사

```bash
# 1. 템플릿 클론
git clone https://github.com/jhm9595/MiniBuskingBig.git MyNewProject
cd MyNewProject

# 2. Git 히스토리 제거 (선택사항)
rm -rf .git
git init

# 3. 초기 커밋
git add .
git commit -m "Initial commit from MiniBuskingBig template"

# 4. 새 리모트 연결
git remote add origin https://github.com/YOUR_USERNAME/MyNewProject.git
git branch -M main
git push -u origin main
```

## 🔧 템플릿 사용 후 커스터마이징

새 프로젝트를 생성한 후:

1. **[TEMPLATE_GUIDE.md](../TEMPLATE_GUIDE.md) 참조**
   - 프로젝트 이름 변경
   - 패키지 구조 수정
   - 데이터베이스 설정 등

2. **환경 세팅 스크립트 실행**
   ```bash
   # Linux/Mac
   ./scripts/setup-env.sh

   # Windows
   .\scripts\setup-env.ps1
   ```

3. **개발 시작**
   ```bash
   # Docker로 실행
   docker-compose up

   # 또는 로컬 개발 환경
   # Backend
   cd backend && mvn spring-boot:run

   # Frontend
   cd frontend-web && npm install && npm run dev
   ```

## 📚 추가 리소스

- [GitHub 템플릿 리포지토리 공식 문서](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-template-repository)
- [프로젝트 템플릿 사용 가이드](../TEMPLATE_GUIDE.md)
- [Git 워크플로우](./GIT_WORKFLOW.md)

## ⚠️ 주의사항

### 템플릿 리포지토리 설정 전 확인사항

- ✅ 민감한 정보 제거 (API 키, 비밀번호 등)
- ✅ `.gitignore` 설정 확인
- ✅ 문서 업데이트 (README, TEMPLATE_GUIDE 등)
- ✅ 예제 코드 정리
- ✅ 불필요한 파일 제거

### 템플릿 사용 시 주의사항

1. **프로젝트 이름 변경 필수**
   - 패키지명, 컨테이너명 등 모두 변경

2. **환경 변수 재설정**
   - `.env` 파일 새로 생성
   - 데이터베이스 설정 수정

3. **Git 히스토리 관리**
   - 템플릿 히스토리가 필요 없으면 제거
   - 새로운 초기 커밋으로 시작

## 🎯 다음 단계

1. ✅ GitHub에서 템플릿 리포지토리 설정
2. ✅ 템플릿으로부터 새 프로젝트 생성
3. ✅ 프로젝트 커스터마이징
4. ✅ 개발 시작!

Happy Coding! 🚀
