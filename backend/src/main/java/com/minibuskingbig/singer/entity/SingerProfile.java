package com.minibuskingbig.singer.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "singer_profiles", indexes = {
    @Index(name = "idx_stage_name", columnList = "stage_name"),
    @Index(name = "idx_verification", columnList = "is_verified")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SingerProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "singer_id")
    private Long singerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 가수 정보
    @Column(name = "stage_name", nullable = false, length = 100)
    private String stageName; // 예명

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio; // 소개

    @Column(name = "profile_banner_url", columnDefinition = "TEXT")
    private String profileBannerUrl;

    // 장르 (JSON array)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "genres", columnDefinition = "json")
    private List<String> genres; // ['K-POP', 'BALLAD', 'ROCK', ...]

    // SNS 링크 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_links", columnDefinition = "json")
    private Map<String, String> socialLinks; // {youtube, instagram, tiktok, ...}

    // 인증 정보
    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verification_document_url", columnDefinition = "TEXT")
    private String verificationDocumentUrl;

    // 통계
    @Builder.Default
    @Column(name = "follower_count")
    private Integer followerCount = 0;

    @Builder.Default
    @Column(name = "total_event_count")
    private Integer totalEventCount = 0;

    // 비즈니스 로직
    public void updateProfile(String stageName, String bio, List<String> genres) {
        this.stageName = stageName;
        this.bio = bio;
        this.genres = genres;
    }

    public void updateSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void verify(String documentUrl) {
        this.isVerified = true;
        this.verificationDocumentUrl = documentUrl;
    }

    public void incrementFollowerCount() {
        this.followerCount++;
    }

    public void decrementFollowerCount() {
        if (this.followerCount > 0) {
            this.followerCount--;
        }
    }

    public void incrementEventCount() {
        this.totalEventCount++;
    }

    public void updateBannerUrl(String bannerUrl) {
        this.profileBannerUrl = bannerUrl;
    }
}
