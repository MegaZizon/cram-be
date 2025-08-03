package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardCreateResponse {
    private Long categoryId; // 생성된 게시판 카테고리 ID
    private String categoryName; // 게시판 카테고리 이름
    private String type; // 게시판 카테고리 유형 (ENUM: '학습', '소통')
    private Long boardId; // 생성된 게시판 ID
    private String boardName; // 게시판 이름
    private Long groupId; // 게시판이 속한 스터디 그룹 ID
    private Long categoryIdInBoard; // 게시판 엔티티 내부에 저장된 카테고리 ID
}