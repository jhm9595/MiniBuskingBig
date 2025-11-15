package com.minibuskingbig.chat.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.event.entity.Event;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms", indexes = {
    @Index(name = "idx_event", columnList = "event_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "container_id", length = 100)
    private String containerId; // AWS ECS Container ID

    @Column(name = "container_arn", columnDefinition = "TEXT")
    private String containerArn; // AWS ECS Task ARN

    @Column(name = "websocket_url", columnDefinition = "TEXT")
    private String websocketUrl; // WebSocket endpoint URL

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatRoomStatus status = ChatRoomStatus.CREATING;

    @Builder.Default
    @Column(name = "current_participants")
    private Integer currentParticipants = 0;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Builder.Default
    @Column(name = "total_messages")
    private Integer totalMessages = 0;

    // 비즈니스 로직
    public void activate(String containerId, String containerArn, String websocketUrl) {
        this.containerId = containerId;
        this.containerArn = containerArn;
        this.websocketUrl = websocketUrl;
        this.status = ChatRoomStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = ChatRoomStatus.CLOSED;
        this.endedAt = LocalDateTime.now();
    }

    public void incrementParticipants() {
        if (this.currentParticipants < this.maxParticipants) {
            this.currentParticipants++;
        }
    }

    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void incrementMessageCount() {
        this.totalMessages++;
    }

    public boolean isFull() {
        return this.currentParticipants >= this.maxParticipants;
    }

    public boolean isActive() {
        return this.status == ChatRoomStatus.ACTIVE;
    }
}
