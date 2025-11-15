package com.minibuskingbig.team.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "teams", indexes = {
    @Index(name = "idx_team_name", columnList = "team_name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    // 팀 정보
    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "banner_url", columnDefinition = "TEXT")
    private String bannerUrl;

    // 장르 (JSON array)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "genres", columnDefinition = "json")
    private List<String> genres;

    // SNS 링크 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_links", columnDefinition = "json")
    private Map<String, String> socialLinks;

    // 통계
    @Builder.Default
    @Column(name = "member_count")
    private Integer memberCount = 0;

    @Builder.Default
    @Column(name = "total_event_count")
    private Integer totalEventCount = 0;

    // 비즈니스 로직
    public void updateTeamInfo(String teamName, String description, List<String> genres) {
        this.teamName = teamName;
        this.description = description;
        this.genres = genres;
    }

    public void updateLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void updateBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public void updateSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void incrementMemberCount() {
        this.memberCount++;
    }

    public void decrementMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        }
    }

    public void incrementEventCount() {
        this.totalEventCount++;
    }
}
