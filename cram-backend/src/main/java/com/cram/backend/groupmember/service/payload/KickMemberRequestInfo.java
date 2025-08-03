package com.cram.backend.groupmember.service.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KickMemberRequestInfo {
    private Long groupId;
    private Long memberId;
    private String reason;

    @Builder
    public KickMemberRequestInfo(Long groupId, Long memberId, String reason) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.reason = reason;
    }
}
