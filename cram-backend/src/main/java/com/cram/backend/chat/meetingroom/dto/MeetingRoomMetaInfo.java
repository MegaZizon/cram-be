package com.cram.backend.chat.meetingroom.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomMetaInfo {
    private Long roomId;
    private String title;
    private String managerId;
    private String managerName;
    private int members;
    private int maxMembers;
    private boolean isActive;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

}

