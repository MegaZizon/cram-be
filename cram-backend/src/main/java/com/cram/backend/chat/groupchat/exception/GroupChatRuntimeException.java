package com.cram.backend.chat.groupchat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupChatRuntimeException extends RuntimeException {
    private final GroupChatErrorInfo groupChatErrorInfo;
}