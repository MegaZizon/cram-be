package com.cram.backend.chat.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 채팅 파일 업로드 설정 정보
 * 도메인별로 다른 설정을 제공하기 위한 DTO
 */
@Data
@Builder
public class ChatFileUploadConfig {
    
    /**
     * 그룹 ID
     */
    private Long groupId;
    
    /**
     * 미팅룸 ID (GroupChat의 경우 null)
     */
    private Long meetingRoomId;
    
    /**
     * 파일 접근을 위한 origin URL
     */
    private String origin;
    
    
    /**
     * 업로드 타입 (group, meetingroom)
     */
    private String uploadType;
    
    /**
     * 파일 저장 경로 생성
     * @return 파일 저장 경로
     */
    public String getUploadPath() {
        if ("meetingroom".equals(uploadType)) {
            return "upload/group/" + groupId + "/meetingroom/" + meetingRoomId;
        }
        return "upload/group/" + groupId;
    }
    
    /**
     * 파일 URL 생성
     * @param storedFileName 저장된 파일명
     * @return 웹 접근 가능한 파일 URL
     */
    public String generateFileUrl(String storedFileName) {
        return origin + "/" + getUploadPath() + "/" + storedFileName;
    }
}