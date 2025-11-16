package com.minibuskingbig.venue.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.repository.EventRepository;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.repository.UserRepository;
import com.minibuskingbig.venue.entity.BookingStatus;
import com.minibuskingbig.venue.entity.Venue;
import com.minibuskingbig.venue.entity.VenueBooking;
import com.minibuskingbig.venue.repository.VenueBookingRepository;
import com.minibuskingbig.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueBookingService {

    private final VenueBookingRepository bookingRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * 장소 예약
     */
    @Transactional
    public VenueBooking createBooking(Long venueId, Long userId,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       String purpose, String specialRequests) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 시간 중복 체크
        List<VenueBooking> conflicts = bookingRepository.findConflictingBookings(
                venueId, startTime, endTime);

        if (!conflicts.isEmpty()) {
            throw new BusinessException(ErrorCode.BOOKING_CONFLICT);
        }

        // 예약 시간 계산 (시간 단위로 반올림)
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        int durationHours = (int) Math.ceil(minutes / 60.0);

        // 총 금액 계산
        BigDecimal totalAmount = venue.getHourlyRate().multiply(BigDecimal.valueOf(durationHours));

        VenueBooking booking = VenueBooking.create(
                venue, user, startTime, endTime,
                durationHours, totalAmount, purpose, specialRequests);

        return bookingRepository.save(booking);
    }

    /**
     * 예약 확정
     */
    @Transactional
    public VenueBooking confirmBooking(Long bookingId, Long providerId) {
        VenueBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!booking.getVenue().getProvider().getId().equals(providerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        booking.confirm();
        return booking;
    }

    /**
     * 예약 취소
     */
    @Transactional
    public VenueBooking cancelBooking(Long bookingId, Long userId, String reason) {
        VenueBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 예약자 또는 장소 제공자만 취소 가능
        if (!booking.getUser().getId().equals(userId) &&
            !booking.getVenue().getProvider().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        booking.cancel(reason);
        return booking;
    }

    /**
     * 예약 완료 처리
     */
    @Transactional
    public VenueBooking completeBooking(Long bookingId) {
        VenueBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.complete();
        return booking;
    }

    /**
     * 예약 조회
     */
    public VenueBooking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    /**
     * 사용자별 예약 목록
     */
    public Page<VenueBooking> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable);
    }

    /**
     * 장소별 예약 목록
     */
    public Page<VenueBooking> getBookingsByVenue(Long venueId, Pageable pageable) {
        return bookingRepository.findByVenueId(venueId, pageable);
    }

    /**
     * 제공자별 예약 목록
     */
    public Page<VenueBooking> getBookingsByProvider(Long providerId, Pageable pageable) {
        return bookingRepository.findByProviderId(providerId, pageable);
    }

    /**
     * 이벤트 연결
     */
    @Transactional
    public VenueBooking linkToEvent(Long bookingId, Long eventId, Long userId) {
        VenueBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.linkEvent(event);
        return booking;
    }

    /**
     * 완료된 예약 일괄 처리 (스케줄러용)
     */
    @Transactional
    public void processCompletedBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<VenueBooking> completedBookings = bookingRepository.findCompletedBookings(now);

        completedBookings.forEach(VenueBooking::complete);
    }

    /**
     * 예약 가능 여부 확인
     */
    public boolean isAvailable(Long venueId, LocalDateTime startTime, LocalDateTime endTime) {
        List<VenueBooking> conflicts = bookingRepository.findConflictingBookings(
                venueId, startTime, endTime);
        return conflicts.isEmpty();
    }

    /**
     * 예약 가능 시간대 조회
     */
    public List<VenueBooking> getBookedTimeSlots(Long venueId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return bookingRepository.findConflictingBookings(venueId, startOfDay, endOfDay);
    }
}
