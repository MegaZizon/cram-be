package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardEditRequest {
    private String name; // 이명 건의 게시판 (요청 본문)
    private String description; // 수정할 게시판 소개 (요청 본문)
}