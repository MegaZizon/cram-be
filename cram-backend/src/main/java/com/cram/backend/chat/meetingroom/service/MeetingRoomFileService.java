package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.meetingroom.dto.response.MeetingRoomFileUploadResponseDto;
import com.cram.backend.user.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 미팅룸 파일 업로드 서비스
 */
public interface MeetingRoomFileService {
    
    /**
     * 미팅룸 채팅 파일 업로드
     * @param files 업로드할 파일 목록
     * @param groupId 그룹 ID
     * @param meetingRoomId 미팅룸 ID
     * @param origin 파일 접근을 위한 origin URL
     * @param user 업로드하는 사용자
     * @return 업로드된 파일 URL 목록
     */
    MeetingRoomFileUploadResponseDto uploadFiles(List<MultipartFile> files, Long groupId, Long meetingRoomId, String origin, UserEntity user);
}