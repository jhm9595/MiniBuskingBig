package com.minibuskingbig.singer.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.singer.dto.SingerProfileResponse;
import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.singer.service.SingerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/singers")
@RequiredArgsConstructor
public class SingerProfileController {

    private final SingerProfileService singerProfileService;

    @PostMapping("/profile")
    public ApiResponse<SingerProfileResponse> createProfile(
        @AuthenticationPrincipal Long userId,
        @RequestBody CreateProfileRequest request
    ) {
        SingerProfile profile = singerProfileService.createProfile(
            userId,
            request.stageName(),
            request.bio(),
            request.genres()
        );
        return ApiResponse.success(SingerProfileResponse.from(profile), "가수 프로필이 생성되었습니다.");
    }

    @GetMapping("/{singerId}")
    public ApiResponse<SingerProfileResponse> getProfile(@PathVariable Long singerId) {
        SingerProfile profile = singerProfileService.getProfileById(singerId);
        return ApiResponse.success(SingerProfileResponse.from(profile));
    }

    @GetMapping("/me")
    public ApiResponse<SingerProfileResponse> getMyProfile(@AuthenticationPrincipal Long userId) {
        SingerProfile profile = singerProfileService.getProfileByUserId(userId);
        return ApiResponse.success(SingerProfileResponse.from(profile));
    }

    @PutMapping("/profile")
    public ApiResponse<SingerProfileResponse> updateProfile(
        @AuthenticationPrincipal Long userId,
        @RequestBody UpdateProfileRequest request
    ) {
        SingerProfile profile = singerProfileService.updateProfile(
            userId,
            request.stageName(),
            request.bio(),
            request.genres()
        );
        return ApiResponse.success(SingerProfileResponse.from(profile), "프로필이 수정되었습니다.");
    }

    record CreateProfileRequest(String stageName, String bio, List<String> genres) {}
    record UpdateProfileRequest(String stageName, String bio, List<String> genres) {}
}
