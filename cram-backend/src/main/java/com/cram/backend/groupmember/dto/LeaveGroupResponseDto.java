package com.cram.backend.groupmember.dto;

import com.cram.backend.groupmember.service.payload.LeaveGroupResponseInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeaveGroupResponseDto {
    private Long memberId;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime leaveAt;
    private String reason;

    @Builder
    public LeaveGroupResponseDto(Long memberId, LocalDateTime leaveAt, String reason) {
        this.memberId = memberId;
        this.leaveAt = leaveAt;
        this.reason = reason;
    }

    public static LeaveGroupResponseDto from(LeaveGroupResponseInfo responseInfo) {
        return LeaveGroupResponseDto.builder()
                .memberId(responseInfo.getMemberId())
                .leaveAt(responseInfo.getLeftAt())
                .reason(responseInfo.getReason())
                .build();
    }
}
