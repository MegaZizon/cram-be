package com.cram.backend.joingroup.service.dto;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.joingroup.entity.GroupJoinRequest;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApproveGroupJoinResponseInfo {
    private Long requestId;
    private Long groupId;
    private String groupName;
    private Long userId;
    private String nickname;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @Builder
    public ApproveGroupJoinResponseInfo(Long requestId, Long groupId, String groupName, Long userId, String nickname, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.requestId = requestId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.userId = userId;
        this.nickname = nickname;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static ApproveGroupJoinResponseInfo from(GroupJoinRequest groupJoinRequest, UserEntity user, StudyGroup group, GroupMember member) {
        return ApproveGroupJoinResponseInfo.builder()
                .requestId(groupJoinRequest.getId())
                .groupId(group.getId())
                .groupName(group.getName())
                .userId(user.getId())
                .nickname(member.getNickname())
                .requestedAt(groupJoinRequest.getRequestedAt())
                .processedAt(member.getJoinedAt())
                .build();
    }
}