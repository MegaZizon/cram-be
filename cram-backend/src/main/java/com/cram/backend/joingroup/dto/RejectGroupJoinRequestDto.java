package com.cram.backend.joingroup.dto;

import lombok.Getter;

@Getter
public class RejectGroupJoinRequestDto {
    private Long userId;
    private String reason;
}
