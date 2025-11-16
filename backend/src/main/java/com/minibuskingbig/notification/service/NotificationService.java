package com.minibuskingbig.notification.service;

import com.minibuskingbig.notification.entity.Notification;
import com.minibuskingbig.notification.entity.NotificationType;
import com.minibuskingbig.notification.repository.NotificationRepository;
import com.minibuskingbig.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // SSE emitters for real-time notifications
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE timeout: 30 minutes
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    /**
     * SSE 연결 생성
     */
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE connection completed for user: {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for user: {}", userId);
            emitters.remove(userId);
        });

        emitter.onError((e) -> {
            log.error("SSE connection error for user: {}", userId, e);
            emitters.remove(userId);
        });

        try {
            // 연결 확인용 초기 이벤트 전송
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event", e);
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * 알림 생성 및 전송
     */
    @Transactional
    public Notification createAndSendNotification(User user, NotificationType type,
                                                   String title, String message, String linkUrl) {
        Notification notification = Notification.create(user, type, title, message, linkUrl);
        notification = notificationRepository.save(notification);

        // SSE를 통해 실시간 전송
        sendToClient(user.getId(), notification);

        return notification;
    }

    /**
     * SSE를 통해 알림 전송
     */
    private void sendToClient(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(Map.of(
                                "id", notification.getId(),
                                "type", notification.getType(),
                                "title", notification.getTitle(),
                                "message", notification.getMessage(),
                                "linkUrl", notification.getLinkUrl(),
                                "createdAt", notification.getCreatedAt()
                        )));
            } catch (IOException e) {
                log.error("Failed to send SSE notification to user: {}", userId, e);
                emitters.remove(userId);
            }
        }
    }

    /**
     * 사용자의 알림 목록 조회
     */
    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 읽지 않은 알림 목록 조회
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to notification");
        }

        notification.markAsRead();
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        unreadNotifications.forEach(Notification::markAsRead);
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
    }

    /**
     * 오래된 알림 삭제 (30일 이전)
     */
    @Transactional
    public void deleteOldNotifications(Long userId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, thirtyDaysAgo);
    }
}
