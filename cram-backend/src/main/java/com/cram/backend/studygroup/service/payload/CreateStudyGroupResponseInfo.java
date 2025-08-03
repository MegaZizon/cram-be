package com.cram.backend.studygroup.service.payload;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CreateStudyGroupResponseInfo {
    private Long groupId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer maxMembers;
    private String accessType;
    private String guidelines;
    private LocalDateTime createdAt;
}
