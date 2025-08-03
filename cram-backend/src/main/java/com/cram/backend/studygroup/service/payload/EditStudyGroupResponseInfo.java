package com.cram.backend.studygroup.service.payload;

import com.cram.backend.studygroup.entity.StudyGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class EditStudyGroupResponseInfo {
    private Long groupId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;
    private LocalDateTime updatedAt;

    public static EditStudyGroupResponseInfo from(StudyGroup studyGroup, List<String> tags) {
        return EditStudyGroupResponseInfo.builder()
                .groupId(studyGroup.getId())
                .name(studyGroup.getName())
                .description(studyGroup.getDescription())
                .tags(tags)
                .memberLimit(studyGroup.getMemberLimit())
                .accessType(studyGroup.getAccessType().name())
                .guidelines(studyGroup.getNotice())
                .updatedAt(studyGroup.getUpdatedAt())
                .build();
    }
}
