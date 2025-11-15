package com.minibuskingbig.chat.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participants", indexes = {
    @Index(name = "idx_room_user", columnList = "room_id,user_id", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    // 비즈니스 로직
    public void leave() {
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    public void rejoin() {
        this.isActive = true;
        this.leftAt = null;
    }
}
