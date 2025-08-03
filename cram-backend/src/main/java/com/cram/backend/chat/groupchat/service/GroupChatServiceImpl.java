package com.cram.backend.chat.groupchat.service;

import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import com.cram.backend.chat.common.dto.ChatMessageResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatFileUploadResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatMessageHistoryResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatTicketResponseDto;
import com.cram.backend.chat.groupchat.entity.GroupChatMessage;
import com.cram.backend.chat.groupchat.entity.GroupChatMessageAttachment;
import com.cram.backend.chat.groupchat.repository.GroupChatMessageRepository;
import com.cram.backend.chat.groupchat.exception.GroupChatErrorInfo;
import com.cram.backend.chat.groupchat.exception.GroupChatRuntimeException;
import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.user.dto.CustomOAuth2User;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupChatServiceImpl implements GroupChatService{

    private final UserRepository userRepository;
    private final GroupChatMessageRepository groupChatMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final JWTUtil jWTUtil;
    private final GroupChatFileService groupChatFileService;
    private final StudyGroupRepository studyGroupRepository;


    @Override
    public void saveGroupChatMessage(ChatMessageInternalDto chatMessageInternalDto)  {

        log.debug("[GroupChatService]chatMessageInternalDto:{}",chatMessageInternalDto);

        Long userId = Long.parseLong(chatMessageInternalDto.getUserId());
        Long groupId = Long.parseLong(chatMessageInternalDto.getStudyGroupId());

        // 1. 해당 유저를 찾음
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GroupChatRuntimeException(GroupChatErrorInfo.USER_NOT_FOUND));

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("StudyGroup not found"));

        // 2. 메세지 저장하기위한 Dto 생성
        GroupChatMessage message = GroupChatMessage.builder()
                .content(chatMessageInternalDto.getContent())
                .studyGroup(studyGroup)
                .user(user)
                .createdAt(chatMessageInternalDto.getCreatedAt())
                .build();
        // 3. DB에 저장하기
        GroupChatMessage savedMsg = groupChatMessageRepository.save(message);

        // 4. 메세지의 아이디를 설정
        chatMessageInternalDto.setRealId(savedMsg.getId().toString());

        // 5. 저장된 메세지 아이디를 socket 서버에 알림
        rabbitTemplate.convertAndSend("chat.direct","group.chat.id.message", chatMessageInternalDto);

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<GroupChatMessageHistoryResponseDto> searchGroupChatMessageHistoryByGroupId(Long groupId, Long before, Long limit) {
        // 1. Limit 만큼 메세지를 검색하기 위해 pageable을 생성 (0~limit 까지)
        log.debug("[searchMSG] groupId:{} before:{} limit:{}",groupId,before,limit);
        Pageable pageable = PageRequest.of(0, limit.intValue());

        // 2. 메시지 ID만 pageable 로 조회
        List<Long> messageIds;
        if (before == null) {
            messageIds = groupChatMessageRepository.findMessageIdsByStudyGroupId(groupId, pageable);
        } else {
            messageIds = groupChatMessageRepository.findMessageIdsByStudyGroupIdAndIdLessThan(groupId, before, pageable);
        }

        // 2-1. 찾는 메시지가 없으면 빈 응답 반환
        if (messageIds.isEmpty()) {
            GroupChatMessageHistoryResponseDto response = GroupChatMessageHistoryResponseDto.builder()
                    .messages(List.of())
                    .nextCursor(null)
                    .hasMore(false)
                    .build();
            return ResponseEntity.ok(response);
        }

        // 3. 메시지 ID 로 JOIN FETCH 조회
        List<GroupChatMessage> rawMessages = groupChatMessageRepository.findByIdsWithUserAndAttachments(messageIds);

        // 4. DTO 생성
        List<ChatMessageResponseDto> messageDtos = rawMessages.stream().map(msg -> {
            UserEntity user = msg.getUser();
            String senderName = user.getName();

            boolean isImage = !msg.getAttachments().isEmpty();
            List<String> imageUrls = isImage ? 
                msg.getAttachments().stream().map(GroupChatMessageAttachment::getFilePath).toList() : null;

            return ChatMessageResponseDto.builder()
                    .messageId(msg.getId())
                    .chatroomId(null)
                    .senderId(msg.getUser().getUsername())
                    .senderName(senderName)
                    .senderProfileUrl(null)
                    .messageType(msg.getAttachments().isEmpty() ? "text" : "image")
                    .message(msg.getContent())
                    .image(imageUrls)
                    .createdAt(msg.getCreatedAt())
                    .build();
        }).toList();

        Long nextCursor = !messageDtos.isEmpty()
                ? messageDtos.get(messageDtos.size() - 1).getMessageId()
                : null;

        boolean hasMore = messageIds.size() == limit;

        GroupChatMessageHistoryResponseDto response = GroupChatMessageHistoryResponseDto.builder()
                .messages(messageDtos)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();

        log.debug("message:{}", response);

        // 5. 응답 반환
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<GroupChatFileUploadResponseDto> uploadFile(List<MultipartFile> files, Long groupId, String origin) {
        log.debug("[uploadFileService] groupId:{} files:{}", groupId, files);

        // 1. 사용자 인증 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = customUserDetails.getUserId();

        // 2. 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GroupChatRuntimeException(GroupChatErrorInfo.USER_NOT_FOUND));

        // TODO: 그룹 권한 확인

        // 3. 파일 업로드 위임
        GroupChatFileUploadResponseDto response = groupChatFileService.uploadFiles(files, groupId, origin, user);
        
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<GroupChatTicketResponseDto> issueGroupSocketTicket(Long groupId) {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // TODO
        // 해당 그룹의 접근 권한 확인 findByGroupId, 이후 SecurityContextHolder에서 UserId 찾아와서 해당 Group에
        // userId가 있는지 확인, 있다면 티켓 발급

        Long userId = customUserDetails.getUserId();
        String userName = customUserDetails.getUsername();

        String token = jWTUtil.createGroupChatTicket(groupId, userName, userId, 999999L);
        log.debug("[issueTicket] token:{}", token);

        GroupChatTicketResponseDto.GroupChatTicketData data = GroupChatTicketResponseDto.GroupChatTicketData.builder()
                .groupChatTicket(token)
                .build();

        GroupChatTicketResponseDto response = GroupChatTicketResponseDto.builder()
                .message("그룹 채팅 입장 티켓이 발급되었습니다.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

}
