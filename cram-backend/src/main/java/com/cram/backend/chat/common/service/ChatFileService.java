package com.cram.backend.chat.common.service;

import com.cram.backend.chat.common.dto.ChatFileUploadConfig;
import com.cram.backend.chat.common.dto.ChatFileUploadResult;
import com.cram.backend.user.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 채팅 파일 업로드 공통 서비스
 * GroupChat과 MeetingRoom에서 공통으로 사용하는 파일 업로드 로직을 제공합니다.
 */
public interface ChatFileService {
    
    /**
     * 파일을 업로드하고 채팅 메시지로 전송합니다.
     * 
     * @param files 업로드할 파일 목록
     * @param config 업로드 설정 정보 (경로, 브로드캐스트 설정 등)
     * @param user 업로드하는 사용자
     * @return 업로드된 파일 URL 목록과 메시지 정보
     */
    ChatFileUploadResult uploadFiles(List<MultipartFile> files, ChatFileUploadConfig config, UserEntity user);
}