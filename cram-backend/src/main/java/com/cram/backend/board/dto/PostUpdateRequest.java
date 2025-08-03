package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostUpdateRequest {
    private String title; // 게시글 제목 (선택)
    private String content; // 게시글 내용 (선택)
    private List<String> tags; // 태그 (선택)
    private Boolean isAnonymous; // 익명 게시 여부 (선택)
}