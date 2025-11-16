package com.minibuskingbig.advertisement.entity;

import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private User advertiser;  // 광고주

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdType type;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String videoUrl;

    @Column(length = 500)
    private String targetUrl;  // 클릭 시 이동할 URL

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;  // 광고 예산

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerClick = BigDecimal.ZERO;  // 클릭당 비용 (CPC)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerImpression = BigDecimal.ZERO;  // 노출당 비용 (CPM/1000)

    // 성과 지표
    @Column(nullable = false)
    private Long impressions = 0L;  // 노출 수

    @Column(nullable = false)
    private Long clicks = 0L;  // 클릭 수

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;  // 총 사용 금액

    // 타겟팅 정보 (JSON)
    @Column(columnDefinition = "TEXT")
    private String targetingConfig;  // 지역, 연령, 성별 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdStatus status = AdStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Advertisement create(User advertiser, String title, String description,
                                        AdType type, String imageUrl, String videoUrl, String targetUrl,
                                        LocalDateTime startDate, LocalDateTime endDate,
                                        BigDecimal budget, BigDecimal costPerClick, BigDecimal costPerImpression) {
        Advertisement ad = new Advertisement();
        ad.advertiser = advertiser;
        ad.title = title;
        ad.description = description;
        ad.type = type;
        ad.imageUrl = imageUrl;
        ad.videoUrl = videoUrl;
        ad.targetUrl = targetUrl;
        ad.startDate = startDate;
        ad.endDate = endDate;
        ad.budget = budget;
        ad.costPerClick = costPerClick;
        ad.costPerImpression = costPerImpression;
        return ad;
    }

    public void updateInfo(String title, String description,
                           String imageUrl, String videoUrl, String targetUrl,
                           LocalDateTime startDate, LocalDateTime endDate,
                           BigDecimal budget, BigDecimal costPerClick, BigDecimal costPerImpression) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.targetUrl = targetUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.costPerClick = costPerClick;
        this.costPerImpression = costPerImpression;
    }

    public void approve() {
        this.status = AdStatus.ACTIVE;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = AdStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void pause() {
        this.status = AdStatus.PAUSED;
    }

    public void resume() {
        this.status = AdStatus.ACTIVE;
    }

    public void expire() {
        this.status = AdStatus.EXPIRED;
    }

    public void recordImpression() {
        this.impressions++;
        BigDecimal cost = this.costPerImpression;
        this.totalSpent = this.totalSpent.add(cost);

        // 예산 초과 시 일시 정지
        if (this.totalSpent.compareTo(this.budget) >= 0) {
            this.pause();
        }
    }

    public void recordClick() {
        this.clicks++;
        BigDecimal cost = this.costPerClick;
        this.totalSpent = this.totalSpent.add(cost);

        // 예산 초과 시 일시 정지
        if (this.totalSpent.compareTo(this.budget) >= 0) {
            this.pause();
        }
    }

    public void setTargetingConfig(String targetingConfig) {
        this.targetingConfig = targetingConfig;
    }

    public double getClickThroughRate() {
        if (impressions == 0) return 0.0;
        return (double) clicks / impressions * 100;
    }
}
