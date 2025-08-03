package com.cram.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetMyGroupListResponseDto {
    private List<GroupInfo> groups;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class GroupInfo {
        private Long groupId;
        private String name;
        private String description;
        private int currentMembers;
        private String visibility;
        private List<String> tags;
        private String thumbnailUrl;
    }
}