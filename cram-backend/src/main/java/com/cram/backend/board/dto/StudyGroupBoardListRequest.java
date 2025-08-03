package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StudyGroupBoardListRequest {
    private Long groupId; // 스터디 그룹 ID
}