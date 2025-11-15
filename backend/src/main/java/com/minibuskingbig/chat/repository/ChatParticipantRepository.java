package com.minibuskingbig.chat.repository;

import com.minibuskingbig.chat.entity.ChatParticipant;
import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatParticipant> findByChatRoomAndIsActiveTrue(ChatRoom chatRoom);

    boolean existsByChatRoomAndUserAndIsActiveTrue(ChatRoom chatRoom, User user);

    long countByChatRoomAndIsActiveTrue(ChatRoom chatRoom);
}
