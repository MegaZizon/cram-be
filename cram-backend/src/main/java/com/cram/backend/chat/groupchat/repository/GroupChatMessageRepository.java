package com.cram.backend.chat.groupchat.repository;

import com.cram.backend.chat.groupchat.entity.GroupChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage,Long> {
    List<GroupChatMessage> findAllByStudyGroupId(Long studyGroupId);

    List<GroupChatMessage> findByStudyGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    List<GroupChatMessage> findByStudyGroupIdAndIdLessThanOrderByIdDesc(Long groupId, Long before, Pageable pageable);

    @Query("SELECT gcm.id FROM GroupChatMessage gcm " +
           "WHERE gcm.studyGroup.id = :groupId " +
           "ORDER BY gcm.createdAt DESC")
    List<Long> findMessageIdsByStudyGroupId(@Param("groupId") Long groupId, Pageable pageable);

    @Query("SELECT gcm.id FROM GroupChatMessage gcm " +
           "WHERE gcm.studyGroup.id = :groupId AND gcm.id < :before " +
           "ORDER BY gcm.id DESC")
    List<Long> findMessageIdsByStudyGroupIdAndIdLessThan(@Param("groupId") Long groupId, @Param("before") Long before, Pageable pageable);

    @Query("SELECT gcm FROM GroupChatMessage gcm " +
           "JOIN FETCH gcm.user " +
           "LEFT JOIN FETCH gcm.attachments " +
           "WHERE gcm.id IN :messageIds " +
           "ORDER BY gcm.createdAt DESC")
    List<GroupChatMessage> findByIdsWithUserAndAttachments(@Param("messageIds") List<Long> messageIds);
}
