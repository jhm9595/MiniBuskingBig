package com.minibuskingbig.advertisement.controller;

import com.minibuskingbig.advertisement.entity.AdStatus;
import com.minibuskingbig.advertisement.entity.AdType;
import com.minibuskingbig.advertisement.entity.Advertisement;
import com.minibuskingbig.advertisement.service.AdvertisementService;
import com.minibuskingbig.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * 광고 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Advertisement>> createAdvertisement(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        Advertisement ad = advertisementService.createAdvertisement(
                userId,
                (String) request.get("title"),
                (String) request.get("description"),
                AdType.valueOf((String) request.get("type")),
                (String) request.get("imageUrl"),
                (String) request.get("videoUrl"),
                (String) request.get("targetUrl"),
                LocalDateTime.parse((String) request.get("startDate")),
                LocalDateTime.parse((String) request.get("endDate")),
                new BigDecimal(request.get("budget").toString()),
                new BigDecimal(request.get("costPerClick").toString()),
                new BigDecimal(request.get("costPerImpression").toString())
        );

        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 수정
     */
    @PutMapping("/{adId}")
    public ResponseEntity<ApiResponse<Advertisement>> updateAdvertisement(
            @PathVariable Long adId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        Advertisement ad = advertisementService.updateAdvertisement(
                adId, userId,
                (String) request.get("title"),
                (String) request.get("description"),
                (String) request.get("imageUrl"),
                (String) request.get("videoUrl"),
                (String) request.get("targetUrl"),
                LocalDateTime.parse((String) request.get("startDate")),
                LocalDateTime.parse((String) request.get("endDate")),
                new BigDecimal(request.get("budget").toString()),
                new BigDecimal(request.get("costPerClick").toString()),
                new BigDecimal(request.get("costPerImpression").toString())
        );

        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 조회
     */
    @GetMapping("/{adId}")
    public ResponseEntity<ApiResponse<Advertisement>> getAdvertisement(@PathVariable Long adId) {
        Advertisement ad = advertisementService.getAdvertisement(adId);
        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 내 광고 목록
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<Advertisement>>> getMyAdvertisements(
            Authentication authentication,
            Pageable pageable) {
        Long userId = Long.parseLong(authentication.getName());
        Page<Advertisement> ads = advertisementService.getAdvertisementsByAdvertiser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(ads));
    }

    /**
     * 광고 일시 정지
     */
    @PostMapping("/{adId}/pause")
    public ResponseEntity<ApiResponse<Advertisement>> pauseAdvertisement(
            @PathVariable Long adId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Advertisement ad = advertisementService.pauseAdvertisement(adId, userId);
        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 재개
     */
    @PostMapping("/{adId}/resume")
    public ResponseEntity<ApiResponse<Advertisement>> resumeAdvertisement(
            @PathVariable Long adId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Advertisement ad = advertisementService.resumeAdvertisement(adId, userId);
        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 삭제
     */
    @DeleteMapping("/{adId}")
    public ResponseEntity<ApiResponse<Void>> deleteAdvertisement(
            @PathVariable Long adId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        advertisementService.deleteAdvertisement(adId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 광고주 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdvertisementService.AdvertiserStats>> getAdvertiserStats(
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        AdvertisementService.AdvertiserStats stats = advertisementService.getAdvertiserStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ===== 공개 API =====

    /**
     * 랜덤 광고 가져오기
     */
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<Advertisement>> getRandomAd(
            @RequestParam(required = false) AdType type) {
        Advertisement ad = advertisementService.getRandomActiveAd(type);
        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 노출 기록
     */
    @PostMapping("/{adId}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(@PathVariable Long adId) {
        advertisementService.recordImpression(adId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 광고 클릭 기록
     */
    @PostMapping("/{adId}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long adId) {
        advertisementService.recordClick(adId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ===== 관리자 API =====

    /**
     * 상태별 광고 목록 (관리자)
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<ApiResponse<Page<Advertisement>>> getAdvertisementsByStatus(
            @PathVariable AdStatus status,
            Pageable pageable) {
        Page<Advertisement> ads = advertisementService.getAdvertisementsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(ads));
    }

    /**
     * 광고 승인 (관리자)
     */
    @PostMapping("/{adId}/approve")
    public ResponseEntity<ApiResponse<Advertisement>> approveAdvertisement(@PathVariable Long adId) {
        Advertisement ad = advertisementService.approveAdvertisement(adId);
        return ResponseEntity.ok(ApiResponse.success(ad));
    }

    /**
     * 광고 거절 (관리자)
     */
    @PostMapping("/{adId}/reject")
    public ResponseEntity<ApiResponse<Advertisement>> rejectAdvertisement(
            @PathVariable Long adId,
            @RequestBody Map<String, String> request) {
        Advertisement ad = advertisementService.rejectAdvertisement(adId, request.get("reason"));
        return ResponseEntity.ok(ApiResponse.success(ad));
    }
}
