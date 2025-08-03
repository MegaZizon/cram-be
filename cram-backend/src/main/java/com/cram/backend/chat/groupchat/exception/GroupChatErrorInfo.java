package com.cram.backend.chat.groupchat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupChatErrorInfo {
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    GROUP_NOT_FOUND(404, "해당 그룹을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(404, "해당 메시지를 찾을 수 없습니다."),
    GROUP_ACCESS_DENIED(403, "해당 그룹에 접근 권한이 없습니다."),
    INVALID_FILE_FORMAT(400, "지원하지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다."),
    DIRECTORY_CREATION_FAILED(500, "디렉토리 생성에 실패했습니다."),
    INVALID_FILE(400, "잘못된 파일입니다."),
    IMAGE_FILE_ONLY(400, "이미지 파일만 업로드 가능합니다.");

    private final int errorCode;
    private final String errorMessage;
}