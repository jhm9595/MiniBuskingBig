#!/bin/bash

# MiniBuskingBig 환경 세팅 스크립트
# 이 스크립트는 프로젝트 초기 환경을 설정합니다.

set -e

echo "🚀 MiniBuskingBig 환경 세팅 시작..."

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Git 브랜치 설정
echo -e "${BLUE}📌 Step 1: Git 브랜치 설정${NC}"

# develop 브랜치 생성 (존재하지 않는 경우)
if git show-ref --verify --quiet refs/heads/develop; then
    echo -e "${YELLOW}develop 브랜치가 이미 존재합니다.${NC}"
else
    echo "develop 브랜치 생성 중..."
    git checkout -b develop
    git push -u origin develop
    echo -e "${GREEN}✓ develop 브랜치 생성 완료${NC}"
fi

# main 브랜치로 돌아가기
git checkout main

# 2. Docker 환경 확인
echo -e "\n${BLUE}📌 Step 2: Docker 환경 확인${NC}"

if command -v docker &> /dev/null; then
    echo -e "${GREEN}✓ Docker 설치 확인됨${NC}"
    docker --version
else
    echo -e "${YELLOW}⚠ Docker가 설치되어 있지 않습니다.${NC}"
    echo "Docker 설치: https://docs.docker.com/get-docker/"
    exit 1
fi

if command -v docker-compose &> /dev/null; then
    echo -e "${GREEN}✓ Docker Compose 설치 확인됨${NC}"
    docker-compose --version
else
    echo -e "${YELLOW}⚠ Docker Compose가 설치되어 있지 않습니다.${NC}"
    echo "Docker Compose 설치: https://docs.docker.com/compose/install/"
    exit 1
fi

# 3. 환경 변수 파일 생성
echo -e "\n${BLUE}📌 Step 3: 환경 변수 파일 생성${NC}"

# Backend .env 예시 생성
if [ ! -f backend/.env ]; then
    cat > backend/.env << EOF
# Spring Boot 환경 변수
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# 데이터베이스 설정 (필요 시 추가)
# DB_HOST=localhost
# DB_PORT=5432
# DB_NAME=minibuskingbig
# DB_USERNAME=admin
# DB_PASSWORD=secret
EOF
    echo -e "${GREEN}✓ backend/.env 파일 생성 완료${NC}"
else
    echo -e "${YELLOW}backend/.env 파일이 이미 존재합니다.${NC}"
fi

# Frontend .env 예시 생성
if [ ! -f frontend-web/.env ]; then
    cat > frontend-web/.env << EOF
# Vite 환경 변수
VITE_API_URL=
NODE_ENV=development
EOF
    echo -e "${GREEN}✓ frontend-web/.env 파일 생성 완료${NC}"
else
    echo -e "${YELLOW}frontend-web/.env 파일이 이미 존재합니다.${NC}"
fi

# 4. .gitignore 업데이트
echo -e "\n${BLUE}📌 Step 4: .gitignore 확인${NC}"

if ! grep -q "\.env" .gitignore; then
    echo ".env" >> .gitignore
    echo -e "${GREEN}✓ .gitignore에 .env 추가${NC}"
else
    echo -e "${GREEN}✓ .gitignore 설정 확인됨${NC}"
fi

# 5. Docker 빌드 테스트
echo -e "\n${BLUE}📌 Step 5: Docker 빌드 테스트 (선택사항)${NC}"
read -p "Docker 이미지를 빌드하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Docker 이미지 빌드 중..."
    docker-compose build
    echo -e "${GREEN}✓ Docker 이미지 빌드 완료${NC}"
fi

# 6. 완료 메시지
echo -e "\n${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}🎉 환경 세팅이 완료되었습니다!${NC}"
echo -e "${GREEN}════════════════════════════════════════${NC}"

echo -e "\n${BLUE}다음 단계:${NC}"
echo "1. 새로운 기능 개발 시작:"
echo "   ${YELLOW}git checkout develop${NC}"
echo "   ${YELLOW}git checkout -b feature/기능명${NC}"
echo ""
echo "2. Docker로 실행:"
echo "   ${YELLOW}docker-compose up${NC}"
echo ""
echo "3. 로컬에서 실행:"
echo "   Backend:  ${YELLOW}cd backend && mvn spring-boot:run${NC}"
echo "   Frontend: ${YELLOW}cd frontend-web && npm run dev${NC}"
echo ""
echo "자세한 내용은 docs/GIT_WORKFLOW.md를 참조하세요."
