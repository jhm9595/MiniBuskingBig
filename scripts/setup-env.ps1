# MiniBuskingBig 환경 세팅 스크립트 (PowerShell)
# 이 스크립트는 프로젝트 초기 환경을 설정합니다.

$ErrorActionPreference = "Stop"

Write-Host "🚀 MiniBuskingBig 환경 세팅 시작..." -ForegroundColor Cyan

# 1. Git 브랜치 설정
Write-Host "`n📌 Step 1: Git 브랜치 설정" -ForegroundColor Blue

# develop 브랜치 생성 (존재하지 않는 경우)
$developExists = git show-ref --verify --quiet refs/heads/develop 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "develop 브랜치가 이미 존재합니다." -ForegroundColor Yellow
} else {
    Write-Host "develop 브랜치 생성 중..."
    git checkout -b develop
    git push -u origin develop
    Write-Host "✓ develop 브랜치 생성 완료" -ForegroundColor Green
}

# main 브랜치로 돌아가기
git checkout main

# 2. Docker 환경 확인
Write-Host "`n📌 Step 2: Docker 환경 확인" -ForegroundColor Blue

try {
    docker --version | Out-Null
    Write-Host "✓ Docker 설치 확인됨" -ForegroundColor Green
    docker --version
} catch {
    Write-Host "⚠ Docker가 설치되어 있지 않습니다." -ForegroundColor Yellow
    Write-Host "Docker 설치: https://docs.docker.com/get-docker/"
    exit 1
}

try {
    docker-compose --version | Out-Null
    Write-Host "✓ Docker Compose 설치 확인됨" -ForegroundColor Green
    docker-compose --version
} catch {
    Write-Host "⚠ Docker Compose가 설치되어 있지 않습니다." -ForegroundColor Yellow
    Write-Host "Docker Compose 설치: https://docs.docker.com/compose/install/"
    exit 1
}

# 3. 환경 변수 파일 생성
Write-Host "`n📌 Step 3: 환경 변수 파일 생성" -ForegroundColor Blue

# Backend .env 예시 생성
if (-not (Test-Path "backend\.env")) {
    @"
# Spring Boot 환경 변수
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# 데이터베이스 설정 (필요 시 추가)
# DB_HOST=localhost
# DB_PORT=5432
# DB_NAME=minibuskingbig
# DB_USERNAME=admin
# DB_PASSWORD=secret
"@ | Out-File -FilePath "backend\.env" -Encoding UTF8
    Write-Host "✓ backend\.env 파일 생성 완료" -ForegroundColor Green
} else {
    Write-Host "backend\.env 파일이 이미 존재합니다." -ForegroundColor Yellow
}

# Frontend .env 예시 생성
if (-not (Test-Path "frontend-web\.env")) {
    @"
# Vite 환경 변수
VITE_API_URL=
NODE_ENV=development
"@ | Out-File -FilePath "frontend-web\.env" -Encoding UTF8
    Write-Host "✓ frontend-web\.env 파일 생성 완료" -ForegroundColor Green
} else {
    Write-Host "frontend-web\.env 파일이 이미 존재합니다." -ForegroundColor Yellow
}

# 4. .gitignore 업데이트
Write-Host "`n📌 Step 4: .gitignore 확인" -ForegroundColor Blue

$gitignoreContent = Get-Content ".gitignore" -Raw
if ($gitignoreContent -notmatch "\.env") {
    Add-Content -Path ".gitignore" -Value ".env"
    Write-Host "✓ .gitignore에 .env 추가" -ForegroundColor Green
} else {
    Write-Host "✓ .gitignore 설정 확인됨" -ForegroundColor Green
}

# 5. Docker 빌드 테스트
Write-Host "`n📌 Step 5: Docker 빌드 테스트 (선택사항)" -ForegroundColor Blue
$response = Read-Host "Docker 이미지를 빌드하시겠습니까? (y/N)"
if ($response -match "^[Yy]$") {
    Write-Host "Docker 이미지 빌드 중..."
    docker-compose build
    Write-Host "✓ Docker 이미지 빌드 완료" -ForegroundColor Green
}

# 6. 완료 메시지
Write-Host "`n════════════════════════════════════════" -ForegroundColor Green
Write-Host "🎉 환경 세팅이 완료되었습니다!" -ForegroundColor Green
Write-Host "════════════════════════════════════════" -ForegroundColor Green

Write-Host "`n다음 단계:" -ForegroundColor Blue
Write-Host "1. 새로운 기능 개발 시작:"
Write-Host "   git checkout develop" -ForegroundColor Yellow
Write-Host "   git checkout -b feature/기능명" -ForegroundColor Yellow
Write-Host ""
Write-Host "2. Docker로 실행:"
Write-Host "   docker-compose up" -ForegroundColor Yellow
Write-Host ""
Write-Host "3. 로컬에서 실행:"
Write-Host "   Backend:  cd backend && mvn spring-boot:run" -ForegroundColor Yellow
Write-Host "   Frontend: cd frontend-web && npm run dev" -ForegroundColor Yellow
Write-Host ""
Write-Host "자세한 내용은 docs\GIT_WORKFLOW.md를 참조하세요."
