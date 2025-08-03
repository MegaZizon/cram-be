package com.cram.backend.chat.meetingroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomTicketResponseDto {
    private String message;
    private MeetingRoomTicketData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MeetingRoomTicketData {
        private String meetingRoomTicket;
        private Long meetingRoomId;
    }
}