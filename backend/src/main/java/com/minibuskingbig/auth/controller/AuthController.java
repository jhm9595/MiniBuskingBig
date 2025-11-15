package com.minibuskingbig.auth.controller;

import com.minibuskingbig.auth.dto.TokenResponse;
import com.minibuskingbig.auth.util.JwtTokenProvider;
import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Value("${jwt.access-token-validity}")
    private Long accessTokenValidity;

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            User user = userService.getUserById(userId);

            String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getRoles()
            );
            String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

            TokenResponse tokenResponse = TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                accessTokenValidity
            );

            return ApiResponse.success(tokenResponse);
        }

        return ApiResponse.fail("Invalid refresh token");
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Long userId) {
        User user = userService.getUserById(userId);
        UserInfoResponse userInfo = UserInfoResponse.from(user);
        return ApiResponse.success(userInfo);
    }

    record RefreshTokenRequest(String refreshToken) {}

    record UserInfoResponse(
        Long userId,
        String email,
        String displayId,
        String nickname,
        String profileImageUrl
    ) {
        static UserInfoResponse from(User user) {
            return new UserInfoResponse(
                user.getUserId(),
                user.getEmail(),
                user.getDisplayId(),
                user.getNickname(),
                user.getProfileImageUrl()
            );
        }
    }
}
