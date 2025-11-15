package com.minibuskingbig.chat.repository;

import com.minibuskingbig.chat.entity.ChatMessage;
import com.minibuskingbig.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomAndIsDeletedFalseOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findByChatRoomAndCreatedAtAfterAndIsDeletedFalse(
        ChatRoom chatRoom,
        LocalDateTime after
    );

    long countByChatRoomAndIsDeletedFalse(ChatRoom chatRoom);
}
