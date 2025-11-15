package com.minibuskingbig.singer.dto;

import com.minibuskingbig.singer.entity.SingerProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingerProfileResponse {

    private Long singerId;
    private String stageName;
    private String bio;
    private List<String> genres;
    private String profileBannerUrl;
    private Boolean isVerified;
    private Integer followerCount;
    private Integer totalEventCount;

    public static SingerProfileResponse from(SingerProfile profile) {
        return SingerProfileResponse.builder()
            .singerId(profile.getSingerId())
            .stageName(profile.getStageName())
            .bio(profile.getBio())
            .genres(profile.getGenres())
            .profileBannerUrl(profile.getProfileBannerUrl())
            .isVerified(profile.getIsVerified())
            .followerCount(profile.getFollowerCount())
            .totalEventCount(profile.getTotalEventCount())
            .build();
    }
}
