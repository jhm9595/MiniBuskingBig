package com.minibuskingbig.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {

    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String venueAddress;
    private BigDecimal venueLat;
    private BigDecimal venueLng;
}
