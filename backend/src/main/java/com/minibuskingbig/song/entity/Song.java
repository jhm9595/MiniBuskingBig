package com.minibuskingbig.song.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.event.entity.Event;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "songs", indexes = {
    @Index(name = "idx_event", columnList = "event_id"),
    @Index(name = "idx_order", columnList = "event_id,display_order")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Song extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // 곡 정보
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "youtube_url", columnDefinition = "TEXT")
    private String youtubeUrl;

    @Column(name = "spotify_url", columnDefinition = "TEXT")
    private String spotifyUrl;

    // 곡 순서
    @Column(name = "display_order")
    private Integer displayOrder;

    // 커스텀 필드 (JSON) - 가수가 자유롭게 추가 가능
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_fields", columnDefinition = "json")
    private Map<String, Object> customFields;

    // 통계
    @Builder.Default
    @Column(name = "request_count")
    private Integer requestCount = 0;

    // 비즈니스 로직
    public void updateSongInfo(String title, String artist, Integer durationSeconds) {
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
    }

    public void updateLinks(String youtubeUrl, String spotifyUrl) {
        this.youtubeUrl = youtubeUrl;
        this.spotifyUrl = spotifyUrl;
    }

    public void updateCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    public void updateDisplayOrder(Integer order) {
        this.displayOrder = order;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }
}
