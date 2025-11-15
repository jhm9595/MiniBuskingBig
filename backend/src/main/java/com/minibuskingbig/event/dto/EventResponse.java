package com.minibuskingbig.event.dto;

import com.minibuskingbig.event.entity.ChatPaymentStatus;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String venueAddress;
    private BigDecimal venueLat;
    private BigDecimal venueLng;
    private Boolean chatEnabled;
    private Integer chatMaxParticipants;
    private ChatPaymentStatus chatPaymentStatus;
    private EventStatus status;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Singer/Team info
    private Long singerId;
    private String singerStageName;
    private Long teamId;
    private String teamName;

    public static EventResponse from(Event event) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .title(event.getTitle())
            .description(event.getDescription())
            .startTime(event.getStartTime())
            .endTime(event.getEndTime())
            .venueAddress(event.getVenueAddress())
            .venueLat(event.getVenueLat())
            .venueLng(event.getVenueLng())
            .chatEnabled(event.getChatEnabled())
            .chatMaxParticipants(event.getChatMaxParticipants())
            .chatPaymentStatus(event.getChatPaymentStatus())
            .status(event.getStatus())
            .viewCount(event.getViewCount())
            .favoriteCount(event.getFavoriteCount())
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .singerId(event.getSinger() != null ? event.getSinger().getSingerId() : null)
            .singerStageName(event.getSinger() != null ? event.getSinger().getStageName() : null)
            .teamId(event.getTeam() != null ? event.getTeam().getTeamId() : null)
            .teamName(event.getTeam() != null ? event.getTeam().getTeamName() : null)
            .build();
    }
}
