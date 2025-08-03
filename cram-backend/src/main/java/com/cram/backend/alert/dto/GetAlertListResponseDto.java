package com.cram.backend.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetAlertListResponseDto {
    private List<AlertDto> data;        // 알림 목록

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AlertDto {
        private Long alertId;                  // 알림 ID
        private String category;               // GROUP, GENERAL
        private String type;                   // ACCEPT, FAIL, COMMENT, SUMMARY, INQUIRY_REPLY 등
        private String message;                // 알림 메시지
        private Long targetId;                 // 대상 ID (그룹ID, 문의ID 등)
        private String targetType;             // 대상 타입 (group, inquiry 등)
        private SenderInfo sender;             // 알림 발생시킨 사용자 정보 (있는 경우)
        private Boolean isRead;                // 읽음 여부
        private LocalDateTime createdAt;       // 생성일시
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class SenderInfo {
        private Long userId;                   // 발신자 사용자 ID
        private String username;               // 발신자 이름
        private String profileImage;           // 발신자 프로필 이미지 URL
    }
}