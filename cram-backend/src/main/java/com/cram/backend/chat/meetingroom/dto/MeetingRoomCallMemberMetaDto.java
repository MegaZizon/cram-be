package com.cram.backend.chat.meetingroom.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MeetingRoomCallMemberMetaDto {
    String userId;
    String userName;
    boolean isManager;
    boolean isMicOn;
    boolean isMute;
    boolean isCameraOn;
    boolean isScreenShareOn;
}
