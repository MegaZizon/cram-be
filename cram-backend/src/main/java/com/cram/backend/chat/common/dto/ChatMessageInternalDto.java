package com.cram.backend.chat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageInternalDto {
    String type;
    String content;
    LocalDateTime createdAt;
    String studyGroupId;
    String userId;
    String meetingRoomId;
    String tempId;
    String realId;
}