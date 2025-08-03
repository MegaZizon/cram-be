package com.cram.backend.chat.meetingroom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomChatMessageRequestDto {
    private String userId;
    private String userName;
    private Long groupId;
    private String type;     // ex) "text", "image"
    private String message;
    private String image;
    private Long meetingRoomId;
}
