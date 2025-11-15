package com.minibuskingbig.event.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.event.dto.EventCreateRequest;
import com.minibuskingbig.event.dto.EventResponse;
import com.minibuskingbig.event.dto.EventUpdateRequest;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ApiResponse<EventResponse> createEvent(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody EventCreateRequest request
    ) {
        Event event = eventService.createEvent(userId, request);
        return ApiResponse.success(EventResponse.from(event), "공연이 등록되었습니다.");
    }

    @GetMapping
    public ApiResponse<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ApiResponse.success(events);
    }

    @GetMapping("/live")
    public ApiResponse<List<EventResponse>> getLiveEvents() {
        List<EventResponse> events = eventService.getLiveEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ApiResponse.success(events);
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> events = eventService.getUpcomingEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ApiResponse.success(events);
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        eventService.incrementViewCount(eventId);
        return ApiResponse.success(EventResponse.from(event));
    }

    @PutMapping("/{eventId}")
    public ApiResponse<EventResponse> updateEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId,
        @Valid @RequestBody EventUpdateRequest request
    ) {
        Event event = eventService.updateEvent(userId, eventId, request);
        return ApiResponse.success(EventResponse.from(event), "공연 정보가 수정되었습니다.");
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        eventService.deleteEvent(userId, eventId);
        return ApiResponse.success(null, "공연이 취소되었습니다.");
    }

    @PostMapping("/{eventId}/start")
    public ApiResponse<Void> startEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        eventService.startEvent(userId, eventId);
        return ApiResponse.success(null, "공연이 시작되었습니다.");
    }

    @PostMapping("/{eventId}/end")
    public ApiResponse<Void> endEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        eventService.endEvent(userId, eventId);
        return ApiResponse.success(null, "공연이 종료되었습니다.");
    }
}
