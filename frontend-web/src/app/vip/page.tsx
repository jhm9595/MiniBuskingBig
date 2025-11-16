'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { paymentApi } from '@/lib/api/payment';
import { useTossPayments } from '@/lib/hooks/useTossPayments';

export default function VipPage() {
  const router = useRouter();
  const { loading, requestPayment } = useTossPayments();
  const [processing, setProcessing] = useState(false);

  const handleSubscribe = async () => {
    setProcessing(true);

    try {
      // 1. 결제 생성
      const payment = await paymentApi.createVipSubscriptionPayment();

      // 2. 토스페이먼츠 결제 요청
      await requestPayment({
        amount: payment.data.amount,
        orderId: payment.data.orderId,
        orderName: payment.data.itemName,
        successUrl: `${window.location.origin}/payment/success`,
        failUrl: `${window.location.origin}/payment/fail`,
      });
    } catch (error) {
      console.error('VIP subscription failed:', error);
      alert('VIP 구독에 실패했습니다.');
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
          <h1 className="text-4xl font-bold text-gray-900 mb-4">VIP 멤버십</h1>
          <p className="text-lg text-gray-600">
            프리미엄 기능을 이용하고 최고의 경험을 누리세요
          </p>
        </div>

        {/* Pricing Card */}
        <div className="max-w-md mx-auto">
          <div className="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl shadow-xl overflow-hidden">
            <div className="p-8 text-white">
              <div className="text-center mb-6">
                <div className="inline-block px-4 py-1 bg-white/20 rounded-full text-sm font-medium mb-4">
                  프리미엄
                </div>
                <h2 className="text-3xl font-bold mb-2">VIP 멤버십</h2>
                <div className="flex items-baseline justify-center gap-2">
                  <span className="text-5xl font-bold">9,900</span>
                  <span className="text-xl">원/월</span>
                </div>
              </div>

              {/* Features */}
              <ul className="space-y-4 mb-8">
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-white flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span>모든 공연 무제한 시청</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-white flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span>채팅 우선 참여 권한</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-white flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span>VIP 전용 배지 표시</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-white flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span>고화질 스트리밍</span>
                </li>
                <li className="flex items-start gap-3">
                  <svg className="w-6 h-6 text-white flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  <span>가수와의 특별 소통 기회</span>
                </li>
              </ul>

              <button
                onClick={handleSubscribe}
                disabled={loading || processing}
                className="w-full px-6 py-4 bg-white text-indigo-600 font-bold rounded-lg hover:bg-gray-100 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                {processing ? '처리 중...' : 'VIP 멤버십 구독하기'}
              </button>
            </div>
          </div>

          {/* Additional Info */}
          <div className="mt-8 text-center text-sm text-gray-600">
            <p>매월 자동 결제됩니다.</p>
            <p className="mt-1">언제든지 구독을 취소할 수 있습니다.</p>
          </div>
        </div>
      </main>
    </div>
  );
}
