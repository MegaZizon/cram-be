package com.cram.backend.chat.meetingroom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingRoomRuntimeException extends RuntimeException {
    private final MeetingRoomErrorInfo meetingRoomErrorInfo;
}