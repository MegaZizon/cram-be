package com.cram.backend.joingroup.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyGroupRuntimeException extends RuntimeException {
    private final StudyGroupErrorInfo studyGroupErrorInfo;
}
