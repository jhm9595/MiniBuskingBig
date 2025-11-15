import { apiClient, ApiResponse } from './client';

export interface Payment {
  paymentId: number;
  orderId: string;
  paymentType: 'CHAT' | 'VIP_SUBSCRIPTION' | 'AD_FREE';
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  itemName: string;
  itemId?: number;
  paidAt?: string;
  createdAt: string;
}

export const paymentApi = {
  async createChatPayment(eventId: number): Promise<ApiResponse<Payment>> {
    return apiClient.post<Payment>(`/api/v1/payments/chat?eventId=${eventId}`);
  },

  async createVipSubscriptionPayment(): Promise<ApiResponse<Payment>> {
    return apiClient.post<Payment>('/api/v1/payments/vip');
  },

  async createAdFreePayment(): Promise<ApiResponse<Payment>> {
    return apiClient.post<Payment>('/api/v1/payments/ad-free');
  },

  async completePayment(orderId: string, pgTransactionId: string, pgResponse: string): Promise<ApiResponse<void>> {
    return apiClient.post<void>('/api/v1/payments/complete', {
      orderId,
      pgTransactionId,
      pgResponse,
    });
  },

  async cancelPayment(orderId: string, reason: string): Promise<ApiResponse<void>> {
    return apiClient.post<void>('/api/v1/payments/cancel', {
      orderId,
      reason,
    });
  },

  async getPayment(orderId: string): Promise<ApiResponse<Payment>> {
    return apiClient.get<Payment>(`/api/v1/payments/${orderId}`);
  },
};
