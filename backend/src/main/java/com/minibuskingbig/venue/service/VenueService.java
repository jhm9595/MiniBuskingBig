package com.minibuskingbig.venue.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.repository.UserRepository;
import com.minibuskingbig.venue.entity.Venue;
import com.minibuskingbig.venue.entity.VenueStatus;
import com.minibuskingbig.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    /**
     * 장소 등록
     */
    @Transactional
    public Venue createVenue(Long providerId, String name, String description,
                              String address, String addressDetail, String postalCode,
                              BigDecimal latitude, BigDecimal longitude,
                              Integer capacity, BigDecimal hourlyRate,
                              String openTime, String closeTime,
                              String contactPhone, String contactEmail) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Venue venue = Venue.create(provider, name, description,
                address, addressDetail, postalCode,
                latitude, longitude,
                capacity, hourlyRate,
                openTime, closeTime,
                contactPhone, contactEmail);

        return venueRepository.save(venue);
    }

    /**
     * 장소 정보 수정
     */
    @Transactional
    public Venue updateVenue(Long venueId, Long providerId,
                              String name, String description,
                              String address, String addressDetail, String postalCode,
                              BigDecimal latitude, BigDecimal longitude,
                              Integer capacity, BigDecimal hourlyRate,
                              String openTime, String closeTime,
                              String contactPhone, String contactEmail) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!venue.getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        venue.updateInfo(name, description, address, addressDetail, postalCode,
                latitude, longitude, capacity, hourlyRate,
                openTime, closeTime, contactPhone, contactEmail);

        return venue;
    }

    /**
     * 장소 승인 (관리자)
     */
    @Transactional
    public Venue approveVenue(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        venue.approve();
        return venue;
    }

    /**
     * 장소 거절 (관리자)
     */
    @Transactional
    public Venue rejectVenue(Long venueId, String reason) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        venue.reject(reason);
        return venue;
    }

    /**
     * 장소 활성화/비활성화
     */
    @Transactional
    public Venue toggleVenueStatus(Long venueId, Long providerId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!venue.getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (venue.getStatus() == VenueStatus.ACTIVE) {
            venue.deactivate();
        } else if (venue.getStatus() == VenueStatus.INACTIVE) {
            venue.activate();
        }

        return venue;
    }

    /**
     * 장소 정지 (관리자)
     */
    @Transactional
    public Venue suspendVenue(Long venueId, String reason) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        venue.suspend(reason);
        return venue;
    }

    /**
     * 장소 조회
     */
    public Venue getVenue(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    /**
     * 활성 장소 목록 조회
     */
    public Page<Venue> getActiveVenues(Pageable pageable) {
        return venueRepository.findByStatus(VenueStatus.ACTIVE, pageable);
    }

    /**
     * 제공자별 장소 목록 조회
     */
    public Page<Venue> getVenuesByProvider(Long providerId, Pageable pageable) {
        return venueRepository.findByProviderId(providerId, pageable);
    }

    /**
     * 주변 장소 검색
     */
    public List<Venue> getNearbyVenues(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        // 위도/경도 기준 대략적인 범위 계산 (1도 ≈ 111km)
        BigDecimal latDiff = BigDecimal.valueOf(radiusKm / 111.0);
        BigDecimal lonDiff = BigDecimal.valueOf(radiusKm / (111.0 * Math.cos(Math.toRadians(latitude.doubleValue()))));

        BigDecimal minLat = latitude.subtract(latDiff);
        BigDecimal maxLat = latitude.add(latDiff);
        BigDecimal minLon = longitude.subtract(lonDiff);
        BigDecimal maxLon = longitude.add(lonDiff);

        return venueRepository.findNearbyVenues(minLat, maxLat, minLon, maxLon);
    }

    /**
     * 장소 검색
     */
    public Page<Venue> searchVenues(String keyword, Pageable pageable) {
        return venueRepository.searchActiveVenues(keyword, pageable);
    }

    /**
     * 장소 삭제
     */
    @Transactional
    public void deleteVenue(Long venueId, Long providerId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!venue.getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        venueRepository.delete(venue);
    }

    /**
     * 시설 정보 업데이트
     */
    @Transactional
    public Venue updateFacilities(Long venueId, Long providerId, String facilities) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!venue.getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        venue.setFacilities(facilities);
        return venue;
    }

    /**
     * 사진 업데이트
     */
    @Transactional
    public Venue updatePhotos(Long venueId, Long providerId, String photos) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!venue.getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        venue.setPhotos(photos);
        return venue;
    }
}
