package com.cram.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class StudyGroupBoardListResponse {
    private String message; // 응답 메시지
    private StudyGroupData data; // 실제 데이터

    @Getter
    @AllArgsConstructor
    @Builder
    public static class StudyGroupData {
        private GroupMetaInfo groupMetaInfo; // 그룹 메타 정보
        private List<CommunityBoardInfo> communityBoard; // 커뮤니티 게시판 목록
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class GroupMetaInfo {
        private String name; // 그룹 이름
        private Integer currentMembers; // 현재 멤버 수
        private String description; // 그룹 설명
        private String thumbnailUrl; // 썸네일 URL
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CommunityBoardInfo {
        private String name; // 게시판 이름
        private String url; // 게시판 URL
    }
}