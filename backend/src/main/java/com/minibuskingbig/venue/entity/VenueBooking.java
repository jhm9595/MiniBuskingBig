package com.minibuskingbig.venue.entity;

import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venue_bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VenueBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 예약자 (가수)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;  // 연결된 이벤트 (선택)

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer durationHours;  // 예약 시간 (시간 단위)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;  // 총 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String purpose;  // 예약 목적

    @Column(columnDefinition = "TEXT")
    private String specialRequests;  // 특별 요청사항

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;  // 취소 사유

    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static VenueBooking create(Venue venue, User user,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       Integer durationHours, BigDecimal totalAmount,
                                       String purpose, String specialRequests) {
        VenueBooking booking = new VenueBooking();
        booking.venue = venue;
        booking.user = user;
        booking.startTime = startTime;
        booking.endTime = endTime;
        booking.durationHours = durationHours;
        booking.totalAmount = totalAmount;
        booking.purpose = purpose;
        booking.specialRequests = specialRequests;
        return booking;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
    }

    public void linkEvent(Event event) {
        this.event = event;
    }
}
