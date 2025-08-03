package com.cram.backend.chat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BroadCastChatMessageResponseDto {
    private String messageId;
    private String chatroomId; // null이면 그룹채팅, 존재하면 미팅룸 채팅

    private String senderId;
    private String senderName;
    private String senderProfileUrl;

    private String messageType; // "text" | "image"
    private String message;     // messageType이 "text"일 때만 사용
    private List<String> imageUrl;    // messageType이 "image"일 때만 사용

    private String createdAt;   // ISO 8601 문자열 ("2025-05-04T12:01:00Z")
    private String studyGroupId;
}
