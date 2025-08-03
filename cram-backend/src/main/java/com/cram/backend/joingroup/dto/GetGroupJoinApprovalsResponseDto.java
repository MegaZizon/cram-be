package com.cram.backend.joingroup.dto;

import com.cram.backend.joingroup.service.dto.JoinGroupApprovalsListData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetGroupJoinApprovalsResponseDto {
    private final String message = "스터디 그룹 게시판 목록을 조회했습니다.";
    private JoinGroupApprovalsListData data;
}
