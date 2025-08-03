package com.cram.backend.studygroup.entity;

import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;

import java.util.Arrays;

import static com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo.NOT_VALID_STUDY_GROUP_ACCESS_TYPE;

public enum StudyGroupAccessType {
    PUBLIC, PRIVATE, HIDDEN;

    public static StudyGroupAccessType from(String raw) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new StudyGroupRuntimeException(NOT_VALID_STUDY_GROUP_ACCESS_TYPE));
    }
}
