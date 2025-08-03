package com.cram.backend.joingroup.service.dto;

import com.cram.backend.joingroup.entity.GroupJoinRequest;
import com.cram.backend.studygroup.entity.StudyGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RejectGroupJoinResponseInfo {
    private Long requestId;
    private String reason;
    private Long groupId;
    private String groupName;
    private Long userId;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @Builder
    public RejectGroupJoinResponseInfo(Long requestId, String reason, Long groupId, String groupName, Long userId, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.requestId = requestId;
        this.reason = reason;
        this.groupId = groupId;
        this.groupName = groupName;
        this.userId = userId;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static RejectGroupJoinResponseInfo from(GroupJoinRequest request, StudyGroup group, Long userId, LocalDateTime processedAt) {
        return RejectGroupJoinResponseInfo.builder()
                .requestId(request.getId())
                .reason(request.getReason())
                .groupId(group.getId())
                .groupName(group.getName())
                .userId(userId)
                .requestedAt(request.getRequestedAt())
                .processedAt(processedAt)
                .build();
    }
}
