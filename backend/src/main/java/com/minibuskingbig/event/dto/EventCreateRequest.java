package com.minibuskingbig.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank(message = "공연 제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "시작 시간은 필수입니다.")
    @Future(message = "시작 시간은 미래여야 합니다.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    @Future(message = "종료 시간은 미래여야 합니다.")
    private LocalDateTime endTime;

    private String venueAddress;
    private BigDecimal venueLat;
    private BigDecimal venueLng;

    private Boolean chatEnabled;
    private Integer chatMaxParticipants;

    private Long teamId; // Optional: 팀으로 공연하는 경우
}
