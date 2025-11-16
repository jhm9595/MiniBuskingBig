package com.minibuskingbig.venue.repository;

import com.minibuskingbig.venue.entity.BookingStatus;
import com.minibuskingbig.venue.entity.VenueBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VenueBookingRepository extends JpaRepository<VenueBooking, Long> {

    Page<VenueBooking> findByUserId(Long userId, Pageable pageable);

    Page<VenueBooking> findByVenueId(Long venueId, Pageable pageable);

    List<VenueBooking> findByVenueIdAndStatus(Long venueId, BookingStatus status);

    @Query("SELECT b FROM VenueBooking b WHERE b.venue.id = :venueId " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
    List<VenueBooking> findConflictingBookings(@Param("venueId") Long venueId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM VenueBooking b WHERE b.venue.provider.id = :providerId " +
           "ORDER BY b.createdAt DESC")
    Page<VenueBooking> findByProviderId(@Param("providerId") Long providerId, Pageable pageable);

    List<VenueBooking> findByUserIdAndStatusOrderByStartTimeDesc(Long userId, BookingStatus status);

    @Query("SELECT b FROM VenueBooking b WHERE b.status = 'CONFIRMED' " +
           "AND b.endTime < :now")
    List<VenueBooking> findCompletedBookings(@Param("now") LocalDateTime now);
}
