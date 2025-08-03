package com.cram.backend.chat.meetingroom.repository;

import com.cram.backend.chat.meetingroom.entity.MeetingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRoomRepository  extends JpaRepository<MeetingRoom,Long> {
    Page<MeetingRoom> findByStudyGroupId(Long groupId, Pageable pageable);

    Page<MeetingRoom> findByStudyGroupIdAndIsActive(Long groupId, boolean b, Pageable pageable);

    List<MeetingRoom> findAllByIsActiveTrue();
}
