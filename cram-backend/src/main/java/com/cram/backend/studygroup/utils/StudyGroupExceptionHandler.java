package com.cram.backend.studygroup.utils;

import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class StudyGroupExceptionHandler {

    @ExceptionHandler(StudyGroupRuntimeException.class)
    private ResponseEntity<Map<String, String>> makeErrorResponse(StudyGroupRuntimeException error) {
        System.out.println("error ocurred" + error.getStudyGroupErrorInfo().getErrorMessage());
        StudyGroupErrorInfo errorInfo = error.getStudyGroupErrorInfo();
        int statusCode = errorInfo.getErrorCode();
        String message = errorInfo.getErrorMessage();
        return ResponseEntity.status(statusCode).body(Map.of("message", message));
    }

}
