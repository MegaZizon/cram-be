package com.cram.backend.joingroup.repository;

import com.cram.backend.joingroup.entity.GroupJoinRequest;
import com.cram.backend.joingroup.entity.GroupJoinStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinGroupRepository extends JpaRepository<GroupJoinRequest, Long> {
    boolean existsByUser_IdAndStudyGroup_Id(Long userId, Long studyGroupId);
    Page<GroupJoinRequest> findByStudyGroup_IdAndStatusOrderByIdDesc(
            Long studyGroupId,
            GroupJoinStatus status,
            Pageable pageable
    );
}