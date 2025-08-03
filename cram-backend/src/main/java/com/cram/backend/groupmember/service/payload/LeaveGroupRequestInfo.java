package com.cram.backend.groupmember.service.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeaveGroupRequestInfo {
    private Long groupId;
    private Long memberId;
    private String leaveReason;

    @Builder
    public LeaveGroupRequestInfo(Long groupId, Long memberId, String leaveReason) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.leaveReason = leaveReason;
    }
}
