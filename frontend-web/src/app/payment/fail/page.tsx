'use client';

import { useSearchParams, useRouter } from 'next/navigation';

export default function PaymentFailPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const errorMessage = searchParams.get('message') || '결제에 실패했습니다.';

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="max-w-md w-full bg-white rounded-lg shadow-sm p-8 text-center">
        <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg className="w-8 h-8 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">결제 실패</h2>
        <p className="text-gray-600 mb-6">{errorMessage}</p>
        <div className="flex gap-3">
          <button
            onClick={() => router.back()}
            className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50"
          >
            다시 시도
          </button>
          <button
            onClick={() => router.push('/')}
            className="flex-1 px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700"
          >
            홈으로
          </button>
        </div>
      </div>
    </div>
  );
}
