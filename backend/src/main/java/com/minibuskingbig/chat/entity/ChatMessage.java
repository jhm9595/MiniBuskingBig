package com.minibuskingbig.chat.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_room", columnList = "room_id,created_at"),
    @Index(name = "idx_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message_type", length = 20)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // 비즈니스 로직
    public void delete() {
        this.isDeleted = true;
    }
}
