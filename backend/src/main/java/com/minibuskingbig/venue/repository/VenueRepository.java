package com.minibuskingbig.venue.repository;

import com.minibuskingbig.venue.entity.Venue;
import com.minibuskingbig.venue.entity.VenueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Page<Venue> findByStatus(VenueStatus status, Pageable pageable);

    Page<Venue> findByProviderId(Long providerId, Pageable pageable);

    List<Venue> findByProviderIdAndStatus(Long providerId, VenueStatus status);

    @Query("SELECT v FROM Venue v WHERE v.status = 'ACTIVE' " +
           "AND v.latitude BETWEEN :minLat AND :maxLat " +
           "AND v.longitude BETWEEN :minLon AND :maxLon")
    List<Venue> findNearbyVenues(@Param("minLat") BigDecimal minLat,
                                  @Param("maxLat") BigDecimal maxLat,
                                  @Param("minLon") BigDecimal minLon,
                                  @Param("maxLon") BigDecimal maxLon);

    @Query("SELECT v FROM Venue v WHERE v.status = 'ACTIVE' " +
           "AND (v.name LIKE %:keyword% OR v.address LIKE %:keyword%)")
    Page<Venue> searchActiveVenues(@Param("keyword") String keyword, Pageable pageable);

    long countByProviderIdAndStatus(Long providerId, VenueStatus status);
}
