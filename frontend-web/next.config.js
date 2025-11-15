/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,

  // Standalone 모드 (Docker 최적화)
  output: 'standalone',

  // API 라우트 재작성 (백엔드 프록시)
  async rewrites() {
    const apiUrl = process.env.API_URL || 'http://localhost:8080';
    return [
      {
        source: '/api/:path*',
        destination: `${apiUrl}/api/:path*`,
      },
    ];
  },

  // 프로덕션 빌드 최적화
  swcMinify: true,

  // 이미지 최적화
  images: {
    domains: [],
  },
};

module.exports = nextConfig;
