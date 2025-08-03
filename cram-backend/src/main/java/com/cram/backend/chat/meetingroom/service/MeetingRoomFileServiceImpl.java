package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.common.dto.ChatFileUploadConfig;
import com.cram.backend.chat.common.dto.ChatFileUploadResult;
import com.cram.backend.chat.common.service.ChatBroadcastService;
import com.cram.backend.chat.common.service.ChatFileService;
import com.cram.backend.chat.common.dto.BroadCastChatMessageResponseDto;
import com.cram.backend.chat.meetingroom.dto.response.MeetingRoomFileUploadResponseDto;
import com.cram.backend.chat.meetingroom.entity.MeetingRoom;
import com.cram.backend.chat.meetingroom.entity.MeetingRoomChatMessage;
import com.cram.backend.chat.meetingroom.entity.MeetingRoomChatMessageAttachment;
import com.cram.backend.chat.meetingroom.repository.MeetingRoomChatMessageRepository;
import com.cram.backend.chat.meetingroom.repository.MeetingRoomRepository;
import com.cram.backend.chat.meetingroom.exception.MeetingRoomErrorInfo;
import com.cram.backend.chat.meetingroom.exception.MeetingRoomRuntimeException;
import com.cram.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 미팅룸 파일 업로드 서비스 구현체
 * 공통 ChatFileService를 사용하여 파일 업로드 로직을 재사용합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomFileServiceImpl implements MeetingRoomFileService {

    private final MeetingRoomChatMessageRepository meetingRoomChatMessageRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final ChatFileService chatFileService;
    private final ChatBroadcastService chatBroadcastService;

    @Override
    @Transactional
    public MeetingRoomFileUploadResponseDto uploadFiles(List<MultipartFile> files, Long groupId, Long meetingRoomId, String origin, UserEntity user) {
        log.debug("[MeetingRoomFileService] groupId:{}, meetingRoomId:{}, files:{}", groupId, meetingRoomId, files);

        // 1. 미팅룸 존재 여부 확인
        MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.MEETING_ROOM_NOT_FOUND));

        // 2. 공통 파일 업로드 설정 생성
        ChatFileUploadConfig config = ChatFileUploadConfig.builder()
                .groupId(groupId)
                .meetingRoomId(meetingRoomId)
                .origin(origin)
                .uploadType("meetingroom")
                .build();
        
        // 3. 메시지 엔티티 생성
        MeetingRoomChatMessage message = createChatMessage(meetingRoom, user);
        
        // 4. 공통 파일 업로드 서비스 호출
        ChatFileUploadResult uploadResult = chatFileService.uploadFiles(files, config, user);
        
        // 5. 첨부파일 엔티티 생성
        List<MeetingRoomChatMessageAttachment> attachments = createAttachments(uploadResult.getFileUrls(), message);
        message.setAttachments(attachments);
        
        // 6. 메시지 저장
        MeetingRoomChatMessage savedMessage = meetingRoomChatMessageRepository.save(message);
        
        // 7. 브로드캐스트 메시지 발송
        sendBroadcastMessage(savedMessage, user, uploadResult.getFileUrls(), groupId, meetingRoomId);
        
        return MeetingRoomFileUploadResponseDto.builder()
                .url(uploadResult.getFileUrls())
                .build();
    }

    /**
     * 채팅 메시지 엔티티 생성
     * @param meetingRoom 미팅룸
     * @param user 사용자
     * @return 생성된 메시지 엔티티
     */
    private MeetingRoomChatMessage createChatMessage(MeetingRoom meetingRoom, UserEntity user) {
        LocalDateTime nowLocal = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime();
        
        return MeetingRoomChatMessage.builder()
                .content(null)
                .meetingRoom(meetingRoom)
                .user(user)
                .createdAt(nowLocal)
                .build();
    }

    /**
     * 첨부파일 엔티티 생성
     * @param fileUrls 파일 URL 목록
     * @param message 채팅 메시지
     * @return 첨부파일 엔티티 목록
     */
    private List<MeetingRoomChatMessageAttachment> createAttachments(List<String> fileUrls, MeetingRoomChatMessage message) {
        List<MeetingRoomChatMessageAttachment> attachments = new ArrayList<>();
        
        for (String fileUrl : fileUrls) {
            // URL에서 파일명 추출
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            // UUID(36자) + "_" + 원본파일명 형태에서 원본 파일명 추출
            String originalName = fileName.length() > 37 ? fileName.substring(37) : fileName;
            
            MeetingRoomChatMessageAttachment attachment = MeetingRoomChatMessageAttachment.builder()
                    .filePath(fileUrl)
                    .originalName(originalName)
                    .savedName(fileName)
                    .meetingRoomChatMessage(message)
                    .build();
            
            attachments.add(attachment);
        }
        
        return attachments;
    }
    
    /**
     * 브로드캐스트 메시지 발송
     * @param savedMessage 저장된 메시지
     * @param user 사용자
     * @param fileUrls 파일 URL 목록
     * @param groupId 그룹 ID
     * @param meetingRoomId 미팅룸 ID
     */
    private void sendBroadcastMessage(MeetingRoomChatMessage savedMessage, UserEntity user, 
                                    List<String> fileUrls, Long groupId, Long meetingRoomId) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        
        BroadCastChatMessageResponseDto broadcastMessage = BroadCastChatMessageResponseDto.builder()
                .messageId(savedMessage.getId().toString())
                .chatroomId(meetingRoomId.toString())
                .senderId(user.getId().toString())
                .senderName(user.getName())
                .senderProfileUrl(user.getProfileImage())
                .messageType("image")
                .message(null)
                .imageUrl(fileUrls)
                .createdAt(nowUtc.toString())
                .studyGroupId(groupId.toString())
                .build();

        chatBroadcastService.sendFileUploadBroadcast(broadcastMessage, "meeting.room.chat.file");
    }
}