package com.cram.backend.groupmember.controller;

import com.cram.backend.groupmember.dto.*;
import com.cram.backend.groupmember.service.GroupMemberService;
import com.cram.backend.groupmember.service.payload.*;
import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups/{groupId}/members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    private final String MSG_LEAVE_SUCCESS = "그룹에서 탈퇴되었습니다.";
    private final String MSG_FETCH_MEMBER_LIST_SUCCESS = "멤버 목록 조회에 성공했습니다.";
    private final String MSG_KICK_MEMBER_SUCCESS = "그룹에서 유저를 추방했습니다.";

    @PostMapping("/{memberId}/leave")
    ResponseEntity<?> leaveGroup(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestBody LeaveGroupRequestDto requestDto
    ) {
        try {
            LeaveGroupRequestInfo requestInfo = LeaveGroupRequestInfo.builder()
                    .groupId(groupId)
                    .memberId(memberId)
                    .leaveReason(requestDto.getReason())
                    .build();
            LeaveGroupResponseInfo responseInfo = groupMemberService.leaveGroup(requestInfo);
            LeaveGroupResponseDto dto = LeaveGroupResponseDto.from(responseInfo);
            GroupMemberAPIResponse<LeaveGroupResponseDto> response = new GroupMemberAPIResponse<>(MSG_LEAVE_SUCCESS, dto);
            return ResponseEntity.ok(response);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    @GetMapping
    ResponseEntity<?> fetchMemberList(
            @PathVariable Long groupId,
            @RequestParam(required = false) String nickname,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, size);
            FetchMemberListRequestInfo requestInfo = FetchMemberListRequestInfo.builder()
                    .groupId(groupId)
                    .nickname(Optional.ofNullable(nickname))
                    .pageable(pageable)
                    .build();
            FetchMemberListResponseInfo responseInfo = groupMemberService.fetchMemeberList(requestInfo);
            List<FetchMemberResponseDto> content = responseInfo.getMemberList().stream()
                    .map(FetchMemberResponseDto::from)
                    .collect(Collectors.toList());

            FetchMemberListResponseDto dto = new FetchMemberListResponseDto(content, responseInfo.getPageableInfo());
            GroupMemberAPIResponse<FetchMemberListResponseDto> apiResponse =
                    new GroupMemberAPIResponse<>(MSG_FETCH_MEMBER_LIST_SUCCESS, dto);

            return ResponseEntity.ok(apiResponse);
        } catch (StudyGroupRuntimeException error) {
            return this.makeErrorResponse(error);
        }
    }

    @PostMapping("/{memberId}/kick")
    ResponseEntity<?> kickMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestBody(required = false) KickMemberRequestDto requestDto
    ) {
        try {
            KickMemberRequestInfo requestInfo = KickMemberRequestInfo.builder()
                    .groupId(groupId)
                    .memberId(memberId)
                    .reason(requestDto.getReason())
                    .build();
            KickMemberResponseInfo responseInfo = groupMemberService.kickMember(requestInfo);
            KickMemberResponseDto dto = KickMemberResponseDto.builder()
                    .memberId(responseInfo.getMemberId())
                    .kickedAt(responseInfo.getKickedAt())
                    .reason(responseInfo.getReason())
                    .build();
            return ResponseEntity.ok(new GroupMemberAPIResponse<KickMemberResponseDto>(MSG_KICK_MEMBER_SUCCESS, dto));
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
