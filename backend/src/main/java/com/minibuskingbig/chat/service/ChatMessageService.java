package com.minibuskingbig.chat.service;

import com.minibuskingbig.chat.entity.ChatMessage;
import com.minibuskingbig.chat.entity.ChatParticipant;
import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.chat.entity.MessageType;
import com.minibuskingbig.chat.repository.ChatMessageRepository;
import com.minibuskingbig.chat.repository.ChatParticipantRepository;
import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Transactional
    public ChatMessage sendMessage(Long userId, Long roomId, MessageType messageType, String content) {
        User user = userService.getUserById(userId);
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);

        // 채팅방이 활성 상태인지 확인
        if (!chatRoom.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채팅방이 활성 상태가 아닙니다.");
        }

        // 참가자인지 확인
        if (!chatParticipantRepository.existsByChatRoomAndUserAndIsActiveTrue(chatRoom, user)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "채팅방 참가자가 아닙니다.");
        }

        ChatMessage message = ChatMessage.builder()
            .chatRoom(chatRoom)
            .user(user)
            .messageType(messageType)
            .content(content)
            .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 메시지 카운트 증가
        chatRoomService.incrementMessageCount(roomId);

        log.info("Message sent: {} in room: {} by user: {}", savedMessage.getMessageId(), roomId, userId);
        return savedMessage;
    }

    public Page<ChatMessage> getMessages(Long roomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);
        return chatMessageRepository.findByChatRoomAndIsDeletedFalseOrderByCreatedAtDesc(chatRoom, pageable);
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // 본인의 메시지인지 확인
        if (!message.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 메시지만 삭제할 수 있습니다.");
        }

        message.delete();
        log.info("Message deleted: {} by user: {}", messageId, userId);
    }

    @Transactional
    public ChatParticipant joinChatRoom(Long userId, Long roomId) {
        User user = userService.getUserById(userId);
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);

        // 이미 참가 중인지 확인
        ChatParticipant existingParticipant = chatParticipantRepository
            .findByChatRoomAndUser(chatRoom, user)
            .orElse(null);

        if (existingParticipant != null && existingParticipant.getIsActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 참가 중입니다.");
        }

        // 재입장인 경우
        if (existingParticipant != null) {
            existingParticipant.rejoin();
            chatRoomService.incrementParticipants(roomId);
            log.info("User rejoined chat room: {} - user: {}", roomId, userId);
            return existingParticipant;
        }

        // 새로 입장
        chatRoomService.incrementParticipants(roomId);

        ChatParticipant participant = ChatParticipant.builder()
            .chatRoom(chatRoom)
            .user(user)
            .joinedAt(LocalDateTime.now())
            .build();

        ChatParticipant savedParticipant = chatParticipantRepository.save(participant);
        log.info("User joined chat room: {} - user: {}", roomId, userId);

        return savedParticipant;
    }

    @Transactional
    public void leaveChatRoom(Long userId, Long roomId) {
        User user = userService.getUserById(userId);
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "참가 정보를 찾을 수 없습니다."));

        participant.leave();
        chatRoomService.decrementParticipants(roomId);

        log.info("User left chat room: {} - user: {}", roomId, userId);
    }
}
