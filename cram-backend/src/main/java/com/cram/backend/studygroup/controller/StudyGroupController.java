package com.cram.backend.studygroup.controller;

import com.cram.backend.studygroup.dto.*;
import com.cram.backend.studygroup.service.StudyGroupService;
import com.cram.backend.studygroup.service.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    private final String MSG_CREATE_GROUP_SUCCESS = "스터디 그룹이 생성되었습니다.";
    private final String MSG_FETCH_GROUP_LIST_SUCCESS = "스터디 그룹 목록을 조회했습니다.";
    private final String MSG_EDIT_GROUP_SUCCESS = "스터디 그룹이 수정되었습니다.";

    @PostMapping
    public ResponseEntity<StudyGroupAPIResponse<CreateStudyGroupResponseDto>> createStudyGroup(
            @RequestBody CreateStudyGroupRequestDto requestDto
    ) {
        CreateStudyGroupRequestInfo requestInfo = CreateStudyGroupRequestInfo.from(requestDto);
        CreateStudyGroupResponseInfo responseInfo = studyGroupService.createStudyGroup(requestInfo);
        CreateStudyGroupResponseDto responseDto = CreateStudyGroupResponseDto.from(responseInfo);
        StudyGroupAPIResponse<CreateStudyGroupResponseDto> response = new StudyGroupAPIResponse<>(MSG_CREATE_GROUP_SUCCESS, responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<StudyGroupAPIResponse<GetStudyGroupListResponseDto>> fetchStudyGropList(
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "size",       defaultValue = "10") int size,
            @RequestParam(name = "name",       required = false) String name,
            @RequestParam(name = "keyword",    required = false) List<String> keywords,
            @RequestParam(name = "accessType", required = false) String accessType
    ) {
        GetStudyGroupListRequestInfo requestInfo = GetStudyGroupListRequestInfo.builder()
                .pageable(PageRequest.of(pageNumber - 1, size))
                .name(name)
                .keywords(keywords)
                .accessType(accessType)
                .build();
        GetStudyGroupListResponseInfo responseInfo = studyGroupService.fetchStudyGroupList(requestInfo);
        GetStudyGroupListResponseDto responseDto = GetStudyGroupListResponseDto.from(responseInfo);
        StudyGroupAPIResponse<GetStudyGroupListResponseDto> response = new StudyGroupAPIResponse<>(MSG_FETCH_GROUP_LIST_SUCCESS, responseDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<StudyGroupAPIResponse<EditStudyGroupResponseDto>> updateStudyGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody EditStudyGroupRequestDto requestDto
    ) {
        EditStudyGroupRequestInfo requestInfo = EditStudyGroupRequestInfo.from(groupId, requestDto);
        EditStudyGroupResponseInfo responseInfo = studyGroupService.editStudyGroup(requestInfo);
        EditStudyGroupResponseDto responseDto = EditStudyGroupResponseDto.from(responseInfo);
        StudyGroupAPIResponse<EditStudyGroupResponseDto> response = new StudyGroupAPIResponse<>(MSG_EDIT_GROUP_SUCCESS, responseDto);
        return ResponseEntity.ok(response);
    }

}