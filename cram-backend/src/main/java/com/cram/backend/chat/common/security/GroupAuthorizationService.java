package com.cram.backend.chat.common.security;

import com.cram.backend.groupmember.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("groupPermissionEvaluator")
@RequiredArgsConstructor
public class GroupAuthorizationService {

    private final GroupMemberRepository groupMemberRepository;

    public boolean hasAccess(Long groupId, Long userId) {
        return groupMemberRepository.existsByUserIdAndStudyGroupId(userId, groupId);
    }
}
