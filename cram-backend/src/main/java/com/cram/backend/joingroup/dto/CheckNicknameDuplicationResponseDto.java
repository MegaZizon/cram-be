package com.cram.backend.joingroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckNicknameDuplicationResponseDto {
    private boolean available;
    private String message;
}
