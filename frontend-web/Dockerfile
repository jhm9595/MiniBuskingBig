# 의존성 설치 스테이지
FROM node:20-alpine AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./
RUN npm ci

# 빌드 스테이지
FROM node:20-alpine AS builder
WORKDIR /app

# 의존성 복사
COPY --from=deps /app/node_modules ./node_modules

# shared 디렉토리 복사 (API 클라이언트)
COPY --from=deps /app/../shared /app/shared

# 소스 코드 복사
COPY . .

# Next.js 프로덕션 빌드
RUN npm run build

# 실행 스테이지
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV production

# 시스템 사용자 생성
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# public 폴더 복사
COPY --from=builder /app/public ./public

# .next 폴더 복사 (standalone 모드)
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

# 포트 노출
EXPOSE 5173

ENV PORT 5173
ENV HOSTNAME "0.0.0.0"

# Next.js 실행
CMD ["node", "server.js"]
