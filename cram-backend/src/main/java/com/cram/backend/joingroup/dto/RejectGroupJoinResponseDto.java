package com.cram.backend.joingroup.dto;

import com.cram.backend.joingroup.service.dto.RejectGroupJoinResponseInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RejectGroupJoinResponseDto {
    private Long requestId;
    private String reason;
    private Long groupId;
    private String groupName;
    private Long userId;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime requestedAt;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime processedAt;

    @Builder
    public RejectGroupJoinResponseDto(Long requestId, String reason, Long groupId, String groupName, Long userId, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.requestId = requestId;
        this.reason = reason;
        this.groupId = groupId;
        this.groupName = groupName;
        this.userId = userId;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static RejectGroupJoinResponseDto from(RejectGroupJoinResponseInfo responseInfo) {
        return RejectGroupJoinResponseDto.builder()
                .requestId(responseInfo.getRequestId())
                .reason(responseInfo.getReason())
                .groupId(responseInfo.getGroupId())
                .groupName(responseInfo.getGroupName())
                .userId(responseInfo.getUserId())
                .requestedAt(responseInfo.getRequestedAt())
                .processedAt(responseInfo.getProcessedAt())
                .build();
    }
}
