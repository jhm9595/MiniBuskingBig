package com.minibuskingbig.singer.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
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
public class SingerProfileService {

    private final SingerProfileRepository singerProfileRepository;
    private final UserService userService;

    @Transactional
    public SingerProfile createProfile(Long userId, String stageName, String bio, List<String> genres) {
        User user = userService.getUserById(userId);

        if (singerProfileRepository.existsByUser(user)) {
            throw new BusinessException(ErrorCode.SINGER_PROFILE_ALREADY_EXISTS);
        }

        SingerProfile profile = SingerProfile.builder()
            .user(user)
            .stageName(stageName)
            .bio(bio)
            .genres(genres)
            .build();

        SingerProfile savedProfile = singerProfileRepository.save(profile);
        userService.addRole(userId, "SINGER");

        log.info("Singer profile created: {} for user: {}", savedProfile.getSingerId(), userId);
        return savedProfile;
    }

    public SingerProfile getProfileById(Long singerId) {
        return singerProfileRepository.findById(singerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));
    }

    public SingerProfile getProfileByUserId(Long userId) {
        return singerProfileRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SINGER_PROFILE_NOT_FOUND));
    }

    @Transactional
    public SingerProfile updateProfile(Long userId, String stageName, String bio, List<String> genres) {
        SingerProfile profile = getProfileByUserId(userId);
        profile.updateProfile(stageName, bio, genres);
        return profile;
    }
}
