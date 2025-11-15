package com.minibuskingbig.chat.service;

import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.chat.entity.ChatRoomStatus;
import com.minibuskingbig.chat.repository.ChatRoomRepository;
import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ChatRoom createChatRoom(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        if (!event.getChatEnabled()) {
            throw new BusinessException(ErrorCode.CHAT_NOT_ENABLED);
        }

        if (chatRoomRepository.existsByEvent(event)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 채팅방이 존재합니다.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
            .event(event)
            .maxParticipants(event.getChatMaxParticipants())
            .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        log.info("Chat room created: {} for event: {}", savedRoom.getRoomId(), eventId);

        return savedRoom;
    }

    public ChatRoom getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
    }

    public ChatRoom getChatRoomByEventId(Long eventId) {
        return chatRoomRepository.findByEvent_EventId(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
    }

    @Transactional
    public void activateChatRoom(Long roomId, String containerId, String containerArn, String websocketUrl) {
        ChatRoom chatRoom = getChatRoomById(roomId);
        chatRoom.activate(containerId, containerArn, websocketUrl);
        log.info("Chat room activated: {} with container: {}", roomId, containerId);
    }

    @Transactional
    public void closeChatRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoomById(roomId);
        chatRoom.close();
        log.info("Chat room closed: {}", roomId);
    }

    @Transactional
    public void incrementParticipants(Long roomId) {
        ChatRoom chatRoom = getChatRoomById(roomId);

        if (chatRoom.isFull()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_FULL);
        }

        chatRoom.incrementParticipants();
    }

    @Transactional
    public void decrementParticipants(Long roomId) {
        ChatRoom chatRoom = getChatRoomById(roomId);
        chatRoom.decrementParticipants();
    }

    @Transactional
    public void incrementMessageCount(Long roomId) {
        ChatRoom chatRoom = getChatRoomById(roomId);
        chatRoom.incrementMessageCount();
    }
}
