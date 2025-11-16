package com.minibuskingbig.advertisement.service;

import com.minibuskingbig.advertisement.entity.AdStatus;
import com.minibuskingbig.advertisement.entity.AdType;
import com.minibuskingbig.advertisement.entity.Advertisement;
import com.minibuskingbig.advertisement.repository.AdvertisementRepository;
import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    /**
     * 광고 생성
     */
    @Transactional
    public Advertisement createAdvertisement(Long advertiserId, String title, String description,
                                              AdType type, String imageUrl, String videoUrl, String targetUrl,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              BigDecimal budget, BigDecimal costPerClick, BigDecimal costPerImpression) {
        User advertiser = userRepository.findById(advertiserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Advertisement ad = Advertisement.create(advertiser, title, description,
                type, imageUrl, videoUrl, targetUrl,
                startDate, endDate, budget, costPerClick, costPerImpression);

        return advertisementRepository.save(ad);
    }

    /**
     * 광고 수정
     */
    @Transactional
    public Advertisement updateAdvertisement(Long adId, Long advertiserId,
                                              String title, String description,
                                              String imageUrl, String videoUrl, String targetUrl,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              BigDecimal budget, BigDecimal costPerClick, BigDecimal costPerImpression) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!ad.getAdvertiser().getId().equals(advertiserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        ad.updateInfo(title, description, imageUrl, videoUrl, targetUrl,
                startDate, endDate, budget, costPerClick, costPerImpression);

        return ad;
    }

    /**
     * 광고 승인 (관리자)
     */
    @Transactional
    public Advertisement approveAdvertisement(Long adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        ad.approve();
        return ad;
    }

    /**
     * 광고 거절 (관리자)
     */
    @Transactional
    public Advertisement rejectAdvertisement(Long adId, String reason) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        ad.reject(reason);
        return ad;
    }

    /**
     * 광고 일시 정지
     */
    @Transactional
    public Advertisement pauseAdvertisement(Long adId, Long advertiserId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!ad.getAdvertiser().getId().equals(advertiserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        ad.pause();
        return ad;
    }

    /**
     * 광고 재개
     */
    @Transactional
    public Advertisement resumeAdvertisement(Long adId, Long advertiserId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!ad.getAdvertiser().getId().equals(advertiserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        ad.resume();
        return ad;
    }

    /**
     * 광고 조회
     */
    public Advertisement getAdvertisement(Long adId) {
        return advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    /**
     * 광고주별 광고 목록
     */
    public Page<Advertisement> getAdvertisementsByAdvertiser(Long advertiserId, Pageable pageable) {
        return advertisementRepository.findByAdvertiserId(advertiserId, pageable);
    }

    /**
     * 상태별 광고 목록 (관리자)
     */
    public Page<Advertisement> getAdvertisementsByStatus(AdStatus status, Pageable pageable) {
        return advertisementRepository.findByStatus(status, pageable);
    }

    /**
     * 노출할 광고 가져오기 (랜덤)
     */
    public Advertisement getRandomActiveAd(AdType type) {
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> activeAds;

        if (type != null) {
            activeAds = advertisementRepository.findActiveAdsByType(now, type);
        } else {
            activeAds = advertisementRepository.findActiveAds(now);
        }

        if (activeAds.isEmpty()) {
            return null;
        }

        return activeAds.get(random.nextInt(activeAds.size()));
    }

    /**
     * 광고 노출 기록
     */
    @Transactional
    public void recordImpression(Long adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        ad.recordImpression();
    }

    /**
     * 광고 클릭 기록
     */
    @Transactional
    public void recordClick(Long adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        ad.recordClick();
    }

    /**
     * 광고 삭제
     */
    @Transactional
    public void deleteAdvertisement(Long adId, Long advertiserId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!ad.getAdvertiser().getId().equals(advertiserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        advertisementRepository.delete(ad);
    }

    /**
     * 만료된 광고 처리 (스케줄러용)
     */
    @Transactional
    public void processExpiredAds() {
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> expiredAds = advertisementRepository.findExpiredAds(now);

        expiredAds.forEach(Advertisement::expire);
    }

    /**
     * 광고주 통계
     */
    public AdvertiserStats getAdvertiserStats(Long advertiserId) {
        Long totalSpent = advertisementRepository.getTotalSpentByAdvertiser(advertiserId);
        Long totalImpressions = advertisementRepository.getTotalImpressionsByAdvertiser(advertiserId);
        Long totalClicks = advertisementRepository.getTotalClicksByAdvertiser(advertiserId);

        return new AdvertiserStats(
                totalSpent != null ? totalSpent : 0L,
                totalImpressions != null ? totalImpressions : 0L,
                totalClicks != null ? totalClicks : 0L
        );
    }

    public record AdvertiserStats(Long totalSpent, Long totalImpressions, Long totalClicks) {}
}
