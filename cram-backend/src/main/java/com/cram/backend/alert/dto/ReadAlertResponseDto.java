package com.cram.backend.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReadAlertResponseDto {
    private Long alertId;                  // 알림 ID
    private Boolean isRead;                // 읽음 여부 (항상 true)
    private LocalDateTime updatedAt;       // 업데이트 시간
}
