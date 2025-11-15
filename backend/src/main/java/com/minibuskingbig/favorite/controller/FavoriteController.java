package com.minibuskingbig.favorite.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.event.dto.EventResponse;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/events/{eventId}")
    public ApiResponse<Void> addFavorite(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        favoriteService.addFavorite(userId, eventId);
        return ApiResponse.success(null, "즐겨찾기에 추가되었습니다.");
    }

    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> removeFavorite(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        favoriteService.removeFavorite(userId, eventId);
        return ApiResponse.success(null, "즐겨찾기가 취소되었습니다.");
    }

    @GetMapping("/events")
    public ApiResponse<List<EventResponse>> getFavoriteEvents(
        @AuthenticationPrincipal Long userId
    ) {
        List<Event> events = favoriteService.getFavoriteEvents(userId);
        List<EventResponse> eventResponses = events.stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ApiResponse.success(eventResponses);
    }

    @GetMapping("/events/{eventId}/check")
    public ApiResponse<Boolean> checkFavorite(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long eventId
    ) {
        boolean isFavorite = favoriteService.isFavorite(userId, eventId);
        return ApiResponse.success(isFavorite);
    }
}
