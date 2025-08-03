package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardDeleteRequest {
    private Long groupId; // 그룹 ID
    private Long boardId; // 삭제할 게시판 ID
}