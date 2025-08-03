package com.cram.backend.chat.meetingroom.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeetingRoomInfo {
    String managerId;
    List<MeetingRoomCallMemberMetaDto> members;
}
