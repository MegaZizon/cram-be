package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardEditResponse {
    private String message; // 응답 메시지
    private BoardData data; // 수정된 게시판 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BoardData {
        private Long id; // 게시판 ID
        private String type; // 게시판 타입 (예: "소통")
        private String name; // 수정된 게시판 이름
        private String description; // 수정된 게시판 설명
    }
}