package com.minibuskingbig.event.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.event.dto.EventCreateRequest;
import com.minibuskingbig.event.dto.EventUpdateRequest;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.entity.EventStatus;
import com.minibuskingbig.event.repository.EventRepository;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.singer.repository.SingerProfileRepository;
import com.minibuskingbig.team.entity.Team;
import com.minibuskingbig.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final SingerProfileRepository singerProfileRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Event createEvent(Long userId, EventCreateRequest request) {
        SingerProfile singer = singerProfileRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException(ErrorCode.INVALID_EVENT_TIME);
        }

        Event.EventBuilder eventBuilder = Event.builder()
            .singer(singer)
            .title(request.getTitle())
            .description(request.getDescription())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .venueAddress(request.getVenueAddress())
            .venueLat(request.getVenueLat())
            .venueLng(request.getVenueLng());

        if (request.getTeamId() != null) {
            Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
            eventBuilder.team(team);
        }

        Event event = eventBuilder.build();

        if (Boolean.TRUE.equals(request.getChatEnabled())) {
            event.enableChat(request.getChatMaxParticipants() != null ? request.getChatMaxParticipants() : 100);
        }

        Event savedEvent = eventRepository.save(event);
        singer.incrementEventCount();

        log.info("Event created: {} by singer: {}", savedEvent.getEventId(), singer.getSingerId());
        return savedEvent;
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getLiveEvents() {
        return eventRepository.findLiveEvents();
    }

    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusWeeks(1);
        return eventRepository.findUpcomingEvents(EventStatus.SCHEDULED, now, oneWeekLater);
    }

    @Transactional
    public Event updateEvent(Long userId, Long eventId, EventUpdateRequest request) {
        Event event = getEventById(eventId);

        // Check ownership
        if (!event.getSinger().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (event.getStatus() == EventStatus.LIVE || event.getStatus() == EventStatus.ENDED) {
            throw new BusinessException(ErrorCode.EVENT_ALREADY_STARTED);
        }

        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new BusinessException(ErrorCode.INVALID_EVENT_TIME);
            }
            event.updateEventInfo(request.getTitle(), request.getDescription(), request.getStartTime(), request.getEndTime());
        }

        if (request.getVenueAddress() != null) {
            event.updateVenue(request.getVenueAddress(), request.getVenueLat(), request.getVenueLng());
        }

        return event;
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);

        if (!event.getSinger().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (event.getStatus() == EventStatus.LIVE) {
            throw new BusinessException(ErrorCode.EVENT_ALREADY_STARTED);
        }

        event.cancelEvent();
        log.info("Event cancelled: {}", eventId);
    }

    @Transactional
    public void startEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);

        if (!event.getSinger().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        event.startEvent();
        log.info("Event started: {}", eventId);
    }

    @Transactional
    public void endEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);

        if (!event.getSinger().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        event.endEvent();
        log.info("Event ended: {}", eventId);
    }

    @Transactional
    public void incrementViewCount(Long eventId) {
        Event event = getEventById(eventId);
        event.incrementViewCount();
    }
}
