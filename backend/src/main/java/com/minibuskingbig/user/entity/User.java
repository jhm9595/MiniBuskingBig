package com.minibuskingbig.user.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_social", columnList = "social_provider,social_id", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_display_id", columnList = "display_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 소셜 로그인 정보
    @Column(name = "social_provider", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Column(name = "email", nullable = false)
    private String email;

    // 사용자 정보
    @Column(name = "display_id", nullable = false, length = 50)
    private String displayId; // @ID (중복 허용)

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    // 권한 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "roles", columnDefinition = "json")
    private List<String> roles; // ['AUDIENCE', 'SINGER', 'BUSINESS', 'ADMIN']

    // 관객 정보
    @Column(name = "audience_tier", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AudienceTier audienceTier = AudienceTier.GENERAL;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "avatar_config", columnDefinition = "json")
    private Map<String, String> avatarConfig; // {head, hat, skin, top, bottom}

    // 광고 제거
    @Builder.Default
    @Column(name = "ad_free", nullable = false)
    private Boolean adFree = false;

    @Column(name = "ad_free_purchased_at")
    private LocalDateTime adFreePurchasedAt;

    // 알림 설정 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_settings", columnDefinition = "json")
    private Map<String, Object> notificationSettings;

    // 상태
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    // 제재
    @Builder.Default
    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;

    @Column(name = "ban_reason", columnDefinition = "TEXT")
    private String banReason;

    @Column(name = "banned_until")
    private LocalDateTime bannedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 비즈니스 메서드
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void purchaseAdFree() {
        this.adFree = true;
        this.adFreePurchasedAt = LocalDateTime.now();
    }

    public void upgradeToVip() {
        this.audienceTier = AudienceTier.VIP;
    }

    public void downgradeToGeneral() {
        this.audienceTier = AudienceTier.GENERAL;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public void addRole(String role) {
        if (roles != null && !roles.contains(role)) {
            roles.add(role);
        }
    }
}
