package com.minibuskingbig.favorite.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.repository.EventRepository;
import com.minibuskingbig.favorite.entity.EventFavorite;
import com.minibuskingbig.favorite.repository.EventFavoriteRepository;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final EventFavoriteRepository eventFavoriteRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Transactional
    public void addFavorite(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        if (eventFavoriteRepository.existsByUserAndEvent(user, event)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 즐겨찾기한 공연입니다.");
        }

        EventFavorite favorite = EventFavorite.builder()
            .user(user)
            .event(event)
            .build();

        eventFavoriteRepository.save(favorite);

        // 공연 즐겨찾기 카운트 증가
        event.incrementFavoriteCount();

        log.info("Event favorite added: event {} by user {}", eventId, userId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        if (!eventFavoriteRepository.existsByUserAndEvent(user, event)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "즐겨찾기를 찾을 수 없습니다.");
        }

        eventFavoriteRepository.deleteByUserAndEvent(user, event);

        // 공연 즐겨찾기 카운트 감소
        event.decrementFavoriteCount();

        log.info("Event favorite removed: event {} by user {}", eventId, userId);
    }

    public List<Event> getFavoriteEvents(Long userId) {
        User user = userService.getUserById(userId);
        List<EventFavorite> favorites = eventFavoriteRepository.findByUserOrderByCreatedAtDesc(user);
        return favorites.stream()
            .map(EventFavorite::getEvent)
            .toList();
    }

    public boolean isFavorite(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        return eventFavoriteRepository.existsByUserAndEvent(user, event);
    }
}
