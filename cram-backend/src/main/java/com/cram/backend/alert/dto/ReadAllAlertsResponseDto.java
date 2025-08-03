package com.cram.backend.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReadAllAlertsResponseDto {
    private Integer updatedCount;          // 업데이트된 알림 개수
    private LocalDateTime updatedAt;       // 업데이트 시간
}