package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostCreateResponse {
    private Long id; // 게시글 ID
    private Long categoryId; // 게시판 카테고리 ID
    private Long boardId; // 게시판 ID
    private Long userId; // 작성자 사용자 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private LocalDateTime createdAt; // 게시글 작성일시
    private Boolean isDeleted; // 삭제 여부
    private Integer viewCount; // 조회수
    private List<AttachedFileInfo> attachedFiles; // 첨부파일 목록 (선택 사항)

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AttachedFileInfo {
        private Long fileId; // 첨부 파일 ID
        private String filePath; // 파일 저장된 경로
        private String originalFileName; // 원본 파일 이름
        private String uuidFileName; // UUID로 변환된 파일 이름
    }
}