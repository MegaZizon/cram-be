package com.cram.backend.joingroup.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RejectGroupJoinRequestInfo {
    private Long userId;
    private Long groupId;
    private Long requestId;
    private String reason;

    @Builder
    public RejectGroupJoinRequestInfo(Long userId, Long groupId, Long requestId, String reason) {
        this.userId = userId;
        this.groupId = groupId;
        this.requestId = requestId;
        this.reason = reason;
    }
}
