package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import com.cram.backend.chat.meetingroom.dto.response.GetMeetingRoomResponse;
import com.cram.backend.chat.meetingroom.dto.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface MeetingRoomService {
    public void saveMeetingRoomChatMessage(ChatMessageInternalDto meetingRoomChatMessageRequestDto) throws JsonProcessingException;

    Long createMeetingRoom(String title, Long groupId);

    ResponseEntity<MeetingRoomTicketResponseDto> issueMeetingRoomSocketTicket(Long meetingRoomId, Long groupId);

    GetMeetingRoomResponse selectMeetingRoomMetaInfoList(Long groupId, int page, int size, int isActivate);

    ResponseEntity<MeetingRoomFileUploadResponseDto> uploadFile(List<MultipartFile> file, Long groupId, Long meetingRoomId, String origin);

//    ResponseEntity<MeetingRoomExistenceResponseDto> isMeetingRoomExist(Long meetingRoomId);

    ResponseEntity<MeetingRoomChatHistoryResponseDto> searchMeetingRoomChatMessageHistoryByGroupId(Long meetingRoomId, Long before, Long limit);

    ResponseEntity<MeetingRoomMemberResponseDto> searchMeetingRoomMemberInfo(Long meetingRoomId, Long groupId);

    void inActiveMeetingRoom(String str);

    void activeMeetingRoom(String str);
}
