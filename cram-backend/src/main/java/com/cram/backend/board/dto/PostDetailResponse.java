package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostDetailResponse {
    private String message; // 응답 메시지
    private PostDetailData data; // 게시글 상세 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostDetailData {
        private BoardInfo board; // 게시판 정보
        private AuthorInfo author; // 작성자 정보
        private Long postId; // 게시글 ID
        private String title; // 게시글 제목
        private String boardName; // 게시판 이름
        private LocalDateTime createdAt; // 작성일시
        private Integer likeCount; // 좋아요 수
        private Integer commentCount; // 댓글 수
        private Integer viewCount; // 조회수
        private Boolean isLiked; // 좋아요 눌렀는지 여부
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BoardInfo {
        private Long id; // 게시판 ID
        private String name; // 게시판 이름
        private String type; // 게시판 타입 (예: "학습")
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AuthorInfo {
        private Long userId; // 작성자 사용자 ID
        private String username; // 작성자 이름
        private String profileImage; // 프로필 이미지 URL
    }
}