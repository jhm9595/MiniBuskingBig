package com.minibuskingbig.follow.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows", indexes = {
    @Index(name = "idx_user_singer", columnList = "user_id,singer_id", unique = true),
    @Index(name = "idx_singer", columnList = "singer_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Follow extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singer_id", nullable = false)
    private SingerProfile singer;
}
