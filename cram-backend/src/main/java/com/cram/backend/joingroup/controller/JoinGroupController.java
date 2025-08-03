package com.cram.backend.joingroup.controller;

import com.cram.backend.joingroup.dto.*;
import com.cram.backend.joingroup.service.JoinGroupService;
import com.cram.backend.joingroup.service.dto.*;
import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups/{groupId}")
public class JoinGroupController {

    private final JoinGroupService joinGroupService;

    private final String MSG_NOT_DUPLICATED_NICKNAME = "이용 가능한 닉네임입니다.";
    private final String MSG_DUPLICATED_NICKNAME = "이용 불가능한 닉네임입니다.";
    private final String MSG_APPROVE_GROUP_JOIN = "가입 신청이 승인되었습니다.";
    private final String MSG_REJECT_GROUP_JOIN = "가입 신청이 거절되었습니다.";

    @PostMapping("/join")
    ResponseEntity<?> joinStudyGroup(
            @PathVariable Long groupId,
            @RequestBody JoinGroupRequestDto requestDto
    ){
        JoinGroupRequestInfo joinGroupRequestInfo = JoinGroupRequestInfo.from(requestDto, groupId);
        try {
            JoinGroupRequestCreatedInfo createdInfo = joinGroupService.joinGroup(joinGroupRequestInfo);
            JoinGroupResponseDto responseDto = JoinGroupResponseDto.from(createdInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    @GetMapping("/nicknames/{nickname}")
    ResponseEntity<?> validateNickname(@PathVariable Long groupId, @PathVariable String nickname)
    {
        try {
            boolean isNicknameAvailable = joinGroupService.existsNicknameInGroup(groupId, nickname);
            String message = isNicknameAvailable ? MSG_NOT_DUPLICATED_NICKNAME : MSG_DUPLICATED_NICKNAME;
            CheckNicknameDuplicationResponseDto responseDto = new CheckNicknameDuplicationResponseDto(isNicknameAvailable, message);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    @GetMapping("/approvals")
    ResponseEntity<?> fetchJoinGroupRequests(
            @PathVariable Long groupId,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        // TODO: JWT 토큰으로 userId 받을 예정
        Long userId = 5L;
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        JoinGroupApprovalsListData data = joinGroupService.fetchJoinGroupRequests(groupId, userId, pageable);
        GetGroupJoinApprovalsResponseDto responseDto = new GetGroupJoinApprovalsResponseDto(data);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // TODO: ResponseBody User ID -> JWT 토큰으로 대체될 예정
    @PostMapping("/approvals/{requestId}/approve")
    ResponseEntity<?> approveGroupJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @RequestBody ApproveGroupJoinRequestDto requestDto
    ) {
        try {
            Long userId = requestDto.getUserId();
            ApproveGroupJoinRequestInfo requestInfo = ApproveGroupJoinRequestInfo.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .requestId(requestId)
                    .build();
            ApproveGroupJoinResponseInfo responseInfo = joinGroupService.approveGroupJoinRequest(requestInfo);
            ApproveGroupJoinResponseDto responseDto = ApproveGroupJoinResponseDto.from(responseInfo);
            JoinGroupAPIResponse<ApproveGroupJoinResponseDto> response = new JoinGroupAPIResponse<>(MSG_APPROVE_GROUP_JOIN, responseDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    @PostMapping("/approvals/{requestId}/reject")
    ResponseEntity<?> rejectGroupJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @RequestBody RejectGroupJoinRequestDto requestDto
    ) {
        try {
            RejectGroupJoinRequestInfo requestInfo = RejectGroupJoinRequestInfo.builder()
                    .userId(requestDto.getUserId())
                    .groupId(groupId)
                    .requestId(requestId)
                    .reason(requestDto.getReason())
                    .build();
            RejectGroupJoinResponseInfo responseInfo = joinGroupService.rejectGroupJoinRequest(requestInfo);
            RejectGroupJoinResponseDto responseDto = RejectGroupJoinResponseDto.from(responseInfo);
            JoinGroupAPIResponse<RejectGroupJoinResponseDto> response = new JoinGroupAPIResponse<>(MSG_REJECT_GROUP_JOIN, responseDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    private ResponseEntity<Map<String, String>> makeErrorResponse(StudyGroupRuntimeException error) {
        StudyGroupErrorInfo errorInfo = error.getStudyGroupErrorInfo();
        int statusCode = errorInfo.getErrorCode();
        String message = errorInfo.getErrorMessage();
        return ResponseEntity.status(statusCode).body(Map.of("message", message));
    }

}