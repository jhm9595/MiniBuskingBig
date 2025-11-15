package com.minibuskingbig.favorite.repository;

import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.favorite.entity.EventFavorite;
import com.minibuskingbig.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventFavoriteRepository extends JpaRepository<EventFavorite, Long> {

    Optional<EventFavorite> findByUserAndEvent(User user, Event event);

    List<EventFavorite> findByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndEvent(User user, Event event);

    void deleteByUserAndEvent(User user, Event event);
}
