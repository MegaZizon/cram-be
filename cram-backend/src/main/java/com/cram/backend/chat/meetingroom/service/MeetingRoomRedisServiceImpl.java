package com.cram.backend.chat.meetingroom.service;

import com.cram.backend.chat.meetingroom.dto.MeetingRoomInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 미팅룸 Redis 공통 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomRedisServiceImpl implements MeetingRoomRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveMeetingRoomInfo(Long meetingRoomId, MeetingRoomInfo info) {
        try {
            String key = "meetingRoom:" + meetingRoomId;
            String value = objectMapper.writeValueAsString(info);
            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            log.error("Failed to save meeting room info to Redis: {}", meetingRoomId, e);
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    @Override
    public MeetingRoomInfo getMeetingRoomInfo(Long meetingRoomId) {
        try {
            String key = "meetingRoom:" + meetingRoomId;
            String json = redisTemplate.opsForValue().get(key);
            
            if (json == null) {
                return null;
            }
            
            return objectMapper.readValue(json, MeetingRoomInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to get meeting room info from Redis: {}", meetingRoomId, e);
            return null;
        }
    }

    @Override
    public int getParticipantCount(Long meetingRoomId) {
        MeetingRoomInfo info = getMeetingRoomInfo(meetingRoomId);
        if (info == null || info.getMembers() == null) {
            return 0;
        }
        return info.getMembers().size();
    }
    
    @Override
    public Map<Long, Integer> getParticipantCounts(List<Long> meetingRoomIds) {
        Map<Long, Integer> result = new HashMap<>();
        
        if (meetingRoomIds.isEmpty()) {
            return result;
        }
        
        // 1. Redis 키 생성
        List<String> keys = meetingRoomIds.stream()
                .map(id -> "meetingRoom:" + id)
                .toList();
        
        // 2. multiGet으로 일괄 조회
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        
        // 3. 각 미팅룸의 참가자 수 계산
        for (int i = 0; i < meetingRoomIds.size(); i++) {
            Long roomId = meetingRoomIds.get(i);
            String json = (values != null && i < values.size()) ? values.get(i) : null;
            
            int count = 0;
            if (json != null) {
                try {
                    MeetingRoomInfo info = objectMapper.readValue(json, MeetingRoomInfo.class);
                    count = (info.getMembers() != null) ? info.getMembers().size() : 0;
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse meeting room info for room {}", roomId, e);
                }
            }
            
            result.put(roomId, count);
        }
        
        return result;
    }
}