package com.cram.backend.groupmember.dto;

import com.cram.backend.groupmember.service.payload.FetchMemberResponseInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FetchMemberResponseDto {
    private Long memberId;
    private String nickname;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime joinedAt;

    @Builder
    public FetchMemberResponseDto(Long memberId, String nickname, LocalDateTime joinedAt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinedAt = joinedAt;
    }

    public static FetchMemberResponseDto from(FetchMemberResponseInfo responseInfo) {
        return FetchMemberResponseDto.builder()
                .memberId(responseInfo.getMemberId())
                .nickname(responseInfo.getNickname())
                .joinedAt(responseInfo.getJoinedAt())
                .build();
    }
}
