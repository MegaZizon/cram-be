package com.cram.backend.groupmember.service.payload;

import com.cram.backend.groupmember.entity.GroupMember;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FetchMemberResponseInfo {
    private Long memberId;
    private String nickname;
    private LocalDateTime joinedAt;

    @Builder
    public FetchMemberResponseInfo(Long memberId, String nickname, LocalDateTime joinedAt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinedAt = joinedAt;
    }

    public static FetchMemberResponseInfo from(GroupMember member) {
        return FetchMemberResponseInfo.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
