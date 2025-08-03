package com.cram.backend.studygroup.service.payload;

import com.cram.backend.studygroup.dto.EditStudyGroupRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EditStudyGroupRequestInfo {
    private Long groupId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;

    public static EditStudyGroupRequestInfo from(Long groupId, EditStudyGroupRequestDto requestDto) {
        return EditStudyGroupRequestInfo.builder()
                .groupId(groupId)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .tags(requestDto.getTags())
                .memberLimit(requestDto.getMemberLimit())
                .accessType(requestDto.getAccessType())
                .guidelines(requestDto.getGuidelines())
                .build();
    }
}
