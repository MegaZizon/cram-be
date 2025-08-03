package com.cram.backend.subscriber;

import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import com.cram.backend.chat.groupchat.service.GroupChatService;
import com.cram.backend.chat.meetingroom.service.MeetingRoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BackendMqSubscriber {

    private final ObjectMapper objectMapper;
    private final GroupChatService groupChatService;
    private final MeetingRoomService meetingRoomService;

    @RabbitListener(queues = "group.chat.db.queue")
    public void receiveGroupChat(ChatMessageInternalDto dto) {
        try {
            log.info("[MQ]Received group chat message: {}", objectMapper.writeValueAsString(dto));
            groupChatService.saveGroupChatMessage(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "meeting.room.chat.db.queue")
    public void receiveMeetingRoomChat(ChatMessageInternalDto dto) {
        try {
            log.info("[MQ]Received group chat message: {}", objectMapper.writeValueAsString(dto));
            meetingRoomService.saveMeetingRoomChatMessage(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "meeting.room.inactive.queue")
    public void reciveMeetingRoomInActive(String str) {
        try {
            log.info("[MQ]Received group chat message: {}", objectMapper.writeValueAsString(str));
            meetingRoomService.inActiveMeetingRoom(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "meeting.room.active.queue")
    public void reciveMeetingRoomActive(String str) {
        try {
            log.info("[MQ]Received group chat message: {}", objectMapper.writeValueAsString(str));
            meetingRoomService.activeMeetingRoom(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
