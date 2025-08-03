package com.cram.backend.chat.common.service;

import com.cram.backend.chat.common.dto.BroadCastChatMessageResponseDto;
import com.cram.backend.chat.common.dto.ChatMessageInternalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 채팅 브로드캐스트 공통 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBroadcastServiceImpl implements ChatBroadcastService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendFileUploadBroadcast(BroadCastChatMessageResponseDto message, String routingKey) {
        rabbitTemplate.convertAndSend("chat.direct", routingKey, message);
    }

    @Override
    public void sendMessageIdUpdate(ChatMessageInternalDto message, String routingKey) {
        rabbitTemplate.convertAndSend("chat.direct", routingKey, message);
    }
}