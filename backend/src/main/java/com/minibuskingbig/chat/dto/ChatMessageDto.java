package com.minibuskingbig.chat.dto;

import com.minibuskingbig.chat.entity.ChatMessage;
import com.minibuskingbig.chat.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private Long messageId;
    private Long roomId;
    private Long userId;
    private String userDisplayId;
    private String userNickname;
    private String userProfileImageUrl;
    private MessageType messageType;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageDto from(ChatMessage message) {
        return ChatMessageDto.builder()
            .messageId(message.getMessageId())
            .roomId(message.getChatRoom().getRoomId())
            .userId(message.getUser().getUserId())
            .userDisplayId(message.getUser().getDisplayId())
            .userNickname(message.getUser().getNickname())
            .userProfileImageUrl(message.getUser().getProfileImageUrl())
            .messageType(message.getMessageType())
            .content(message.getContent())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
