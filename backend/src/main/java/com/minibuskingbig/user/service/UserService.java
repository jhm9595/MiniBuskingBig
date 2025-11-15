package com.minibuskingbig.user.service;

import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.user.entity.SocialProvider;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User createOrUpdateUser(SocialProvider provider, String socialId, String email, String nickname, String profileImageUrl) {
        return userRepository.findBySocialProviderAndSocialId(provider, socialId)
            .map(existingUser -> {
                existingUser.updateLastLogin();
                return existingUser;
            })
            .orElseGet(() -> {
                User newUser = User.builder()
                    .socialProvider(provider)
                    .socialId(socialId)
                    .email(email)
                    .displayId(generateDisplayId(email))
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .roles(List.of("AUDIENCE"))
                    .build();
                newUser.updateLastLogin();
                return userRepository.save(newUser);
            });
    }

    private String generateDisplayId(String email) {
        String baseId = email.split("@")[0];
        String displayId = "@" + baseId;

        // If displayId already exists, append random number
        if (userRepository.existsByDisplayId(displayId)) {
            displayId = displayId + System.currentTimeMillis() % 10000;
        }

        return displayId;
    }

    @Transactional
    public void purchaseAdFree(Long userId) {
        User user = getUserById(userId);
        user.purchaseAdFree();
    }

    @Transactional
    public void upgradeToVip(Long userId) {
        User user = getUserById(userId);
        user.upgradeToVip();
    }

    @Transactional
    public void addRole(Long userId, String role) {
        User user = getUserById(userId);
        user.addRole(role);
    }
}
