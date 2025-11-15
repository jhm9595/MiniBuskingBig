package com.minibuskingbig.song.repository;

import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByEventOrderByDisplayOrderAsc(Event event);

    List<Song> findByEventOrderByRequestCountDesc(Event event);
}
