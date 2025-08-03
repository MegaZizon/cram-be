package com.cram.backend.chat.meetingroom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingRoomErrorInfo {
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    MEETING_ROOM_NOT_FOUND(404, "해당 미팅룸을 찾을 수 없습니다."),
    GROUP_NOT_FOUND(404, "해당 그룹을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(404, "해당 메시지를 찾을 수 없습니다."),
    MEETING_ROOM_ACCESS_DENIED(403, "해당 미팅룸에 접근 권한이 없습니다."),
    MEETING_ROOM_ALREADY_ENDED(400, "이미 종료된 미팅룸입니다."),
    INVALID_MEETING_ROOM_STATE(400, "유효하지 않은 미팅룸 상태입니다."),
    REDIS_PROCESSING_ERROR(500, "Redis 처리 중 오류가 발생했습니다."),
    JSON_PROCESSING_ERROR(500, "JSON 처리 중 오류가 발생했습니다.");

    private final int errorCode;
    private final String errorMessage;
}