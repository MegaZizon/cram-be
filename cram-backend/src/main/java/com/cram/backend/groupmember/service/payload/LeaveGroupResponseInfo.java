package com.cram.backend.groupmember.service.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveGroupResponseInfo {
    private Long memberId;
    private LocalDateTime leftAt;
    private String reason;
}