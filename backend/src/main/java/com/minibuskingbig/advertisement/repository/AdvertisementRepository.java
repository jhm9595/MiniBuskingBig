package com.minibuskingbig.advertisement.repository;

import com.minibuskingbig.advertisement.entity.AdStatus;
import com.minibuskingbig.advertisement.entity.AdType;
import com.minibuskingbig.advertisement.entity.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Page<Advertisement> findByAdvertiserId(Long advertiserId, Pageable pageable);

    Page<Advertisement> findByStatus(AdStatus status, Pageable pageable);

    List<Advertisement> findByAdvertiserIdAndStatus(Long advertiserId, AdStatus status);

    @Query("SELECT a FROM Advertisement a WHERE a.status = 'ACTIVE' " +
           "AND a.startDate <= :now AND a.endDate >= :now " +
           "AND a.totalSpent < a.budget")
    List<Advertisement> findActiveAds(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Advertisement a WHERE a.status = 'ACTIVE' " +
           "AND a.startDate <= :now AND a.endDate >= :now " +
           "AND a.totalSpent < a.budget " +
           "AND a.type = :type")
    List<Advertisement> findActiveAdsByType(@Param("now") LocalDateTime now, @Param("type") AdType type);

    @Query("SELECT a FROM Advertisement a WHERE a.status = 'CONFIRMED' " +
           "AND a.endDate < :now")
    List<Advertisement> findExpiredAds(@Param("now") LocalDateTime now);

    @Query("SELECT SUM(a.totalSpent) FROM Advertisement a WHERE a.advertiser.id = :advertiserId")
    Long getTotalSpentByAdvertiser(@Param("advertiserId") Long advertiserId);

    @Query("SELECT SUM(a.impressions) FROM Advertisement a WHERE a.advertiser.id = :advertiserId")
    Long getTotalImpressionsByAdvertiser(@Param("advertiserId") Long advertiserId);

    @Query("SELECT SUM(a.clicks) FROM Advertisement a WHERE a.advertiser.id = :advertiserId")
    Long getTotalClicksByAdvertiser(@Param("advertiserId") Long advertiserId);
}
