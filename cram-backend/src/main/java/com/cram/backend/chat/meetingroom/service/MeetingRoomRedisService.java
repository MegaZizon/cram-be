package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.meetingroom.dto.MeetingRoomInfo;

import java.util.List;
import java.util.Map;

/**
 * 미팅룸 Redis 공통 서비스
 */
public interface MeetingRoomRedisService {
    
    /**
     * 미팅룸 정보 저장
     */
    void saveMeetingRoomInfo(Long meetingRoomId, MeetingRoomInfo info);
    
    /**
     * 미팅룸 정보 조회
     */
    MeetingRoomInfo getMeetingRoomInfo(Long meetingRoomId);
    
    /**
     * 미팅룸 참가자 수 조회
     */
    int getParticipantCount(Long meetingRoomId);
    
    /**
     * 여러 미팅룸의 참가자 수 일괄 조회 (성능 최적화)
     */
    Map<Long, Integer> getParticipantCounts(List<Long> meetingRoomIds);
}