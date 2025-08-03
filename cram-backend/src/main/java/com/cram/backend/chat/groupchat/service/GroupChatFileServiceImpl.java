package com.cram.backend.chat.groupchat.service;

import com.cram.backend.chat.common.dto.ChatFileUploadConfig;
import com.cram.backend.chat.common.dto.ChatFileUploadResult;
import com.cram.backend.chat.common.service.ChatBroadcastService;
import com.cram.backend.chat.common.service.ChatFileService;
import com.cram.backend.chat.common.dto.BroadCastChatMessageResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatFileUploadResponseDto;
import com.cram.backend.chat.groupchat.entity.GroupChatMessage;
import com.cram.backend.chat.groupchat.entity.GroupChatMessageAttachment;
import com.cram.backend.chat.groupchat.repository.GroupChatMessageRepository;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupChatFileServiceImpl implements GroupChatFileService {

    private final GroupChatMessageRepository groupChatMessageRepository;
    private final ChatFileService chatFileService;
    private final ChatBroadcastService chatBroadcastService;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public GroupChatFileUploadResponseDto uploadFiles(List<MultipartFile> files, Long groupId, String origin, UserEntity user) {
        log.debug("[GroupChatFileService] groupId:{} files:{}", groupId, files);

        // 1. 공통 파일 업로드 설정 생성
        ChatFileUploadConfig config = ChatFileUploadConfig.builder()
                .groupId(groupId)
                .meetingRoomId(null)
                .origin(origin)
                .uploadType("group")
                .build();
        
        // 2. 메시지 엔티티 생성
        GroupChatMessage message = createChatMessage(groupId, user);
        
        // 3. 공통 파일 업로드 서비스 호출
        ChatFileUploadResult uploadResult = chatFileService.uploadFiles(files, config, user);
        
        // 4. 첨부파일 엔티티 생성
        List<GroupChatMessageAttachment> attachments = createAttachments(uploadResult.getFileUrls(), message);
        message.setAttachments(attachments);
        
        // 5. 메시지 저장
        GroupChatMessage savedMessage = groupChatMessageRepository.save(message);
        
        // 6. 브로드캐스트 메시지 발송
        sendBroadcastMessage(savedMessage, user, uploadResult.getFileUrls(), groupId);
        
        return GroupChatFileUploadResponseDto.builder()
                .url(uploadResult.getFileUrls())
                .build();
    }


    private GroupChatMessage createChatMessage(Long groupId, UserEntity user) {
        LocalDateTime nowLocal = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("StudyGroup not found"));

        return GroupChatMessage.builder()
                .content(null)
                .studyGroup(studyGroup)
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
    private List<GroupChatMessageAttachment> createAttachments(List<String> fileUrls, GroupChatMessage message) {
        List<GroupChatMessageAttachment> attachments = new ArrayList<>();
        
        for (String fileUrl : fileUrls) {
            // URL에서 파일명 추출
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            // UUID(36자) + "_" + 원본파일명 형태에서 원본 파일명 추출
            String originalName = fileName.length() > 37 ? fileName.substring(37) : fileName;
            
            GroupChatMessageAttachment attachment = GroupChatMessageAttachment.builder()
                    .filePath(fileUrl)
                    .originalName(originalName)
                    .savedName(fileName)
                    .groupChatMessage(message)
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
     */
    private void sendBroadcastMessage(GroupChatMessage savedMessage, UserEntity user, 
                                    List<String> fileUrls, Long groupId) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        
        BroadCastChatMessageResponseDto broadcastMessage = BroadCastChatMessageResponseDto.builder()
                .messageId(savedMessage.getId().toString())
                .chatroomId(null)
                .senderId(user.getId().toString())
                .senderName(user.getName())
                .senderProfileUrl(user.getProfileImage())
                .messageType("image")
                .message(null)
                .imageUrl(fileUrls)
                .createdAt(nowUtc.toString())
                .studyGroupId(groupId.toString())
                .build();

        chatBroadcastService.sendFileUploadBroadcast(broadcastMessage, "group.chat.file");
    }
}