package com.cram.backend.chat.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 채팅 파일 업로드 결과
 * 업로드된 파일 정보와 메시지 ID를 포함합니다.
 */
@Data
@Builder
public class ChatFileUploadResult {
    
    /**
     * 업로드된 파일 URL 목록
     */
    private List<String> fileUrls;
    
    /**
     * 생성된 메시지 ID
     */
    private Long messageId;
}