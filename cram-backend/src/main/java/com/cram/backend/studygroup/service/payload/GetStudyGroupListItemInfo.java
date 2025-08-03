package com.cram.backend.studygroup.service.payload;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyGroupListItemInfo {
    private Long groupId;
    private String name;
    private String description;
    private Integer memberCount;
    private Integer postCount;
    private String accessType;
    private List<String> tags;
    private String thumbnailUrl;
}
