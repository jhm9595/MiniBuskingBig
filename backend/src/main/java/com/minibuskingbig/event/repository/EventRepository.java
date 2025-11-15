package com.minibuskingbig.event.repository;

import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.entity.EventStatus;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findBySingerOrderByStartTimeDesc(SingerProfile singer);

    List<Event> findByTeamOrderByStartTimeDesc(Team team);

    List<Event> findByStatusOrderByStartTimeAsc(EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startTime BETWEEN :startTime AND :endTime ORDER BY e.startTime ASC")
    List<Event> findUpcomingEvents(@Param("status") EventStatus status,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    @Query("SELECT e FROM Event e WHERE e.status = 'LIVE' ORDER BY e.startTime ASC")
    List<Event> findLiveEvents();
}
