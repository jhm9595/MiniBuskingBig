# 버스킹 플랫폼 데이터베이스 스키마 설계

## 설계 원칙

1. **운영비 최소화**: 핵심 필드만 정의, 확장은 JSON 컬럼 활용
2. **유연한 확장성**: 새로운 기능 추가 시 스키마 변경 최소화
3. **성능 최적화**: 적절한 인덱스와 정규화
4. **타입 안정성**: Enum 활용으로 데이터 일관성 보장

## ERD 개요

```
User (사용자)
├── SingerProfile (가수 프로필)
├── Team (팀)
│   └── TeamMember (팀 멤버)
├── Subscription (구독)
├── Favorite (즐겨찾기)
└── Payment (결제)

Event (공연)
├── Song (곡)
│   └── SongCustomField (곡 커스텀 필드)
├── EventRequest (축가/공연 요청)
└── ChatRoom (채팅방)
    └── ChatMessage (채팅 메시지)

Venue (장소)
├── VenueOption (장소 옵션)
└── VenueBooking (예약)

AdCampaign (광고 캠페인)
└── AdSlot (광고 슬롯)

StreamingSession (스트리밍 - 준비중)
```

## 1. User (사용자)

```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 소셜 로그인 정보
    social_provider VARCHAR(20) NOT NULL, -- 'GOOGLE', 'KAKAO', 'NAVER'
    social_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,

    -- 사용자 정보
    display_id VARCHAR(50) NOT NULL, -- @ID (중복 허용)
    nickname VARCHAR(100),
    profile_image_url TEXT,

    -- 권한 (비트 플래그 또는 JSON)
    roles JSON, -- ['AUDIENCE', 'SINGER', 'BUSINESS', 'ADMIN']

    -- 관객 정보
    audience_tier VARCHAR(20) DEFAULT 'GENERAL', -- 'GENERAL', 'VIP'
    avatar_config JSON, -- {head, hat, skin, top, bottom}

    -- 광고 제거 (영구 구매)
    ad_free BOOLEAN DEFAULT FALSE,
    ad_free_purchased_at DATETIME,

    -- 알림 설정
    notification_settings JSON, -- {webpush: true, email: false, region: "서울"}

    -- 상태
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,

    -- 제재
    is_banned BOOLEAN DEFAULT FALSE,
    ban_reason TEXT,
    banned_until DATETIME,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME,

    -- 인덱스
    UNIQUE KEY uk_social (social_provider, social_id),
    UNIQUE KEY uk_email (email),
    INDEX idx_display_id (display_id),
    INDEX idx_created_at (created_at)
);
```

## 2. SingerProfile (가수 프로필)

```sql
CREATE TABLE singer_profiles (
    singer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,

    -- 가수 정보
    stage_name VARCHAR(100) NOT NULL,
    bio TEXT,
    genre VARCHAR(50),

    -- SNS 링크
    youtube_url TEXT,
    instagram_url TEXT,
    other_sns JSON, -- [{platform: "TikTok", url: "..."}]

    -- VIP 전용 정보
    vip_exclusive_info TEXT,

    -- 통계
    total_events INT DEFAULT 0,
    follower_count INT DEFAULT 0,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_stage_name (stage_name)
);
```

## 3. Team (팀)

```sql
CREATE TABLE teams (
    team_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 팀 정보
    team_name VARCHAR(100) NOT NULL,
    leader_user_id BIGINT NOT NULL,
    team_email VARCHAR(255),
    bio TEXT,

    -- 팀 이미지
    logo_url TEXT,

    -- 공연 화면 커스터마이징 (팀별 저장)
    dashboard_layout JSON, -- {layout: "split", positions: {...}}

    -- 상태
    is_active BOOLEAN DEFAULT TRUE,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (leader_user_id) REFERENCES users(user_id),
    INDEX idx_team_name (team_name)
);

CREATE TABLE team_members (
    team_member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    -- 역할
    role VARCHAR(50), -- 'LEADER', 'MEMBER'

    -- 타임스탬프
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_team_user (team_id, user_id)
);
```

## 4. Event (공연)

```sql
CREATE TABLE events (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 공연 주최자
    singer_id BIGINT,
    team_id BIGINT,

    -- 공연 정보
    title VARCHAR(255) NOT NULL,
    description TEXT,

    -- 일시
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,

    -- 장소
    venue_id BIGINT, -- NULL이면 자유 입력
    venue_address TEXT,
    venue_lat DECIMAL(10, 8),
    venue_lng DECIMAL(11, 8),

    -- 채팅 설정
    chat_enabled BOOLEAN DEFAULT FALSE,
    chat_max_participants INT DEFAULT 0,
    chat_payment_status VARCHAR(20), -- 'UNPAID', 'PAID', 'REFUNDED'
    chat_room_id BIGINT,

    -- 상태
    status VARCHAR(20) DEFAULT 'SCHEDULED', -- 'SCHEDULED', 'LIVE', 'ENDED', 'CANCELLED'

    -- 통계
    view_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (singer_id) REFERENCES singer_profiles(singer_id) ON DELETE SET NULL,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE SET NULL,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE SET NULL,

    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    INDEX idx_location (venue_lat, venue_lng)
);
```

## 5. Song (곡)

```sql
CREATE TABLE songs (
    song_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,

    -- 곡 정보
    song_name VARCHAR(255) NOT NULL,
    artist_name VARCHAR(255),
    youtube_url TEXT,

    -- 순서
    song_order INT NOT NULL,

    -- 상태
    is_played BOOLEAN DEFAULT FALSE,
    played_at DATETIME,

    -- 커스텀 필드 (JSON)
    custom_fields JSON, -- {field1: "value1", field2: true, ...}

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    INDEX idx_event_order (event_id, song_order)
);

CREATE TABLE song_custom_fields (
    field_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    singer_id BIGINT NOT NULL,

    -- 필드 정의
    field_name VARCHAR(100) NOT NULL,
    field_type VARCHAR(20) NOT NULL, -- 'TEXT', 'SELECT', 'BOOLEAN'

    -- SELECT 옵션 (배열)
    select_options JSON, -- ["옵션1", "옵션2", ...]

    -- 순서
    field_order INT,

    FOREIGN KEY (singer_id) REFERENCES singer_profiles(singer_id) ON DELETE CASCADE,
    INDEX idx_singer (singer_id)
);
```

## 6. EventRequest (축가/공연 요청)

```sql
CREATE TABLE event_requests (
    request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,

    -- 요청자 정보
    user_id BIGINT, -- NULL이면 비로그인
    guest_name VARCHAR(100),
    contact VARCHAR(255) NOT NULL, -- 연락처 필수

    -- 요청 내용
    request_type VARCHAR(20) NOT NULL, -- 'SONG_REQUEST', 'PERFORMANCE'
    song_name VARCHAR(255),
    message TEXT,

    -- 상태
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED'

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_event_status (event_id, status)
);
```

## 7. ChatRoom (채팅방)

```sql
CREATE TABLE chat_rooms (
    chat_room_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 연결 (공연 또는 장소)
    event_id BIGINT,
    venue_id BIGINT, -- 장소 문의 (준비중)

    -- 채팅방 타입
    room_type VARCHAR(20) NOT NULL, -- 'EVENT', 'VENUE'

    -- Docker 컨테이너 정보
    container_id VARCHAR(255),
    container_status VARCHAR(20), -- 'PENDING', 'RUNNING', 'STOPPED'
    websocket_url TEXT,

    -- 설정
    max_participants INT NOT NULL,
    current_participants INT DEFAULT 0,

    -- 대기 예약
    waiting_count INT DEFAULT 0,

    -- 상태
    is_active BOOLEAN DEFAULT TRUE,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    ended_at DATETIME,

    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    INDEX idx_event (event_id),
    INDEX idx_container_status (container_status)
);

CREATE TABLE chat_messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,

    -- 발신자
    user_id BIGINT,
    guest_session_id VARCHAR(255), -- 비로그인 게스트

    -- 발신자 타입 (강조 표시용)
    sender_type VARCHAR(20) DEFAULT 'AUDIENCE', -- 'SINGER', 'TEAM_MEMBER', 'AUDIENCE'

    -- 메시지 내용
    message_type VARCHAR(20) DEFAULT 'TEXT', -- 'TEXT', 'SYSTEM', 'AD'
    content TEXT NOT NULL,

    -- 제재
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_reason TEXT,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_chat_room_time (chat_room_id, created_at)
);

CREATE TABLE chat_kicks (
    kick_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT,
    guest_session_id VARCHAR(255),

    -- 제재 정보
    kicked_by_user_id BIGINT NOT NULL,
    reason TEXT,

    -- 타임스탬프
    kicked_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (kicked_by_user_id) REFERENCES users(user_id),
    INDEX idx_chat_room (chat_room_id)
);
```

## 8. Venue (장소)

```sql
CREATE TABLE venues (
    venue_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_user_id BIGINT NOT NULL,

    -- 장소 정보
    venue_name VARCHAR(255) NOT NULL,
    description TEXT,
    address TEXT NOT NULL,
    lat DECIMAL(10, 8),
    lng DECIMAL(11, 8),

    -- 이미지
    images JSON, -- ["url1", "url2", ...]

    -- 운영 시간 (30분 단위)
    available_time_slots JSON, -- [{day: "MON", start: "09:00", end: "22:00"}]

    -- 기본 장비
    has_drum BOOLEAN DEFAULT FALSE,
    has_keyboard BOOLEAN DEFAULT FALSE,
    has_mic BOOLEAN DEFAULT FALSE,
    has_speaker BOOLEAN DEFAULT FALSE,

    -- 추가 옵션
    additional_options JSON, -- [{name: "조명", price: 10000}, ...]

    -- 가격
    base_price_per_30min INT,

    -- 상태
    is_active BOOLEAN DEFAULT TRUE,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (owner_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_location (lat, lng),
    INDEX idx_owner (owner_user_id)
);

CREATE TABLE venue_bookings (
    booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venue_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    -- 예약 시간
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    -- 옵션
    selected_options JSON, -- ["조명", "스탠드마이크"]
    total_price INT NOT NULL,

    -- 상태
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_venue_date (venue_id, booking_date),
    INDEX idx_user (user_id)
);
```

## 9. AdCampaign (광고)

```sql
CREATE TABLE ad_campaigns (
    campaign_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    advertiser_user_id BIGINT NOT NULL,

    -- 광고 정보
    campaign_name VARCHAR(255) NOT NULL,
    ad_type VARCHAR(20) NOT NULL, -- 'CHAT_TEXT', 'BANNER', 'VIDEO'

    -- 광고 콘텐츠
    content TEXT, -- 텍스트 광고
    image_url TEXT, -- 배너
    video_url TEXT, -- 동영상
    click_url TEXT, -- 클릭 시 이동 URL

    -- 노출 설정
    target_audience JSON, -- {tier: ["GENERAL"], region: ["서울"]}
    daily_budget INT,
    total_budget INT,

    -- 통계
    impression_count INT DEFAULT 0,
    click_count INT DEFAULT 0,
    spent_amount INT DEFAULT 0,

    -- 상태
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'RUNNING', 'PAUSED', 'COMPLETED', 'REJECTED'
    admin_note TEXT,

    -- 기간
    start_date DATE,
    end_date DATE,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (advertiser_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_ad_type (ad_type)
);

CREATE TABLE ad_slots (
    slot_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 광고 위치
    slot_type VARCHAR(20) NOT NULL, -- 'CHAT', 'BANNER', 'VIDEO', 'STREAMING_MIDROLL'
    position VARCHAR(50), -- 'TOP', 'BOTTOM', 'SIDEBAR', etc.

    -- 스트리밍 광고 전용 (준비중)
    time_offset_sec INT, -- 영상 재생 N초 시점

    -- 할당된 캠페인
    campaign_id BIGINT,

    -- 노출 제어
    max_daily_impressions INT,
    current_daily_impressions INT DEFAULT 0,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_impression_at DATETIME,

    FOREIGN KEY (campaign_id) REFERENCES ad_campaigns(campaign_id) ON DELETE SET NULL,
    INDEX idx_slot_type (slot_type),
    INDEX idx_campaign (campaign_id)
);
```

## 10. Subscription (구독)

```sql
CREATE TABLE subscriptions (
    subscription_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,

    -- 구독 정보
    tier VARCHAR(20) NOT NULL, -- 'VIP'

    -- 기간
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    -- 결제
    payment_id BIGINT,

    -- 자동 갱신
    auto_renew BOOLEAN DEFAULT TRUE,

    -- 상태
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 'ACTIVE', 'CANCELLED', 'EXPIRED'

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_end_date (end_date)
);
```

## 11. Payment (결제)

```sql
CREATE TABLE payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,

    -- 결제 타입
    payment_type VARCHAR(20) NOT NULL, -- 'CHAT', 'SUBSCRIPTION', 'AD_FREE', 'VENUE'

    -- 관련 엔티티
    event_id BIGINT, -- 채팅 결제
    subscription_id BIGINT, -- 구독 결제
    booking_id BIGINT, -- 장소 예약

    -- 금액
    amount INT NOT NULL,
    currency VARCHAR(10) DEFAULT 'KRW',

    -- 결제 수단
    payment_method VARCHAR(20), -- 'KAKAO', 'NAVER', 'APPLE', 'CARD'

    -- PG사 정보
    pg_provider VARCHAR(50),
    pg_transaction_id VARCHAR(255),

    -- 상태
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE SET NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE SET NULL,
    FOREIGN KEY (booking_id) REFERENCES venue_bookings(booking_id) ON DELETE SET NULL,

    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

## 12. Favorite (즐겨찾기)

```sql
CREATE TABLE favorites (
    favorite_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    singer_id BIGINT NOT NULL,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (singer_id) REFERENCES singer_profiles(singer_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_singer (user_id, singer_id),
    INDEX idx_singer (singer_id)
);
```

## 13. StreamingSession (준비중)

```sql
CREATE TABLE streaming_sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,

    -- 스트리밍 설정
    streaming_type VARCHAR(20), -- 'YOUTUBE_EMBED', 'WEBRTC', 'HLS'
    external_url TEXT, -- YouTube Live URL 등

    -- 자체 스트리밍 (준비중)
    stream_key VARCHAR(255),
    rtmp_url TEXT,
    hls_url TEXT,

    -- VIP 전용
    vip_only BOOLEAN DEFAULT FALSE,

    -- 광고 슬롯 (준비중)
    ad_slots_config JSON, -- [{position: "pre-roll", time: 0}, {position: "mid-roll", time: 300}]

    -- 상태
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'LIVE', 'ENDED'

    -- 통계
    viewer_count INT DEFAULT 0,
    peak_viewer_count INT DEFAULT 0,

    -- 타임스탬프
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    ended_at DATETIME,

    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    INDEX idx_event (event_id),
    INDEX idx_status (status)
);
```

## 인덱스 전략

### 조회 성능 최적화
- `events`: start_time, status, location (공연 목록/지도)
- `chat_messages`: chat_room_id + created_at (채팅 히스토리)
- `users`: display_id, created_at (사용자 검색)
- `singer_profiles`: stage_name (가수 검색)

### 외래키 인덱스
- 모든 FK에 인덱스 자동 생성 (JOIN 성능)

### 복합 인덱스
- `event_id + song_order` (곡 순서 조회)
- `venue_id + booking_date` (예약 현황)
- `user_id + status` (구독 상태 조회)

## JSON 필드 활용

### 확장성을 위한 JSON
- `custom_fields`: 가수별 커스텀 곡 필드
- `avatar_config`: 관객 아바타 설정
- `notification_settings`: 알림 설정
- `dashboard_layout`: 팀별 화면 커스터마이징
- `additional_options`: 장소 추가 옵션

### JSON 쿼리 (MySQL 8.0+)
```sql
-- 커스텀 필드 검색
SELECT * FROM songs WHERE JSON_EXTRACT(custom_fields, '$.mood') = 'happy';

-- VIP 등급 필터
SELECT * FROM users WHERE audience_tier = 'VIP';

-- 알림 설정 확인
SELECT * FROM users WHERE JSON_EXTRACT(notification_settings, '$.webpush') = true;
```

## 마이그레이션 전략

1. **초기 스키마**: 모든 테이블 생성
2. **데이터 시딩**: 관리자 계정, 기본 광고 슬롯
3. **인덱스 최적화**: 실제 데이터 기반 조정
4. **JSON 필드 확장**: 새 기능 추가 시 스키마 변경 없이 JSON 필드 활용

## 다음 단계

- [ ] JPA Entity 클래스 작성
- [ ] Repository 인터페이스 정의
- [ ] 데이터 시딩 스크립트
- [ ] 마이그레이션 파일 (Flyway)
