package com.minibuskingbig.venue.entity;

import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;  // 장소 제공자

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 주소 정보
    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 100)
    private String addressDetail;

    @Column(length = 20)
    private String postalCode;

    // 위치 정보 (위도, 경도)
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    // 장소 정보
    @Column(nullable = false)
    private Integer capacity;  // 최대 수용 인원

    @Column(nullable = false)
    private BigDecimal hourlyRate;  // 시간당 대여료

    @Column(columnDefinition = "TEXT")
    private String facilities;  // 시설 정보 (JSON)

    @Column(columnDefinition = "TEXT")
    private String photos;  // 사진 URL 목록 (JSON array)

    // 운영 시간
    @Column(length = 5)
    private String openTime;  // HH:mm

    @Column(length = 5)
    private String closeTime;  // HH:mm

    // 연락처
    @Column(length = 20)
    private String contactPhone;

    @Column(length = 100)
    private String contactEmail;

    // 승인 및 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VenueStatus status = VenueStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;  // 거절 사유

    private LocalDateTime approvedAt;

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

    public static Venue create(User provider, String name, String description,
                                String address, String addressDetail, String postalCode,
                                BigDecimal latitude, BigDecimal longitude,
                                Integer capacity, BigDecimal hourlyRate,
                                String openTime, String closeTime,
                                String contactPhone, String contactEmail) {
        Venue venue = new Venue();
        venue.provider = provider;
        venue.name = name;
        venue.description = description;
        venue.address = address;
        venue.addressDetail = addressDetail;
        venue.postalCode = postalCode;
        venue.latitude = latitude;
        venue.longitude = longitude;
        venue.capacity = capacity;
        venue.hourlyRate = hourlyRate;
        venue.openTime = openTime;
        venue.closeTime = closeTime;
        venue.contactPhone = contactPhone;
        venue.contactEmail = contactEmail;
        return venue;
    }

    public void updateInfo(String name, String description,
                           String address, String addressDetail, String postalCode,
                           BigDecimal latitude, BigDecimal longitude,
                           Integer capacity, BigDecimal hourlyRate,
                           String openTime, String closeTime,
                           String contactPhone, String contactEmail) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.addressDetail = addressDetail;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public void approve() {
        this.status = VenueStatus.ACTIVE;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = VenueStatus.PENDING;
        this.rejectionReason = reason;
    }

    public void activate() {
        this.status = VenueStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = VenueStatus.INACTIVE;
    }

    public void suspend(String reason) {
        this.status = VenueStatus.SUSPENDED;
        this.rejectionReason = reason;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }
}
