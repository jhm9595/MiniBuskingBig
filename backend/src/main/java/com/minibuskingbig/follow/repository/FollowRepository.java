package com.minibuskingbig.follow.repository;

import com.minibuskingbig.follow.entity.Follow;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserAndSinger(User user, SingerProfile singer);

    List<Follow> findByUserOrderByCreatedAtDesc(User user);

    List<Follow> findBySingerOrderByCreatedAtDesc(SingerProfile singer);

    boolean existsByUserAndSinger(User user, SingerProfile singer);

    void deleteByUserAndSinger(User user, SingerProfile singer);

    long countBySinger(SingerProfile singer);
}
