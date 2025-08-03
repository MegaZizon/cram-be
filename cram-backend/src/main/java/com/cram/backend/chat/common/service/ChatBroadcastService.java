package com.cram.backend.chat.common.service;

import com.cram.backend.chat.common.dto.BroadCastChatMessageResponseDto;
import com.cram.backend.chat.common.dto.ChatMessageInternalDto;

/**
 * 채팅 브로드캐스트 공통 서비스
 */
public interface ChatBroadcastService {
    
    /**
     * 파일 업로드 브로드캐스트 메시지 발송
     */
    void sendFileUploadBroadcast(BroadCastChatMessageResponseDto message, String routingKey);
    
    /**
     * 메시지 ID 업데이트 브로드캐스트 발송
     */
    void sendMessageIdUpdate(ChatMessageInternalDto message, String routingKey);
}