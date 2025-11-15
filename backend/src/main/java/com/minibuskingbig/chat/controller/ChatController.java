package com.minibuskingbig.chat.controller;

import com.minibuskingbig.chat.dto.ChatMessageDto;
import com.minibuskingbig.chat.dto.ChatMessageRequest;
import com.minibuskingbig.chat.dto.ChatRoomResponse;
import com.minibuskingbig.chat.entity.ChatMessage;
import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.chat.service.ChatMessageService;
import com.minibuskingbig.chat.service.ChatRoomService;
import com.minibuskingbig.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/rooms")
    public ApiResponse<ChatRoomResponse> createChatRoom(
        @AuthenticationPrincipal Long userId,
        @RequestParam Long eventId
    ) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(eventId);
        return ApiResponse.success(ChatRoomResponse.from(chatRoom), "채팅방이 생성되었습니다.");
    }

    @GetMapping("/rooms/{roomId}")
    public ApiResponse<ChatRoomResponse> getChatRoom(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);
        return ApiResponse.success(ChatRoomResponse.from(chatRoom));
    }

    @GetMapping("/rooms/event/{eventId}")
    public ApiResponse<ChatRoomResponse> getChatRoomByEventId(@PathVariable Long eventId) {
        ChatRoom chatRoom = chatRoomService.getChatRoomByEventId(eventId);
        return ApiResponse.success(ChatRoomResponse.from(chatRoom));
    }

    @PostMapping("/rooms/{roomId}/join")
    public ApiResponse<Void> joinChatRoom(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long roomId
    ) {
        chatMessageService.joinChatRoom(userId, roomId);
        return ApiResponse.success(null, "채팅방에 입장했습니다.");
    }

    @PostMapping("/rooms/{roomId}/leave")
    public ApiResponse<Void> leaveChatRoom(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long roomId
    ) {
        chatMessageService.leaveChatRoom(userId, roomId);
        return ApiResponse.success(null, "채팅방을 나갔습니다.");
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<Page<ChatMessageDto>> getMessages(
        @PathVariable Long roomId,
        Pageable pageable
    ) {
        Page<ChatMessage> messages = chatMessageService.getMessages(roomId, pageable);
        Page<ChatMessageDto> messageDtos = messages.map(ChatMessageDto::from);
        return ApiResponse.success(messageDtos);
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long messageId
    ) {
        chatMessageService.deleteMessage(userId, messageId);
        return ApiResponse.success(null, "메시지가 삭제되었습니다.");
    }

    // WebSocket message handling
    @MessageMapping("/chat.send")
    public void sendMessage(
        @Payload ChatMessageRequest request,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

        ChatMessage message = chatMessageService.sendMessage(
            userId,
            request.getRoomId(),
            request.getMessageType(),
            request.getContent()
        );

        ChatMessageDto messageDto = ChatMessageDto.from(message);

        // 해당 채팅방의 모든 참가자에게 메시지 브로드캐스트
        messagingTemplate.convertAndSend(
            "/topic/chat/" + request.getRoomId(),
            messageDto
        );
    }
}
