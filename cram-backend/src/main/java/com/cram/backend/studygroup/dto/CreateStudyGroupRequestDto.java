package com.cram.backend.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateStudyGroupRequestDto {
    private Long leaderId;
    private String name;
    private String description;
    private List<String> tags;
    private Integer memberLimit;
    private String accessType;
    private String guidelines;
}