package com.minibuskingbig.follow.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.follow.service.FollowService;
import com.minibuskingbig.singer.dto.SingerProfileResponse;
import com.minibuskingbig.singer.entity.SingerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/singers/{singerId}")
    public ApiResponse<Void> followSinger(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long singerId
    ) {
        followService.followSinger(userId, singerId);
        return ApiResponse.success(null, "팔로우했습니다.");
    }

    @DeleteMapping("/singers/{singerId}")
    public ApiResponse<Void> unfollowSinger(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long singerId
    ) {
        followService.unfollowSinger(userId, singerId);
        return ApiResponse.success(null, "언팔로우했습니다.");
    }

    @GetMapping("/singers")
    public ApiResponse<List<SingerProfileResponse>> getFollowingSingers(
        @AuthenticationPrincipal Long userId
    ) {
        List<SingerProfile> singers = followService.getFollowingSingers(userId);
        List<SingerProfileResponse> singerResponses = singers.stream()
            .map(SingerProfileResponse::from)
            .collect(Collectors.toList());
        return ApiResponse.success(singerResponses);
    }

    @GetMapping("/singers/{singerId}/check")
    public ApiResponse<Boolean> checkFollow(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long singerId
    ) {
        boolean isFollowing = followService.isFollowing(userId, singerId);
        return ApiResponse.success(isFollowing);
    }
}
