package com.cram.backend.groupmember.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KickMemberResponseDto {
    private Long memberId;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime kickedAt;
    private String reason;

    @Builder
    public KickMemberResponseDto(Long memberId, LocalDateTime kickedAt, String reason) {
        this.memberId = memberId;
        this.kickedAt = kickedAt;
        this.reason = reason;
    }
}
