'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { paymentApi } from '@/lib/api/payment';
import { useTossPayments } from '@/lib/hooks/useTossPayments';

export default function AdFreePage() {
  const router = useRouter();
  const { loading, requestPayment } = useTossPayments();
  const [processing, setProcessing] = useState(false);

  const handlePurchase = async () => {
    setProcessing(true);

    try {
      // 1. 결제 생성
      const payment = await paymentApi.createAdFreePayment();

      // 2. 토스페이먼츠 결제 요청
      await requestPayment({
        amount: payment.data.amount,
        orderId: payment.data.orderId,
        orderName: payment.data.itemName,
        successUrl: `${window.location.origin}/payment/success`,
        failUrl: `${window.location.origin}/payment/fail`,
      });
    } catch (error) {
      console.error('Ad-free purchase failed:', error);
      alert('광고 제거 구매에 실패했습니다.');
      setProcessing(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <a href="/" className="text-indigo-600 hover:text-indigo-800 text-sm font-medium">
            ← 홈으로
          </a>
        </div>
      </header>

      {/* Content */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">광고 제거</h1>
          <p className="text-lg text-gray-600">
            방해받지 않고 순수하게 공연을 즐기세요
          </p>
        </div>

        {/* Pricing Card */}
        <div className="max-w-md mx-auto">
          <div className="bg-white rounded-2xl shadow-xl overflow-hidden border-2 border-gray-200">
            <div className="p-8">
              <div className="text-center mb-6">
                <div className="inline-block px-4 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium mb-4">
                  일회성 결제
                </div>
                <h2 className="text-3xl font-bold text-gray-900 mb-2">광고 제거</h2>
                <div className="flex items-baseline justify-center gap-2">
                  <span className="text-5xl font-bold text-gray-900">4,900</span>
                  <span className="text-xl text-gray-600">원</span>
                </div>
                <p className="text-sm text-gray-500 mt-2">평생 사용</p>
              </div>

              {/* Features */}
              <ul className="space-y-4 mb-8">
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span className="text-gray-700">모든 광고 제거</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span className="text-gray-700">방해 없는 시청 경험</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span className="text-gray-700">빠른 페이지 로딩</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span className="text-gray-700">평생 사용 가능</span>
                </li>
              </ul>

              <button
                onClick={handlePurchase}
                disabled={loading || processing}
                className="w-full px-6 py-4 bg-green-600 text-white font-bold rounded-lg hover:bg-green-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                {processing ? '처리 중...' : '광고 제거 구매하기'}
              </button>
            </div>
          </div>

          {/* Additional Info */}
          <div className="mt-8 text-center text-sm text-gray-600">
            <p>일회성 결제로 평생 사용하실 수 있습니다.</p>
            <p className="mt-1">구매 후 즉시 적용됩니다.</p>
          </div>
        </div>
      </main>
    </div>
  );
}
