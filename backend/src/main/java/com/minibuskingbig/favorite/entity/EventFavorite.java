package com.minibuskingbig.favorite.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_favorites", indexes = {
    @Index(name = "idx_user_event", columnList = "user_id,event_id", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventFavorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
