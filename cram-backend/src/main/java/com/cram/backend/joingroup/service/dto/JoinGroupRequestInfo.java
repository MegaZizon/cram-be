package com.cram.backend.joingroup.service.dto;

import com.cram.backend.joingroup.dto.JoinGroupRequestDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinGroupRequestInfo {
    private Long groupId;
    private Long userId;
    private String profileImage;
    private String nickname;
    private String greetingMessage;

    @Builder
    public JoinGroupRequestInfo(Long groupId, Long userId, String profileImage, String nickname, String greetingMessage) {
        this.groupId = groupId;
        this.userId = userId;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.greetingMessage = greetingMessage;
    }

    public static JoinGroupRequestInfo from(JoinGroupRequestDto dto, Long groupId) {
        return JoinGroupRequestInfo.builder()
                .groupId(groupId)
                .userId(dto.getUserId())
                .profileImage(dto.getProfileImage())
                .nickname(dto.getNickname())
                .greetingMessage(dto.getGreetingMessage())
                .build();
    }
}
