package com.minibuskingbig.chat.entity;

public enum ChatRoomStatus {
    CREATING,   // 생성 중 (컨테이너 시작 중)
    ACTIVE,     // 활성
    CLOSING,    // 종료 중
    CLOSED      // 종료됨
}
