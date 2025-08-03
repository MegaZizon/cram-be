package com.cram.backend.chat.meetingroom.repository;

import com.cram.backend.chat.meetingroom.entity.MeetingRoomChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingRoomChatMessageRepository extends JpaRepository<MeetingRoomChatMessage,Long> {
    List<MeetingRoomChatMessage> findByMeetingRoomIdOrderByIdDesc(Long meetingRoomId, Pageable pageable);

    List<MeetingRoomChatMessage> findByMeetingRoomIdAndIdLessThanOrderByIdDesc(Long meetingRoomId, Long before, Pageable pageable);

    @Query("SELECT mrm.id FROM MeetingRoomChatMessage mrm " +
           "WHERE mrm.meetingRoom.id = :meetingRoomId " +
           "ORDER BY mrm.id DESC")
    List<Long> findMessageIdsByMeetingRoomId(@Param("meetingRoomId") Long meetingRoomId, Pageable pageable);

    @Query("SELECT mrm.id FROM MeetingRoomChatMessage mrm " +
           "WHERE mrm.meetingRoom.id = :meetingRoomId AND mrm.id < :before " +
           "ORDER BY mrm.id DESC")
    List<Long> findMessageIdsByMeetingRoomIdAndIdLessThan(@Param("meetingRoomId") Long meetingRoomId, @Param("before") Long before, Pageable pageable);

    @Query("SELECT mrm FROM MeetingRoomChatMessage mrm " +
           "JOIN FETCH mrm.user " +
           "LEFT JOIN FETCH mrm.attachments " +
           "WHERE mrm.id IN :messageIds " +
           "ORDER BY mrm.id DESC")
    List<MeetingRoomChatMessage> findByIdsWithUserAndAttachments(@Param("messageIds") List<Long> messageIds);
}
