package com.cram.backend.joingroup.dto;

import lombok.Getter;

@Getter
public class JoinGroupRequestDto {
    // TODO: JWT 토큰으로 대체될 예정
    private Long userId;
    private String profileImage;
    private String nickname;
    private  String greetingMessage;
}
