package com.cram.backend.chat.groupchat.controller;

import com.cram.backend.chat.groupchat.dto.response.GroupChatFileUploadResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatMessageHistoryResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatTicketResponseDto;
import com.cram.backend.chat.groupchat.service.GroupChatService;
import com.cram.backend.user.dto.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Group Chat API", description = "그룹 채팅 관련 API - JWT 인증이 필요하며 그룹 멤버만 접근 가능")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupChatController {

    private final GroupChatService groupChatService;

    @Operation(summary = "그룹 채팅 소켓 티켓 발급", description = "그룹 채팅에 참여하기 위한 소켓 연결 티켓을 발급합니다. WebSocket 연결 시 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소켓 티켓 발급 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/join")
    public ResponseEntity<GroupChatTicketResponseDto> getGroupChatTicket(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId) {
        return groupChatService.issueGroupSocketTicket(groupId);
    }

    @Operation(summary = "그룹 채팅 메시지 히스토리 조회", description = "그룹의 채팅 메시지 히스토리를 커서 기반 페이징으로 조회합니다. before 파라미터로 특정 메시지 이전의 메시지들을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 히스토리 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @GetMapping("/{groupId}/chat")
    public ResponseEntity<GroupChatMessageHistoryResponseDto> getGroupChat(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "커서 기준 메시지 ID (이전 메시지 조회용)", example = "100")
            @RequestParam(required = false) Long before,
            @Parameter(description = "조회할 메시지 개수", example = "20")
            @RequestParam Long limit) {
        return groupChatService.searchGroupChatMessageHistoryByGroupId(groupId, before, limit);
    }

    @Operation(summary = "그룹 채팅 파일 업로드", description = "그룹 채팅에 파일(이미지)을 업로드하고 채팅으로 전송합니다. 업로드된 파일은 채팅 메시지로 자동 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 및 채팅 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 파일 크기 초과"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "그룹 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("@groupPermissionEvaluator.hasAccess(#groupId, principal.userId)")
    @PostMapping(value="/{groupId}/chat/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupChatFileUploadResponseDto> postGroupChatFile(
            @Parameter(description = "그룹 ID", example = "12345")
            @PathVariable Long groupId,
            @Parameter(description = "업로드할 파일 목록 (이미지 파일)")
            @RequestParam(value = "file", required = false) List<MultipartFile> file,
            HttpServletRequest request) {
        log.debug("postGroupChatFile: groupId: " + groupId + ", file: " + file);

        String origin = request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());

        return groupChatService.uploadFile(file, groupId, origin);
    }
}
