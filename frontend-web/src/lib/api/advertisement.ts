import { apiClient } from './api-client';

export interface Advertisement {
  id: number;
  title: string;
  description?: string;
  type: 'BANNER' | 'VIDEO' | 'POPUP' | 'NATIVE';
  imageUrl?: string;
  videoUrl?: string;
  targetUrl?: string;
  startDate: string;
  endDate: string;
  budget: number;
  costPerClick: number;
  costPerImpression: number;
  impressions: number;
  clicks: number;
  totalSpent: number;
  status: 'PENDING' | 'ACTIVE' | 'PAUSED' | 'EXPIRED' | 'REJECTED';
  rejectionReason?: string;
  approvedAt?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface AdvertiserStats {
  totalSpent: number;
  totalImpressions: number;
  totalClicks: number;
}

export const advertisementApi = {
  // 광고 생성
  createAdvertisement: async (data: {
    title: string;
    description?: string;
    type: string;
    imageUrl?: string;
    videoUrl?: string;
    targetUrl?: string;
    startDate: string;
    endDate: string;
    budget: number;
    costPerClick: number;
    costPerImpression: number;
  }) => {
    return apiClient.post<Advertisement>('/api/advertisements', data);
  },

  // 광고 수정
  updateAdvertisement: async (adId: number, data: Partial<Advertisement>) => {
    return apiClient.put<Advertisement>(`/api/advertisements/${adId}`, data);
  },

  // 광고 조회
  getAdvertisement: async (adId: number) => {
    return apiClient.get<Advertisement>(`/api/advertisements/${adId}`);
  },

  // 내 광고 목록
  getMyAdvertisements: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: Advertisement[] }>(
      `/api/advertisements/my?page=${page}&size=${size}`
    );
  },

  // 광고 일시 정지
  pauseAdvertisement: async (adId: number) => {
    return apiClient.post<Advertisement>(`/api/advertisements/${adId}/pause`);
  },

  // 광고 재개
  resumeAdvertisement: async (adId: number) => {
    return apiClient.post<Advertisement>(`/api/advertisements/${adId}/resume`);
  },

  // 광고 삭제
  deleteAdvertisement: async (adId: number) => {
    return apiClient.delete(`/api/advertisements/${adId}`);
  },

  // 광고주 통계
  getAdvertiserStats: async () => {
    return apiClient.get<AdvertiserStats>('/api/advertisements/stats');
  },

  // 랜덤 광고 가져오기
  getRandomAd: async (type?: string) => {
    const params = type ? `?type=${type}` : '';
    return apiClient.get<Advertisement>(`/api/advertisements/random${params}`);
  },

  // 광고 노출 기록
  recordImpression: async (adId: number) => {
    return apiClient.post(`/api/advertisements/${adId}/impression`);
  },

  // 광고 클릭 기록
  recordClick: async (adId: number) => {
    return apiClient.post(`/api/advertisements/${adId}/click`);
  },
};

export const adminApi = {
  // 대시보드 통계
  getDashboardStats: async () => {
    return apiClient.get<any>('/api/admin/dashboard/stats');
  },

  // 시스템 상태
  getSystemStatus: async () => {
    return apiClient.get<any>('/api/admin/system/status');
  },

  // 최근 활동
  getRecentActivity: async (page: number = 0, size: number = 20) => {
    return apiClient.get<any>(`/api/admin/activity/recent?page=${page}&size=${size}`);
  },

  // 공지사항 작성
  createAnnouncement: async (data: { title: string; content: string }) => {
    return apiClient.post('/api/admin/announcements', data);
  },

  // 사용자 정지
  banUser: async (userId: number, reason: string) => {
    return apiClient.post(`/api/admin/users/${userId}/ban`, { reason });
  },

  // 사용자 정지 해제
  unbanUser: async (userId: number) => {
    return apiClient.post(`/api/admin/users/${userId}/unban`);
  },
};
