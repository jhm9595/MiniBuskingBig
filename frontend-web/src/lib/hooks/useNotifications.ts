import { useEffect, useState, useCallback, useRef } from 'react';
import { Notification } from '@/lib/api/notification';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

interface UseNotificationsOptions {
  onNotification?: (notification: Notification) => void;
  autoConnect?: boolean;
}

export function useNotifications(options: UseNotificationsOptions = {}) {
  const { onNotification, autoConnect = true } = options;
  const [connected, setConnected] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const eventSourceRef = useRef<EventSource | null>(null);

  const connect = useCallback(() => {
    // JWT 토큰 가져오기
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.warn('No access token available for SSE connection');
      return;
    }

    // EventSource는 헤더를 지원하지 않으므로 쿼리 파라미터로 토큰 전달
    const url = `${API_BASE_URL}/api/notifications/subscribe?token=${token}`;
    const eventSource = new EventSource(url);

    eventSource.addEventListener('connect', (event) => {
      console.log('SSE Connected:', event.data);
      setConnected(true);
    });

    eventSource.addEventListener('notification', (event) => {
      try {
        const notification: Notification = JSON.parse(event.data);
        console.log('Received notification:', notification);

        // 알림 목록에 추가
        setNotifications(prev => [notification, ...prev]);
        setUnreadCount(prev => prev + 1);

        // 콜백 호출
        if (onNotification) {
          onNotification(notification);
        }
      } catch (error) {
        console.error('Failed to parse notification:', error);
      }
    });

    eventSource.onerror = (error) => {
      console.error('SSE error:', error);
      setConnected(false);
      eventSource.close();

      // 5초 후 재연결 시도
      setTimeout(() => {
        if (autoConnect) {
          connect();
        }
      }, 5000);
    };

    eventSourceRef.current = eventSource;
  }, [onNotification, autoConnect]);

  const disconnect = useCallback(() => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
      setConnected(false);
    }
  }, []);

  const addNotification = useCallback((notification: Notification) => {
    setNotifications(prev => [notification, ...prev]);
    if (!notification.isRead) {
      setUnreadCount(prev => prev + 1);
    }
  }, []);

  const markAsRead = useCallback((notificationId: number) => {
    setNotifications(prev =>
      prev.map(n => (n.id === notificationId ? { ...n, isRead: true } : n))
    );
    setUnreadCount(prev => Math.max(0, prev - 1));
  }, []);

  const markAllAsRead = useCallback(() => {
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    setUnreadCount(0);
  }, []);

  const removeNotification = useCallback((notificationId: number) => {
    setNotifications(prev => {
      const notification = prev.find(n => n.id === notificationId);
      if (notification && !notification.isRead) {
        setUnreadCount(count => Math.max(0, count - 1));
      }
      return prev.filter(n => n.id !== notificationId);
    });
  }, []);

  const clearNotifications = useCallback(() => {
    setNotifications([]);
    setUnreadCount(0);
  }, []);

  useEffect(() => {
    if (autoConnect) {
      connect();
    }

    return () => {
      disconnect();
    };
  }, [autoConnect, connect, disconnect]);

  return {
    connected,
    notifications,
    unreadCount,
    connect,
    disconnect,
    addNotification,
    markAsRead,
    markAllAsRead,
    removeNotification,
    clearNotifications,
  };
}
