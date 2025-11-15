package com.minibuskingbig.chat.dto;

import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.chat.entity.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {

    private Long roomId;
    private Long eventId;
    private String eventTitle;
    private String websocketUrl;
    private ChatRoomStatus status;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private Integer totalMessages;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
            .roomId(chatRoom.getRoomId())
            .eventId(chatRoom.getEvent().getEventId())
            .eventTitle(chatRoom.getEvent().getTitle())
            .websocketUrl(chatRoom.getWebsocketUrl())
            .status(chatRoom.getStatus())
            .currentParticipants(chatRoom.getCurrentParticipants())
            .maxParticipants(chatRoom.getMaxParticipants())
            .totalMessages(chatRoom.getTotalMessages())
            .startedAt(chatRoom.getStartedAt())
            .endedAt(chatRoom.getEndedAt())
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }
}
