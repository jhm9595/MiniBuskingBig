import { useEffect, useState } from 'react';

declare global {
  interface Window {
    TossPayments: any;
  }
}

export function useTossPayments() {
  const [tossPayments, setTossPayments] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const clientKey = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY || 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq';

    // 토스페이먼츠 SDK 로드
    const script = document.createElement('script');
    script.src = 'https://js.tosspayments.com/v1/payment';
    script.async = true;

    script.onload = () => {
      if (window.TossPayments) {
        const tossPaymentsInstance = window.TossPayments(clientKey);
        setTossPayments(tossPaymentsInstance);
        setLoading(false);
      }
    };

    script.onerror = () => {
      console.error('Failed to load TossPayments SDK');
      setLoading(false);
    };

    document.body.appendChild(script);

    return () => {
      if (script.parentNode) {
        script.parentNode.removeChild(script);
      }
    };
  }, []);

  const requestPayment = async (options: {
    amount: number;
    orderId: string;
    orderName: string;
    customerName?: string;
    successUrl: string;
    failUrl: string;
  }) => {
    if (!tossPayments) {
      throw new Error('TossPayments is not initialized');
    }

    try {
      await tossPayments.requestPayment('카드', {
        amount: options.amount,
        orderId: options.orderId,
        orderName: options.orderName,
        customerName: options.customerName || '고객',
        successUrl: options.successUrl,
        failUrl: options.failUrl,
      });
    } catch (error) {
      console.error('Payment request failed:', error);
      throw error;
    }
  };

  return { tossPayments, loading, requestPayment };
}
