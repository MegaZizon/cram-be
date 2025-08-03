package com.cram.backend.joingroup.service;

import com.cram.backend.joingroup.service.dto.*;
import org.springframework.data.domain.Pageable;

public interface JoinGroupService {
    JoinGroupRequestCreatedInfo joinGroup(JoinGroupRequestInfo requestInfo);
    boolean existsNicknameInGroup(Long groupId, String nickname);
    JoinGroupApprovalsListData fetchJoinGroupRequests(Long groupId, Long userId, Pageable pageable);
    ApproveGroupJoinResponseInfo approveGroupJoinRequest(ApproveGroupJoinRequestInfo requestInfo);
    RejectGroupJoinResponseInfo rejectGroupJoinRequest(RejectGroupJoinRequestInfo requestInfo);
}
