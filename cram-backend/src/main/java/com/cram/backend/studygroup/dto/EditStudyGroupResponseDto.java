package com.cram.backend.studygroup.dto;

import com.cram.backend.studygroup.service.payload.EditStudyGroupResponseInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class EditStudyGroupResponseDto {
    private Long groupId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    private LocalDateTime updatedAt;

    public static EditStudyGroupResponseDto from(EditStudyGroupResponseInfo responseInfo) {
        return EditStudyGroupResponseDto.builder()
                .groupId(responseInfo.getGroupId())
                .name(responseInfo.getName())
                .description(responseInfo.getDescription())
                .tags(responseInfo.getTags())
                .memberLimit(responseInfo.getMemberLimit())
                .accessType(responseInfo.getAccessType())
                .guidelines(responseInfo.getGuidelines())
                .updatedAt(responseInfo.getUpdatedAt())
                .build();
    }
}
