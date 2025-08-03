package com.cram.backend.studygroup.service.payload;

import com.cram.backend.studygroup.dto.CreateStudyGroupRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateStudyGroupRequestInfo {
    private Long leaderId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;

    public static CreateStudyGroupRequestInfo from(CreateStudyGroupRequestDto dto) {
        return CreateStudyGroupRequestInfo.builder()
                .leaderId(dto.getLeaderId())
                .name(dto.getName())
                .description(dto.getDescription())
                .tags(dto.getTags())
                .memberLimit(dto.getMemberLimit())
                .accessType(dto.getAccessType())
                .guidelines(dto.getGuidelines())
                .build();
    }
}
