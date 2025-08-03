package com.cram.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetMyInquiryResponseDto {
    private List<InquiryInfo> inquiries;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class InquiryInfo {
        private Long inquiryId;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private String status;
    }
}