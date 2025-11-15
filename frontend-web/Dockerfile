# 빌드 스테이지
FROM node:20-alpine AS builder

WORKDIR /app

# package.json과 package-lock.json 복사 (의존성 캐싱)
COPY package*.json ./

# 의존성 설치
RUN npm ci

# shared 디렉토리 복사 (API 클라이언트)
COPY ../shared /app/shared

# 소스 코드 복사
COPY . .

# 프로덕션 빌드
RUN npm run build

# 실행 스테이지 (Nginx)
FROM nginx:alpine

# 빌드된 파일 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# Nginx 설정 복사 (API 프록시)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
