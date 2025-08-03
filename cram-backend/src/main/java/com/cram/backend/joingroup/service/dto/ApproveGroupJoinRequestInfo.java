package com.cram.backend.joingroup.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApproveGroupJoinRequestInfo {
    private Long userId;
    private Long groupId;
    private Long requestId;

    @Builder
    public ApproveGroupJoinRequestInfo(Long userId, Long groupId, Long requestId) {
        this.userId = userId;
        this.groupId = groupId;
        this.requestId = requestId;
    }
}
