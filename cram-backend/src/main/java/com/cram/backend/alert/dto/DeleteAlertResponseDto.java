package com.cram.backend.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 알림 삭제 Response DTO
@Getter
@AllArgsConstructor
@Builder
public class DeleteAlertResponseDto {
    private Long alertId;               // 삭제된 알림 ID
    private LocalDateTime deletedAt;           // 삭제된 시간
}
