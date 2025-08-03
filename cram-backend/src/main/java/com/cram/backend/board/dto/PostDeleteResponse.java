package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostDeleteResponse {
    private Integer status; // HTTP 상태 코드
    private String message; // 응답 메시지
    private PostDeleteData data; // 삭제된 게시글 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostDeleteData {
        private Long postId; // 삭제된 게시글 ID
    }
}