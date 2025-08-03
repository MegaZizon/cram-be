package com.cram.backend.groupmember.repository;

import com.cram.backend.groupmember.entity.GroupOutRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupOutRequestRepository extends JpaRepository<GroupOutRequest, Long> {
    boolean existsByMember_Id(Long memberId);
}
