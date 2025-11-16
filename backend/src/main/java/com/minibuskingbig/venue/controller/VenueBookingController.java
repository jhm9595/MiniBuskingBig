package com.minibuskingbig.venue.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.venue.entity.VenueBooking;
import com.minibuskingbig.venue.service.VenueBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venue-bookings")
@RequiredArgsConstructor
public class VenueBookingController {

    private final VenueBookingService bookingService;

    /**
     * 장소 예약
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VenueBooking>> createBooking(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        VenueBooking booking = bookingService.createBooking(
                Long.parseLong(request.get("venueId").toString()),
                userId,
                LocalDateTime.parse(request.get("startTime").toString()),
                LocalDateTime.parse(request.get("endTime").toString()),
                (String) request.get("purpose"),
                (String) request.get("specialRequests")
        );

        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * 예약 확정 (제공자)
     */
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<VenueBooking>> confirmBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        VenueBooking booking = bookingService.confirmBooking(bookingId, userId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * 예약 취소
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<VenueBooking>> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        VenueBooking booking = bookingService.cancelBooking(
                bookingId, userId, request.get("reason"));
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * 예약 조회
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<VenueBooking>> getBooking(@PathVariable Long bookingId) {
        VenueBooking booking = bookingService.getBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * 내 예약 목록
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<VenueBooking>>> getMyBookings(
            Authentication authentication,
            Pageable pageable) {
        Long userId = Long.parseLong(authentication.getName());
        Page<VenueBooking> bookings = bookingService.getBookingsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    /**
     * 장소별 예약 목록
     */
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<ApiResponse<Page<VenueBooking>>> getVenueBookings(
            @PathVariable Long venueId,
            Pageable pageable) {
        Page<VenueBooking> bookings = bookingService.getBookingsByVenue(venueId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    /**
     * 제공자별 예약 목록
     */
    @GetMapping("/provider")
    public ResponseEntity<ApiResponse<Page<VenueBooking>>> getProviderBookings(
            Authentication authentication,
            Pageable pageable) {
        Long userId = Long.parseLong(authentication.getName());
        Page<VenueBooking> bookings = bookingService.getBookingsByProvider(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    /**
     * 이벤트 연결
     */
    @PostMapping("/{bookingId}/link-event")
    public ResponseEntity<ApiResponse<VenueBooking>> linkToEvent(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        VenueBooking booking = bookingService.linkToEvent(
                bookingId, request.get("eventId"), userId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * 예약 가능 여부 확인
     */
    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @RequestParam Long venueId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        boolean available = bookingService.isAvailable(
                venueId,
                LocalDateTime.parse(startTime),
                LocalDateTime.parse(endTime)
        );
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    /**
     * 예약된 시간대 조회
     */
    @GetMapping("/booked-slots")
    public ResponseEntity<ApiResponse<List<VenueBooking>>> getBookedTimeSlots(
            @RequestParam Long venueId,
            @RequestParam String date) {
        List<VenueBooking> bookings = bookingService.getBookedTimeSlots(
                venueId, LocalDateTime.parse(date));
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
}
