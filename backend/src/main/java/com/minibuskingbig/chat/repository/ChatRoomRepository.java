package com.minibuskingbig.chat.repository;

import com.minibuskingbig.chat.entity.ChatRoom;
import com.minibuskingbig.chat.entity.ChatRoomStatus;
import com.minibuskingbig.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByEvent(Event event);

    Optional<ChatRoom> findByEvent_EventId(Long eventId);

    List<ChatRoom> findByStatus(ChatRoomStatus status);

    boolean existsByEvent(Event event);
}
