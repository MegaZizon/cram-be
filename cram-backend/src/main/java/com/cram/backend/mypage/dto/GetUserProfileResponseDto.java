package com.cram.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetUserProfileResponseDto {
    private final String name;
    private final String email;
    private final String provider;
    private final String profileImage;
}