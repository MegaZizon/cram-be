package com.cram.backend.runner;

import com.cram.backend.chat.meetingroom.entity.MeetingRoom;
import com.cram.backend.chat.meetingroom.repository.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupCleanupRunner implements ApplicationRunner {

    private final MeetingRoomRepository meetingRoomRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 1. DB 처리
        List<MeetingRoom> activeRooms = meetingRoomRepository.findAllByIsActiveTrue();
        LocalDateTime now = LocalDateTime.now();

        for (MeetingRoom room : activeRooms) {
            room.setIsActive(false);
            room.setEndedAt(now);
        }
        meetingRoomRepository.saveAll(activeRooms);

        // 2. Redis 초기화
        Set<String> keys = redisTemplate.keys("meetingRoom:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // 3. RabbitMQ로 소켓 연결 종료 메시지 전송
        rabbitTemplate.convertAndSend("chat.direct", "meeting.room.clean", "server restart and clean up rooms and redis.");

        log.info("[StartupCleanupRunner] 초기화 완료: {}개 room 종료 및 Redis 초기화", activeRooms.size());
    }
}
