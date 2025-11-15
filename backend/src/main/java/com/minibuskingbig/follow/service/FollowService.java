package com.minibuskingbig.follow.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.follow.entity.Follow;
import com.minibuskingbig.follow.repository.FollowRepository;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.singer.repository.SingerProfileRepository;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final SingerProfileRepository singerProfileRepository;

    @Transactional
    public void followSinger(Long userId, Long singerId) {
        User user = userService.getUserById(userId);
        SingerProfile singer = singerProfileRepository.findById(singerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));

        if (followRepository.existsByUserAndSinger(user, singer)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 팔로우한 가수입니다.");
        }

        Follow follow = Follow.builder()
            .user(user)
            .singer(singer)
            .build();

        followRepository.save(follow);

        // 가수 팔로워 카운트 증가
        singer.incrementFollowerCount();

        log.info("Singer followed: singer {} by user {}", singerId, userId);
    }

    @Transactional
    public void unfollowSinger(Long userId, Long singerId) {
        User user = userService.getUserById(userId);
        SingerProfile singer = singerProfileRepository.findById(singerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));

        if (!followRepository.existsByUserAndSinger(user, singer)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "팔로우를 찾을 수 없습니다.");
        }

        followRepository.deleteByUserAndSinger(user, singer);

        // 가수 팔로워 카운트 감소
        singer.decrementFollowerCount();

        log.info("Singer unfollowed: singer {} by user {}", singerId, userId);
    }

    public List<SingerProfile> getFollowingSingers(Long userId) {
        User user = userService.getUserById(userId);
        List<Follow> follows = followRepository.findByUserOrderByCreatedAtDesc(user);
        return follows.stream()
            .map(Follow::getSinger)
            .toList();
    }

    public boolean isFollowing(Long userId, Long singerId) {
        User user = userService.getUserById(userId);
        SingerProfile singer = singerProfileRepository.findById(singerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));

        return followRepository.existsByUserAndSinger(user, singer);
    }
}
