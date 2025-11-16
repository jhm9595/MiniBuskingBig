package com.minibuskingbig.notification.entity;

public enum NotificationType {
    // 이벤트 관련
    EVENT_STARTED,           // 팔로우한 가수의 공연 시작
    EVENT_ENDED,             // 참여한 공연 종료
    EVENT_REMINDER,          // 즐겨찾기한 공연 시작 30분 전

    // 채팅 관련
    CHAT_MENTION,            // 채팅에서 멘션됨
    CHAT_VIP_WELCOME,        // VIP 채팅 참여 환영

    // 결제 관련
    PAYMENT_SUCCESS,         // 결제 성공
    PAYMENT_FAILED,          // 결제 실패
    SUBSCRIPTION_RENEWAL,    // 구독 갱신
    SUBSCRIPTION_CANCELLED,  // 구독 취소

    // 팔로우 관련
    NEW_FOLLOWER,            // 새로운 팔로워

    // 시스템
    SYSTEM_ANNOUNCEMENT,     // 시스템 공지
    SYSTEM_MAINTENANCE       // 시스템 점검
}
