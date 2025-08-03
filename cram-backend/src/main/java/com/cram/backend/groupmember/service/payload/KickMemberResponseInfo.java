package com.cram.backend.groupmember.service.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KickMemberResponseInfo {
    private Long memberId;
    private LocalDateTime kickedAt;
    private String reason;

    @Builder
    public KickMemberResponseInfo(Long memberId, LocalDateTime kickedAt, String reason) {
        this.memberId = memberId;
        this.kickedAt = kickedAt;
        this.reason = reason;
    }
}
