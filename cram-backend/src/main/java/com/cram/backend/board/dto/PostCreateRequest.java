package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostCreateRequest {
    private Long boardCategoryId; // 게시판 카테고리 ID (필수)
    private Long boardId; // 게시판 ID (필수)
    private Long userId; // 사용자 ID (필수)
    private String title; // 게시글 제목 (필수)
    private String content; // 게시글 내용 (필수)
    private Boolean isDeleted; // 삭제 여부 (선택, 기본값 false)
    private Integer viewCount; // 조회수 (선택, 기본값 0)
    private List<String> tags; // 태그 목록 (선택)
    private List<AttachmentInfo> attachmentList; // 첨부파일 목록 (선택)

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AttachmentInfo {
        private String originalFileName; // 원본 파일 이름 (선택)
        private String savedFilePath; // 저장된 경로 (선택)
        private String uuidFileName; // UUID로 저장된 파일 이름 (선택)
    }
}