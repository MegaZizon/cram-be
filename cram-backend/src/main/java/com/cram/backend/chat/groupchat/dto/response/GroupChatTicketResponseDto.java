package com.cram.backend.chat.groupchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatTicketResponseDto {
    private String message;
    private GroupChatTicketData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupChatTicketData {
        private String groupChatTicket;
    }
}