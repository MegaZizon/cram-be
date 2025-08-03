package com.cram.backend.chat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long messageId;
    private Long chatroomId;
    private String senderId;
    private String senderName;
    private String senderProfileUrl;
    private String messageType; // "text" | "image"
    private String message;
    private List<String> image;
    private LocalDateTime createdAt;
}