package com.cram.backend.chat.meetingroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomExistenceResponseDto {
    private String status;
    private LocalDateTime endedAt;
    private Long managerId;
    private String message;

    public static MeetingRoomExistenceResponseDto success(LocalDateTime endedAt, Long managerId) {
        return MeetingRoomExistenceResponseDto.builder()
                .status("success")
                .endedAt(endedAt)
                .managerId(managerId)
                .build();
    }

    public static MeetingRoomExistenceResponseDto failure(String message) {
        return MeetingRoomExistenceResponseDto.builder()
                .status("fail")
                .message(message)
                .build();
    }
}