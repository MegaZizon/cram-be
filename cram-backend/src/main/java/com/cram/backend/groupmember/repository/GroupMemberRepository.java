package com.cram.backend.groupmember.repository;

import com.cram.backend.groupmember.entity.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByUser_IdAndStudyGroup_IdAndIsActiveTrue(Long userId, Long studyGroupId);
    boolean existsByStudyGroup_IdAndNickname(Long studyGroupId, String nickname);
    Optional<GroupMember> findByUser_IdAndStudyGroup_IdAndIsActiveTrue(Long userId, Long studyGroupId);
    Page<GroupMember> findByStudyGroup_IdAndIsActiveTrue(Long groupId, Pageable pageable);
    Page<GroupMember> findByStudyGroup_IdAndIsActiveTrueAndNicknameContainingIgnoreCase(
            Long groupId, String nickname, Pageable pageable
    );
    Integer countMembersByStudyGroupIdAndIsActiveTrue(Long groupId);

    boolean existsByUserIdAndStudyGroupId(Long userId, Long groupId);
}
