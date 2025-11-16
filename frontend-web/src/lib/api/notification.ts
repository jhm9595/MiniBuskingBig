import { apiClient } from './api-client';

export interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  linkUrl?: string;
  isRead: boolean;
  createdAt: string;
  readAt?: string;
}

export const notificationApi = {
  // 알림 목록 조회
  getNotifications: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: Notification[] }>(`/api/notifications?page=${page}&size=${size}`);
  },

  // 읽지 않은 알림 조회
  getUnreadNotifications: async () => {
    return apiClient.get<Notification[]>('/api/notifications/unread');
  },

  // 읽지 않은 알림 개수 조회
  getUnreadCount: async () => {
    return apiClient.get<number>('/api/notifications/unread/count');
  },

  // 알림 읽음 처리
  markAsRead: async (notificationId: number) => {
    return apiClient.patch(`/api/notifications/${notificationId}/read`);
  },

  // 모든 알림 읽음 처리
  markAllAsRead: async () => {
    return apiClient.patch('/api/notifications/read-all');
  },

  // 알림 삭제
  deleteNotification: async (notificationId: number) => {
    return apiClient.delete(`/api/notifications/${notificationId}`);
  },

  // 오래된 알림 삭제
  deleteOldNotifications: async () => {
    return apiClient.delete('/api/notifications/old');
  },
};
