package com.cram.backend.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class EditStudyGroupRequestDto {
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;
}
