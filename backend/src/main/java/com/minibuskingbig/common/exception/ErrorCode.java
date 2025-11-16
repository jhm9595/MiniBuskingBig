package com.minibuskingbig.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C003", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C004", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "요청한 리소스를 찾을 수 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다."),
    USER_BANNED(HttpStatus.FORBIDDEN, "U003", "제재된 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "U004", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "U005", "만료된 토큰입니다."),

    // Singer
    SINGER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "가수 프로필을 찾을 수 없습니다."),
    SINGER_PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "S002", "이미 가수 프로필이 존재합니다."),
    SINGER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "S003", "인증되지 않은 가수입니다."),

    // Event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "공연을 찾을 수 없습니다."),
    EVENT_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "E002", "이미 시작된 공연입니다."),
    EVENT_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "E003", "이미 종료된 공연입니다."),
    EVENT_CANCELLED(HttpStatus.BAD_REQUEST, "E004", "취소된 공연입니다."),
    INVALID_EVENT_TIME(HttpStatus.BAD_REQUEST, "E005", "유효하지 않은 공연 시간입니다."),

    // Team
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "팀을 찾을 수 없습니다."),
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, "T002", "이미 존재하는 팀 이름입니다."),
    NOT_TEAM_LEADER(HttpStatus.FORBIDDEN, "T003", "팀 리더만 수행할 수 있습니다."),
    ALREADY_TEAM_MEMBER(HttpStatus.CONFLICT, "T004", "이미 팀 멤버입니다."),

    // Song
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "SG001", "곡을 찾을 수 없습니다."),

    // Chat
    CHAT_NOT_ENABLED(HttpStatus.BAD_REQUEST, "CH001", "채팅이 활성화되지 않았습니다."),
    CHAT_NOT_PAID(HttpStatus.PAYMENT_REQUIRED, "CH002", "채팅 요금이 결제되지 않았습니다."),
    CHAT_ROOM_FULL(HttpStatus.BAD_REQUEST, "CH003", "채팅방이 가득 찼습니다."),

    // Payment
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "P001", "결제에 실패했습니다."),
    REFUND_FAILED(HttpStatus.BAD_REQUEST, "P002", "환불에 실패했습니다."),

    // Venue
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "V001", "장소를 찾을 수 없습니다."),
    VENUE_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "V002", "활성화되지 않은 장소입니다."),
    BOOKING_CONFLICT(HttpStatus.CONFLICT, "V003", "예약 시간이 중복됩니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "V004", "예약을 찾을 수 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "V005", "요청한 항목을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
