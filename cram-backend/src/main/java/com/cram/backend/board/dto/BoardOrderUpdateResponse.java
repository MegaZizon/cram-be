package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardOrderUpdateResponse {
    private String message; // 응답 메시지
    private BoardData data; // 순서가 변경된 게시판 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BoardData {
        private Long id; // 게시판 ID
        private String type; // 게시판 타입 (예: "소통")
        private String name; // 게시판 이름
        private Integer order; // 변경된 순서
    }
}