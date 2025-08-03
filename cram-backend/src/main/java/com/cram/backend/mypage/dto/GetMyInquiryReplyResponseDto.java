package com.cram.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetMyInquiryReplyResponseDto {
    private List<InquiryReplyInfo> replies;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class InquiryReplyInfo {
        private Long inquiryId;
        private String inquiryTitle; // 원본 문의 제목
        private String replyContent;
        private LocalDateTime createdAt;
        private String adminId;
    }
}