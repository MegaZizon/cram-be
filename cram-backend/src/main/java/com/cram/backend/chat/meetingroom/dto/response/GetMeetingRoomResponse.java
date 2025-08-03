package com.cram.backend.chat.meetingroom.dto.response;

import com.cram.backend.chat.meetingroom.dto.MeetingRoomMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class GetMeetingRoomResponse {
    private List<MeetingRoomMetaInfo> content;
    private PageableInfo pageable;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageableInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
