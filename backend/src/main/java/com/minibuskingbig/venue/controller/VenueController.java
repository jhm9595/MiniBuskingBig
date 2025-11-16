package com.minibuskingbig.venue.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.venue.entity.Venue;
import com.minibuskingbig.venue.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    /**
     * 장소 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Venue>> createVenue(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        Venue venue = venueService.createVenue(
                userId,
                (String) request.get("name"),
                (String) request.get("description"),
                (String) request.get("address"),
                (String) request.get("addressDetail"),
                (String) request.get("postalCode"),
                new BigDecimal(request.get("latitude").toString()),
                new BigDecimal(request.get("longitude").toString()),
                (Integer) request.get("capacity"),
                new BigDecimal(request.get("hourlyRate").toString()),
                (String) request.get("openTime"),
                (String) request.get("closeTime"),
                (String) request.get("contactPhone"),
                (String) request.get("contactEmail")
        );

        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 장소 정보 수정
     */
    @PutMapping("/{venueId}")
    public ResponseEntity<ApiResponse<Venue>> updateVenue(
            @PathVariable Long venueId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        Venue venue = venueService.updateVenue(
                venueId, userId,
                (String) request.get("name"),
                (String) request.get("description"),
                (String) request.get("address"),
                (String) request.get("addressDetail"),
                (String) request.get("postalCode"),
                new BigDecimal(request.get("latitude").toString()),
                new BigDecimal(request.get("longitude").toString()),
                (Integer) request.get("capacity"),
                new BigDecimal(request.get("hourlyRate").toString()),
                (String) request.get("openTime"),
                (String) request.get("closeTime"),
                (String) request.get("contactPhone"),
                (String) request.get("contactEmail")
        );

        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 장소 조회
     */
    @GetMapping("/{venueId}")
    public ResponseEntity<ApiResponse<Venue>> getVenue(@PathVariable Long venueId) {
        Venue venue = venueService.getVenue(venueId);
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 활성 장소 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Venue>>> getActiveVenues(Pageable pageable) {
        Page<Venue> venues = venueService.getActiveVenues(pageable);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    /**
     * 내 장소 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<Venue>>> getMyVenues(
            Authentication authentication,
            Pageable pageable) {
        Long userId = Long.parseLong(authentication.getName());
        Page<Venue> venues = venueService.getVenuesByProvider(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    /**
     * 주변 장소 검색
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<Venue>>> getNearbyVenues(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        List<Venue> venues = venueService.getNearbyVenues(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    /**
     * 장소 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Venue>>> searchVenues(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<Venue> venues = venueService.searchVenues(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    /**
     * 장소 활성화/비활성화
     */
    @PatchMapping("/{venueId}/toggle")
    public ResponseEntity<ApiResponse<Venue>> toggleVenueStatus(
            @PathVariable Long venueId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Venue venue = venueService.toggleVenueStatus(venueId, userId);
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 장소 삭제
     */
    @DeleteMapping("/{venueId}")
    public ResponseEntity<ApiResponse<Void>> deleteVenue(
            @PathVariable Long venueId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        venueService.deleteVenue(venueId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 시설 정보 업데이트
     */
    @PatchMapping("/{venueId}/facilities")
    public ResponseEntity<ApiResponse<Venue>> updateFacilities(
            @PathVariable Long venueId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Venue venue = venueService.updateFacilities(venueId, userId, request.get("facilities"));
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 사진 업데이트
     */
    @PatchMapping("/{venueId}/photos")
    public ResponseEntity<ApiResponse<Venue>> updatePhotos(
            @PathVariable Long venueId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Venue venue = venueService.updatePhotos(venueId, userId, request.get("photos"));
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    // ===== 관리자 API =====

    /**
     * 장소 승인 (관리자)
     */
    @PostMapping("/{venueId}/approve")
    public ResponseEntity<ApiResponse<Venue>> approveVenue(@PathVariable Long venueId) {
        Venue venue = venueService.approveVenue(venueId);
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 장소 거절 (관리자)
     */
    @PostMapping("/{venueId}/reject")
    public ResponseEntity<ApiResponse<Venue>> rejectVenue(
            @PathVariable Long venueId,
            @RequestBody Map<String, String> request) {
        Venue venue = venueService.rejectVenue(venueId, request.get("reason"));
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    /**
     * 장소 정지 (관리자)
     */
    @PostMapping("/{venueId}/suspend")
    public ResponseEntity<ApiResponse<Venue>> suspendVenue(
            @PathVariable Long venueId,
            @RequestBody Map<String, String> request) {
        Venue venue = venueService.suspendVenue(venueId, request.get("reason"));
        return ResponseEntity.ok(ApiResponse.success(venue));
    }
}
