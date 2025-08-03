package com.cram.backend.chat.common.handler;

import com.cram.backend.chat.groupchat.exception.GroupChatRuntimeException;
import com.cram.backend.chat.meetingroom.exception.MeetingRoomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.cram.backend.chat")
@Slf4j
public class ChatExceptionHandler {

    @ExceptionHandler(GroupChatRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGroupChatRuntimeException(GroupChatRuntimeException e) {
        log.error("GroupChatRuntimeException: {}", e.getGroupChatErrorInfo().getErrorMessage());

        int statusCode = e.getGroupChatErrorInfo().getErrorCode();
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getGroupChatErrorInfo().getErrorMessage());
        
        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    @ExceptionHandler(MeetingRoomRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleMeetingRoomRuntimeException(MeetingRoomRuntimeException e) {
        log.error("MeetingRoomRuntimeException: {}", e.getMeetingRoomErrorInfo().getErrorMessage());

        int statusCode = e.getMeetingRoomErrorInfo().getErrorCode();
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMeetingRoomErrorInfo().getErrorMessage());

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(Map.of("error", "권한이 없습니다.", "message", ex.getMessage()));
//    }
}