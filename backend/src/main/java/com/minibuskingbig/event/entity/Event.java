package com.minibuskingbig.event.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_start_time", columnList = "start_time"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_location", columnList = "venue_lat,venue_lng")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    // 공연 주최자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singer_id")
    private SingerProfile singer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 공연 정보
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 일시
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // 장소
    @Column(name = "venue_address", columnDefinition = "TEXT")
    private String venueAddress;

    @Column(name = "venue_lat", precision = 10, scale = 8)
    private BigDecimal venueLat;

    @Column(name = "venue_lng", precision = 11, scale = 8)
    private BigDecimal venueLng;

    // 채팅 설정
    @Builder.Default
    @Column(name = "chat_enabled", nullable = false)
    private Boolean chatEnabled = false;

    @Builder.Default
    @Column(name = "chat_max_participants")
    private Integer chatMaxParticipants = 0;

    @Column(name = "chat_payment_status", length = 20)
    @Enumerated(EnumType.STRING)
    private ChatPaymentStatus chatPaymentStatus;

    // 상태
    @Builder.Default
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.SCHEDULED;

    // 통계
    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

    // 비즈니스 로직
    public void updateEventInfo(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updateVenue(String address, BigDecimal lat, BigDecimal lng) {
        this.venueAddress = address;
        this.venueLat = lat;
        this.venueLng = lng;
    }

    public void enableChat(int maxParticipants) {
        this.chatEnabled = true;
        this.chatMaxParticipants = maxParticipants;
        this.chatPaymentStatus = ChatPaymentStatus.UNPAID;
    }

    public void completeChatPayment() {
        this.chatPaymentStatus = ChatPaymentStatus.PAID;
    }

    public void startEvent() {
        this.status = EventStatus.LIVE;
    }

    public void endEvent() {
        this.status = EventStatus.ENDED;
    }

    public void cancelEvent() {
        this.status = EventStatus.CANCELLED;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    public void decrementFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount--;
        }
    }
}
