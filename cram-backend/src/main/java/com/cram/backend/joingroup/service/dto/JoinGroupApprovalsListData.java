package com.cram.backend.joingroup.service.dto;

import com.cram.backend.joingroup.dto.PageableInfo;
import com.cram.backend.joingroup.entity.GroupJoinRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinGroupApprovalsListData {
    private List<JoinGroupRequestSummary> content;
    private PageableInfo pageable;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JoinGroupRequestSummary {
        private Long requestId;
        private Long userId;
        private String nickname;
        private String greetingMessage;
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss",
                timezone = "Asia/Seoul"
        )
        private LocalDateTime requestedAt;

        @Builder
        public JoinGroupRequestSummary(Long requestId, Long userId, String nickname, String greetingMessage, LocalDateTime requestedAt) {
            this.requestId = requestId;
            this.userId = userId;
            this.nickname = nickname;
            this.greetingMessage = greetingMessage;
            this.requestedAt = requestedAt;
        }

        public static JoinGroupRequestSummary from(GroupJoinRequest entity) {
            return new JoinGroupRequestSummary(
                    entity.getId(),
                    entity.getUser().getId(),
                    entity.getNickname(),
                    entity.getGreeting(),
                    entity.getRequestedAt()
            );
        }
    }

    @Builder
    public JoinGroupApprovalsListData(List<JoinGroupRequestSummary> content, PageableInfo pageable) {
        this.content = content;
        this.pageable = pageable;
    }
}
