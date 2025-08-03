package com.cram.backend.joingroup.dto;

import com.cram.backend.joingroup.service.dto.JoinGroupRequestCreatedInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class JoinGroupResponseDto {
    private final String message = "그룹 가입 신청이 완료되었습니다.";
    private JoinGroupResponseData data;

    public static JoinGroupResponseDto from(JoinGroupRequestCreatedInfo info) {
        return new JoinGroupResponseDto(
                new JoinGroupResponseData(
                        info.getRequestId(),
                        info.getStatus(),
                        info.getRequestedAt()
                )
        );
    }

    @Getter
    @AllArgsConstructor
    public static class JoinGroupResponseData {
        private Long requestId;
        private String status;

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss",
                timezone = "Asia/Seoul"
        )
        private LocalDateTime requestedAt;
    }
}