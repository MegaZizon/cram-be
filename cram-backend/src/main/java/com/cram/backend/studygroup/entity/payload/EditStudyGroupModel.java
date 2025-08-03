package com.cram.backend.studygroup.entity.payload;

import com.cram.backend.studygroup.entity.StudyGroupAccessType;
import com.cram.backend.studygroup.service.payload.EditStudyGroupRequestInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class EditStudyGroupModel {
    private String name;
    private String description;
    private Integer memberLimit;
    private StudyGroupAccessType accessType;
    private String notice;
    private LocalDateTime updatedAt;

    public static EditStudyGroupModel from(EditStudyGroupRequestInfo requestInfo, LocalDateTime updatedAt) {
        return EditStudyGroupModel.builder()
                .name(requestInfo.getName())
                .description(requestInfo.getDescription())
                .memberLimit(requestInfo.getMemberLimit())
                .accessType(StudyGroupAccessType.from(requestInfo.getAccessType()))
                .notice(requestInfo.getGuidelines())
                .updatedAt(updatedAt)
                .build();
    }
}
