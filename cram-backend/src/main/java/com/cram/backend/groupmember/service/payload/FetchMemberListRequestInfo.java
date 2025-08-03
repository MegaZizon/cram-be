package com.cram.backend.groupmember.service.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FetchMemberListRequestInfo {
    private Long groupId;
    private Optional<String> nickname;
    private Pageable pageable;

    @Builder
    public FetchMemberListRequestInfo(Long groupId, Optional<String> nickname, Pageable pageable) {
        this.groupId = groupId;
        this.nickname = nickname;
        this.pageable = pageable;
    }
}
