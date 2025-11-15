package com.minibuskingbig.team.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.singer.entity.SingerProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members", indexes = {
    @Index(name = "idx_team_singer", columnList = "team_id,singer_id", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singer_id", nullable = false)
    private SingerProfile singer;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private TeamRole role;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 비즈니스 로직
    public void changeRole(TeamRole newRole) {
        this.role = newRole;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
