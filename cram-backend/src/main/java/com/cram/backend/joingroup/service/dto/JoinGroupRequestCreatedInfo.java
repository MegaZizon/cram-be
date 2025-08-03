package com.cram.backend.joingroup.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class JoinGroupRequestCreatedInfo {
    private Long requestId;
    private String status;
    private LocalDateTime requestedAt;
}
