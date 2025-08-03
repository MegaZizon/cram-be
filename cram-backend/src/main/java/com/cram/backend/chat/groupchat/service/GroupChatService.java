package com.cram.backend.chat.groupchat.service;

import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatFileUploadResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatMessageHistoryResponseDto;
import com.cram.backend.chat.groupchat.dto.response.GroupChatTicketResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupChatService {
    void saveGroupChatMessage(ChatMessageInternalDto chatMessageInternalDto) throws JsonProcessingException;

    ResponseEntity<GroupChatMessageHistoryResponseDto> searchGroupChatMessageHistoryByGroupId(Long groupId, Long before, Long limit);

    ResponseEntity<GroupChatFileUploadResponseDto> uploadFile(List<MultipartFile> file, Long groupId, String origin);

    ResponseEntity<GroupChatTicketResponseDto> issueGroupSocketTicket(Long groupId);
}