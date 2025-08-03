package com.cram.backend.chat.meetingroom.controller;

import com.cram.backend.chat.meetingroom.dto.request.CreateMeetingRoomRequestDto;
import com.cram.backend.chat.meetingroom.dto.response.GetMeetingRoomResponse;
import com.cram.backend.chat.meetingroom.dto.response.*;
import com.cram.backend.chat.meetingroom.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Meeting Room API", description = "미팅룸 관련 API (채팅 포함) - JWT 인증이 필요하며 그룹 멤버만 접근 가능")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomChatController {
    
    private final MeetingRoomService meetingRoomService;

    @Operation(summary = "미팅룸 생성 및 입장 티켓 발급", description = "미팅룸을 생성하고 생성자에게 즉시 입장 티켓을 발급합니다. 생성된 미팅룸에 바로 입장할 수 있는 WebSocket 티켓을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미팅룸 생성 및 티켓 발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @PostMapping("/{groupId}/meeting-rooms/create")
    public ResponseEntity<MeetingRoomTicketResponseDto> createMeetingRoomAndGetTicket(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "미팅룸 생성 정보")
            @RequestBody CreateMeetingRoomRequestDto createMeetingRoomRequestDto) {
        // 1. 미팅룸 생성
        Long meetingRoomId = meetingRoomService.createMeetingRoom(createMeetingRoomRequestDto.getName(), groupId);
        
        // 2. 생성자에게 입장 티켓 발급
        return meetingRoomService.issueMeetingRoomSocketTicket(meetingRoomId, groupId);
    }

    @Operation(summary = "미팅룸 입장 티켓 발급", description = "기존 미팅룸에 입장하기 위한 WebSocket 연결 티켓을 발급합니다. 이 티켓으로 WebSocket에 연결하여 실시간 채팅을 이용할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미팅룸 입장 티켓 발급 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "미팅룸 또는 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/meeting-rooms/{meetingRoomId}/join")
    public ResponseEntity<MeetingRoomTicketResponseDto> getMeetingRoomTicket(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "미팅룸 ID", example = "67890")
            @PathVariable Long meetingRoomId) {
        return meetingRoomService.issueMeetingRoomSocketTicket(meetingRoomId, groupId);
    }

    @Operation(summary = "그룹의 미팅룸 목록 조회", description = "그룹에 속한 미팅룸 목록을 페이징과 활성화 상태 필터링을 지원하여 조회합니다. isActive: 0(비활성), 1(활성), 2(전체)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미팅룸 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/meeting-rooms")
    public ResponseEntity<GetMeetingRoomResponse> getMeetingRooms(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 아이템 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "활성화 상태 필터: 0(비활성), 1(활성), 2(전체)", example = "2")
            @RequestParam(required = false) Integer isActive) {
        // 활성화 상태 기본값 설정 (2: 전체)
        if (isActive == null) isActive = 2;
        
        GetMeetingRoomResponse response = meetingRoomService.selectMeetingRoomMetaInfoList(groupId, page, size, isActive);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "미팅룸 채팅 파일 업로드", description = "미팅룸 채팅에 파일(이미지)을 업로드하고 채팅으로 전송합니다. 업로드된 파일은 채팅 메시지로 자동 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 및 채팅 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 파일 크기 초과"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 또는 미팅룸 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "미팅룸 또는 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @PostMapping(value="/{groupId}/meeting-rooms/{meetingRoomId}/chat/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MeetingRoomFileUploadResponseDto> postMeetingRoomChatFile(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "미팅룸 ID", example = "67890")
            @PathVariable Long meetingRoomId,
            @Parameter(description = "업로드할 파일 목록 (이미지 파일)")
            @RequestParam(value = "file", required = false) List<MultipartFile> file,
            HttpServletRequest request) {
        log.debug("[postMeetingRoomChatFile]: groupId: {}, meetingRoomId: {}", groupId, meetingRoomId);

        // 파일 접근을 위한 origin URL 생성
        String origin = request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());

        return meetingRoomService.uploadFile(file, groupId, meetingRoomId, origin);
    }

//    /**
//     * 미팅룸 메타 정보 조회
//     * 미팅룸의 존재 여부와 기본 정보를 확인합니다.
//     */
//    @GetMapping("/{groupId}/meeting-rooms/{meetingRoomId}/meta")
//    public ResponseEntity<MeetingRoomExistenceResponseDto> getMeetingRoomMetaInfo(
//            @PathVariable Long groupId,
//            @PathVariable Long meetingRoomId) {
//        log.debug("[getMeetingRoomMetaInfo]: groupId: {}, meetingRoomId: {}", groupId, meetingRoomId);
//        return meetingRoomService.isMeetingRoomExist(meetingRoomId);
//    }

    @Operation(summary = "미팅룸 채팅 메시지 히스토리 조회", description = "미팅룸의 채팅 메시지 히스토리를 커서 기반 페이징으로 조회합니다. before 파라미터로 특정 메시지 이전의 메시지들을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 히스토리 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 또는 미팅룸 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "미팅룸 또는 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/meeting-rooms/{meetingRoomId}/chat")
    public ResponseEntity<MeetingRoomChatHistoryResponseDto> getMeetingRoomChat(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "미팅룸 ID", example = "67890")
            @PathVariable Long meetingRoomId,
            @Parameter(description = "커서 기준 메시지 ID (이전 메시지 조회용)", example = "100")
            @RequestParam(required = false) Long before,
            @Parameter(description = "조회할 메시지 개수", example = "20")
            @RequestParam Long limit) {
        return meetingRoomService.searchMeetingRoomChatMessageHistoryByGroupId(meetingRoomId, before, limit);
    }

    @Operation(summary = "미팅룸 참가자 목록 조회", description = "현재 미팅룸에 참가 중인 멤버들의 정보를 조회합니다. 실시간으로 업데이트되는 참가자 목록을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미팅룸 참가자 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 또는 미팅룸 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "미팅룸 또는 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/meeting-rooms/{meetingRoomId}/members")
    public ResponseEntity<MeetingRoomMemberResponseDto> getMeetingRoomMembers(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "미팅룸 ID", example = "67890")
            @PathVariable Long meetingRoomId) {
        return meetingRoomService.searchMeetingRoomMemberInfo(meetingRoomId, groupId);
    }
}