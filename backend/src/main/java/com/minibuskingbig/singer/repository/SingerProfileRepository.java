package com.minibuskingbig.singer.repository;

import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SingerProfileRepository extends JpaRepository<SingerProfile, Long> {

    Optional<SingerProfile> findByUser(User user);

    Optional<SingerProfile> findByUser_UserId(Long userId);

    boolean existsByUser(User user);
}
