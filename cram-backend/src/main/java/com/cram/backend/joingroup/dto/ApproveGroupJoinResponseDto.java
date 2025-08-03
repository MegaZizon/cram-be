package com.cram.backend.joingroup.dto;

import com.cram.backend.joingroup.service.dto.ApproveGroupJoinResponseInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApproveGroupJoinResponseDto {
    private Long requestId;
    private Long groupId;
    private String groupName;
    private Long userId;
    private String nickname;
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
    public ApproveGroupJoinResponseDto(Long requestId, Long groupId, String groupName, Long userId, String nickname, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.requestId = requestId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.userId = userId;
        this.nickname = nickname;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static ApproveGroupJoinResponseDto from(ApproveGroupJoinResponseInfo approveGroupJoinResponseInfo) {
        return ApproveGroupJoinResponseDto.builder()
                .requestId(approveGroupJoinResponseInfo.getRequestId())
                .groupId(approveGroupJoinResponseInfo.getGroupId())
                .groupName(approveGroupJoinResponseInfo.getGroupName())
                .userId(approveGroupJoinResponseInfo.getUserId())
                .nickname(approveGroupJoinResponseInfo.getNickname())
                .requestedAt(approveGroupJoinResponseInfo.getRequestedAt())
                .processedAt(approveGroupJoinResponseInfo.getProcessedAt())
                .build();
    }
}