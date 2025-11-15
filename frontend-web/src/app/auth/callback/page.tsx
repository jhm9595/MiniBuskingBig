'use client';

import { useEffect } from 'react';
import { useSearchParams } from 'next/navigation';
import { authApi } from '@/lib/api/auth';

export default function AuthCallbackPage() {
  const searchParams = useSearchParams();

  useEffect(() => {
    const accessToken = searchParams.get('accessToken');
    const refreshToken = searchParams.get('refreshToken');

    if (accessToken && refreshToken) {
      authApi.setTokens(accessToken, refreshToken);
      window.location.href = '/';
    } else {
      window.location.href = '/login';
    }
  }, [searchParams]);

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="text-center">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">로그인 처리 중...</h2>
        <p className="text-gray-600">잠시만 기다려주세요.</p>
      </div>
    </div>
  );
}
