package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BoardDeleteResponse {
    private String message; // 응답 메시지
    private BoardData data; // 삭제된 게시판 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BoardData {
        private Long id; // 게시판 ID
        private String type; // 게시판 타입 (예: "소통")
        private String name; // 게시판 이름
        private LocalDateTime deletedAt; // 삭제 시간
    }
}