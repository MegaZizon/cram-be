package com.cram.backend.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyGroupAPIResponse<T> {
    private String message;
    private T data;
}