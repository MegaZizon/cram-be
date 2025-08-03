package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostDeleteRequest {
    private Long groupId; // 스터디 그룹 ID (필수)
    private Long boardId; // 게시판 ID (필수)
    private Long postId; // 게시글 ID (필수)
}