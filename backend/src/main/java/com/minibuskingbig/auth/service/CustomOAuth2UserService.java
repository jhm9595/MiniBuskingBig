package com.minibuskingbig.auth.service;

import com.minibuskingbig.user.entity.SocialProvider;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());

        SocialProvider provider = SocialProvider.valueOf(userInfo.getProvider());
        User user = userService.createOrUpdateUser(
            provider,
            userInfo.getProviderId(),
            userInfo.getEmail(),
            userInfo.getName(),
            userInfo.getProfileImageUrl()
        );

        log.info("OAuth2 user logged in: {} ({})", user.getEmail(), provider);

        return new DefaultOAuth2User(
            Collections.emptyList(),
            oAuth2User.getAttributes(),
            "email"
        );
    }
}
