package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import com.cram.backend.chat.common.service.ChatBroadcastService;
import com.cram.backend.chat.common.dto.ChatMessageResponseDto;
import com.cram.backend.chat.meetingroom.dto.MeetingRoomCallMemberMetaDto;
import com.cram.backend.chat.meetingroom.dto.MeetingRoomInfo;
import com.cram.backend.chat.meetingroom.dto.MeetingRoomMetaInfo;
import com.cram.backend.chat.meetingroom.dto.response.GetMeetingRoomResponse;
import com.cram.backend.chat.meetingroom.dto.response.*;
import com.cram.backend.chat.meetingroom.entity.MeetingRoom;
import com.cram.backend.chat.meetingroom.entity.MeetingRoomChatMessage;
import com.cram.backend.chat.meetingroom.entity.MeetingRoomChatMessageAttachment;
import com.cram.backend.chat.meetingroom.exception.MeetingRoomErrorInfo;
import com.cram.backend.chat.meetingroom.exception.MeetingRoomRuntimeException;
import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.chat.meetingroom.repository.MeetingRoomChatMessageRepository;
import com.cram.backend.chat.meetingroom.repository.MeetingRoomRepository;
import com.cram.backend.user.dto.CustomOAuth2User;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private final UserRepository userRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomChatMessageRepository meetingRoomChatMessageRepository;
    private final MeetingRoomRedisService meetingRoomRedisService;
    private final JWTUtil jwtUtil;
    private final ChatBroadcastService chatBroadcastService;
    private final MeetingRoomFileService meetingRoomFileService;

    /**
     * 미팅룸 채팅 메시지 저장
     * RabbitMQ로부터 받은 채팅 메시지를 데이터베이스에 저장합니다.
     */
    @Override
    public void saveMeetingRoomChatMessage(ChatMessageInternalDto chatMessageInternalDto) throws JsonProcessingException {

        log.debug("[MeetingRoomService] chatMessageInternalDto: {}", chatMessageInternalDto);

        // 1. DTO에서 사용자 ID와 미팅룸 ID 추출
        Long userId = Long.parseLong(chatMessageInternalDto.getUserId());
        Long meetingRoomId = Long.parseLong(chatMessageInternalDto.getMeetingRoomId());

        // 2. 사용자와 미팅룸 엔티티 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.USER_NOT_FOUND));
        MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.MEETING_ROOM_NOT_FOUND));

        // 3. 채팅 메시지 엔티티 생성 및 저장
        MeetingRoomChatMessage message = MeetingRoomChatMessage.builder()
                .content(chatMessageInternalDto.getContent())
                .meetingRoom(meetingRoom)
                .user(user)
                .createdAt(chatMessageInternalDto.getCreatedAt())
                .build();

        MeetingRoomChatMessage savedMsg = meetingRoomChatMessageRepository.save(message);

        // 4. 실제 DB ID를 DTO에 설정하고 ID 업데이트 메시지 발송
        chatMessageInternalDto.setRealId(savedMsg.getId().toString());
        chatBroadcastService.sendMessageIdUpdate(chatMessageInternalDto,"meeting.room.chat.id.message");

    }

    /**
     * 미팅룸 생성
     * 새로운 미팅룸을 생성하고 Redis에 초기 정보를 저장합니다.
     */
    @Override
    @Transactional
    public Long createMeetingRoom(String title, Long groupId) {
        // 1. 현재 인증된 사용자 정보 조회
        CustomOAuth2User customUserDetails = (CustomOAuth2User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.USER_NOT_FOUND));

        // TODO: Study 그룹원인지 확인

        // 2. 미팅룸 엔티티 생성 및 저장
        MeetingRoom meetingRoom = MeetingRoom.builder()
                .title(title)
                .studyGroupId(groupId)
                .manager(user)
                .isActive(false)
                .build();

        MeetingRoom saved = meetingRoomRepository.save(meetingRoom);
        Long meetingRoomId = saved.getId();

        // 3. Redis에 미팅룸 정보 저장
        MeetingRoomInfo meetingRoomInfo = MeetingRoomInfo.builder()
                .managerId(user.getId().toString())
                .build();
        meetingRoomRedisService.saveMeetingRoomInfo(meetingRoomId, meetingRoomInfo);

        return meetingRoomId;
    }

    /**
     * 미팅룸 입장 티켓 발급
     * 사용자가 미팅룸에 입장할 수 있는 JWT 티켓을 발급합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<MeetingRoomTicketResponseDto> issueMeetingRoomSocketTicket(Long meetingRoomId, Long groupId) {
        // 1. 현재 인증된 사용자 정보 조회
        CustomOAuth2User customUserDetails = (CustomOAuth2User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.USER_NOT_FOUND));

        Long userId = user.getId();
        String userName = user.getUsername();

        // TODO: Study 그룹원인지 확인

        // 2. 미팅룸 티켓 발행
        String token = jwtUtil.createMeetingRoomTicket(groupId, userName, userId, meetingRoomId, 99999999L);

        // 3. 응답 DTO 생성
        MeetingRoomTicketResponseDto.MeetingRoomTicketData data = MeetingRoomTicketResponseDto.MeetingRoomTicketData.builder()
                .meetingRoomTicket(token)
                .meetingRoomId(meetingRoomId)
                .build();

        MeetingRoomTicketResponseDto response = MeetingRoomTicketResponseDto.builder()
                .message("미팅룸 입장 티켓이 발급되었습니다.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 미팅룸 목록 조회
     * 그룹의 미팅룸 목록을 페이징으로 조회하고 실시간 참가자 수를 포함합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public GetMeetingRoomResponse selectMeetingRoomMetaInfoList(Long groupId, int page, int size, int isActive) {
        // 1. 페이징 및 필터링 조건으로 미팅룸 조회
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<MeetingRoom> meetingRooms;
        
        if (isActive == 1) {
            meetingRooms = meetingRoomRepository.findByStudyGroupIdAndIsActive(groupId, true, pageable);
        } else if (isActive == 0) {
            meetingRooms = meetingRoomRepository.findByStudyGroupIdAndIsActive(groupId, false, pageable);
        } else {
            meetingRooms = meetingRoomRepository.findByStudyGroupId(groupId, pageable);
        }
        // 2. 활성화된 미팅룸의 참가자 수 일괄 조회 (multiGet 성능 최적화)
        List<MeetingRoom> activeRooms = meetingRooms.stream().filter(r -> Boolean.TRUE.equals(r.getIsActive())).toList();
        List<Long> activeRoomIds = activeRooms.stream().map(MeetingRoom::getId).toList();
        Map<Long, Integer> participantCountMap = meetingRoomRedisService.getParticipantCounts(activeRoomIds);

        // 3. DTO 생성 및 반환
        List<MeetingRoomMetaInfo> content = meetingRooms.stream().map(r -> MeetingRoomMetaInfo.builder()
                .roomId(r.getId()).title(r.getTitle())
                .managerId(r.getManager().getId().toString())
                .managerName(r.getManager().getName())
                .members(participantCountMap.getOrDefault(r.getId(), 0))
                .maxMembers(30).isActive(r.getIsActive())
                .startedAt(r.getStartedAt()).endedAt(r.getEndedAt()).build()).toList();

        return GetMeetingRoomResponse.builder()
                .content(content)
                .pageable(GetMeetingRoomResponse.PageableInfo.builder()
                        .page(page).size(size)
                        .totalElements(meetingRooms.getTotalElements())
                        .totalPages(meetingRooms.getTotalPages()).build())
                .build();

    }

    /**
     * 미팅룸 파일 업로드
     * 이미지 파일을 업로드하고 채팅 메시지로 전송합니다.
     */
    @Override
    @Transactional
    public ResponseEntity<MeetingRoomFileUploadResponseDto> uploadFile(List<MultipartFile> files, Long groupId, Long meetingRoomId, String origin) {
        // 1. 현재 인증된 사용자 정보 조회
        CustomOAuth2User customUserDetails = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = customUserDetails.getUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new MeetingRoomRuntimeException(MeetingRoomErrorInfo.USER_NOT_FOUND));

        // TODO: 그룹 권한 확인

        // 2. 파일 서비스 호출
        MeetingRoomFileUploadResponseDto response = meetingRoomFileService.uploadFiles(files, groupId, meetingRoomId, origin, user);
        
        return ResponseEntity.ok(response);
    }

//    /**
//     * 미팅룸 존재 여부 확인
//     * 미팅룸의 존재 여부와 기본 정보를 확인합니다.
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<MeetingRoomExistenceResponseDto> isMeetingRoomExist(Long meetingRoomId) {
//        return meetingRoomRepository.findById(meetingRoomId)
//                .map(room -> ResponseEntity.ok(MeetingRoomExistenceResponseDto.success(
//                        room.getEndedAt(),
//                        room.getManager().getId()
//                )))
//                .orElseGet(() -> ResponseEntity.ok(MeetingRoomExistenceResponseDto.failure(
//                        "해당 미팅룸이 존재하지 않습니다"
//                )));
//    }

    /**
     * 미팅룸 채팅 메시지 히스토리 조회
     * 커서 기반 페이징으로 채팅 메시지를 조회합니다. (N+1 쿼리 문제 해결됨)
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<MeetingRoomChatHistoryResponseDto> searchMeetingRoomChatMessageHistoryByGroupId(Long meetingRoomId, Long before, Long limit) {
        Pageable pageable = PageRequest.of(0, limit.intValue());
        log.debug("[searchMSG] meetingRoomId: {}, before: {}, limit: {}", meetingRoomId, before, limit);

        // 1. 메시지 ID만 페이징으로 조회
        List<Long> messageIds;
        if (before == null) {
            messageIds = meetingRoomChatMessageRepository.findMessageIdsByMeetingRoomId(meetingRoomId, pageable);
        } else {
            messageIds = meetingRoomChatMessageRepository.findMessageIdsByMeetingRoomIdAndIdLessThan(meetingRoomId, before, pageable);
        }

        // 2. 빈 결과 처리
        if (messageIds.isEmpty()) {
            MeetingRoomChatHistoryResponseDto response = MeetingRoomChatHistoryResponseDto.builder()
                    .messages(List.of())
                    .nextCursor(null)
                    .hasMore(false)
                    .build();
            return ResponseEntity.ok(response);
        }

        // 3. 메시지 상세 정보 JOIN FETCH로 조회
        List<MeetingRoomChatMessage> rawMessages = meetingRoomChatMessageRepository.findByIdsWithUserAndAttachments(messageIds);

        // 4. DTO 변환
        List<ChatMessageResponseDto> messageDtos = rawMessages.stream().map(msg -> {
            UserEntity user = msg.getUser();

            List<String> imageUrls = msg.getAttachments().stream()
                    .map(MeetingRoomChatMessageAttachment::getFilePath)
                    .toList();

            return ChatMessageResponseDto.builder()
                    .messageId(msg.getId())
                    .chatroomId(msg.getMeetingRoom().getId())
                    .senderId(user.getUsername())
                    .senderName(user.getName())
                    .senderProfileUrl(null)
                    .messageType(msg.getAttachments().isEmpty() ? "text" : "image")
                    .message(msg.getContent())
                    .image(imageUrls.isEmpty() ? null : imageUrls)
                    .createdAt(msg.getCreatedAt())
                    .build();
        }).toList();

        // 5. 페이징 정보 설정
        Long nextCursor = !messageDtos.isEmpty()
                ? messageDtos.get(messageDtos.size() - 1).getMessageId()
                : null;
        boolean hasMore = messageIds.size() == limit;

        // 6. 응답 생성
        MeetingRoomChatHistoryResponseDto response = MeetingRoomChatHistoryResponseDto.builder()
                .messages(messageDtos)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 미팅룸 참가자 정보 조회
     * Redis에서 현재 미팅룸에 참가 중인 멤버들의 정보를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<MeetingRoomMemberResponseDto> searchMeetingRoomMemberInfo(Long meetingRoomId, Long groupId) {
        // 1. Redis에서 미팅룸 정보 조회
        MeetingRoomInfo info = meetingRoomRedisService.getMeetingRoomInfo(meetingRoomId);
        
        if (info == null) {
            log.debug("[searchMemberInfo] room is not exist. roomId: {}", meetingRoomId);
            throw new MeetingRoomRuntimeException(MeetingRoomErrorInfo.MEETING_ROOM_NOT_FOUND);
        }

        // 2. 멤버 리스트 추출 및 응답 DTO 생성
        List<MeetingRoomCallMemberMetaDto> memberList = info.getMembers() == null ?
                new ArrayList<>() : info.getMembers();

        MeetingRoomMemberResponseDto response = MeetingRoomMemberResponseDto.builder()
                .members(memberList)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 미팅룸 비활성화 (rabbitMQ)
     * 미팅룸을 종료 상태로 변경합니다.
     */
    @Override
    @Transactional
    public void inActiveMeetingRoom(String str) {
        Long meetingRoomId = Long.parseLong(str);
        meetingRoomRepository.findById(meetingRoomId).ifPresent(meetingRoom -> {
            meetingRoom.setIsActive(false);
            meetingRoom.setEndedAt(LocalDateTime.now());
            meetingRoomRepository.save(meetingRoom);
        });
    }

    /**
     * 미팅룸 활성화 (rabbitMQ)
     * 미팅룸을 시작 상태로 변경합니다.
     */
    @Override
    @Transactional
    public void activeMeetingRoom(String str) {
        Long meetingRoomId = Long.parseLong(str);
        meetingRoomRepository.findById(meetingRoomId).ifPresent(meetingRoom -> {
            meetingRoom.setIsActive(true);
            meetingRoom.setStartedAt(LocalDateTime.now());
            meetingRoomRepository.save(meetingRoom);
        });
    }
}