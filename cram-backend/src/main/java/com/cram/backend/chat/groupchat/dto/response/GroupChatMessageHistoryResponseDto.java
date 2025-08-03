package com.cram.backend.chat.groupchat.dto.response;

import com.cram.backend.chat.common.dto.ChatMessageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatMessageHistoryResponseDto {
    private List<ChatMessageResponseDto> messages;
    private Long nextCursor;
    private boolean hasMore;
}